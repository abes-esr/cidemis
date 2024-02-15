package fr.abes.cidemis.ajoutRefus;

import fr.abes.cbs.exception.CBSException;
import fr.abes.cbs.exception.ZoneException;
import fr.abes.cbs.notices.Biblio;
import fr.abes.cbs.notices.Zone;
import fr.abes.cbs.process.ProcessCBS;
import fr.abes.cbs.utilitaire.Constants;
import fr.abes.cbs.utilitaire.Utilitaire;
import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.dao.cidemis.CidemisDaoProvider;
import fr.abes.cidemis.model.cidemis.Commentaires;
import fr.abes.cidemis.model.cidemis.Demandes;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
public class MajSudocTasklet implements Tasklet, StepExecutionListener {
    @Value("${cbs.url}")
    private String serveur;
    @Value("${cbs.port}")
    private String port;
    @Value("${cbs.login}")
    private String login;
    @Value("${cbs.password}")
    private String pass;
    @Autowired
    private CidemisDaoProvider dao;
    private ProcessCBS processCBS;
    private List<Demandes> demandes;
    private String dateNowFormat = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    private String pattern = "(identifiant Cidemis : ";

    @Override
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
        this.demandes = (List<Demandes>) executionContext.get("demandes");
        processCBS = new ProcessCBS();
        try {
            processCBS.authenticate(serveur, port, login, pass);//Récupéré de la step précédente
        } catch (CBSException e) {
            log.error("Impossible de se connecter au Sudoc");
        }
    }

    @SneakyThrows
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        processCBS.disconnect();
        return stepExecution.getExitStatus();
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        String commentaire;
        if (this.demandes.size() > 0) {
            for (Demandes d : this.demandes) {
                commentaire = findLastCommentaireUserCieps(d);
                if (commentaire != null) {
                    if (d.getTypesDemandes().getIdTypeDemande().equals(Constant.TYPE_DEMANDE_NUMEROTATION)) {
                        traitementCbsNUM(d, commentaire);
                    }
                    if (d.getTypesDemandes().getIdTypeDemande().equals(Constant.TYPE_DEMANDE_CORRECTION)) {
                        traitementCBSCOR(d, commentaire);
                    }
                }
            }
        }
        return RepeatStatus.FINISHED;
    }

    /**
     * Récupère le dernier commentaire posté par le cieps correspondant au motif du refus
     *
     * @param demande demande concernée
     * @return le libellé du commentaire trouvé, chaine générique si non trouvé
     */
    private String findLastCommentaireUserCieps(Demandes demande) {
        Optional<Commentaires> comment = dao.getCommentairesDao().findFirstByCbsUsersAndDemandeOrderByDateCommentaireDesc(dao.getCbsUsersDao().findCbsUsersByUserKey("352CI001"), demande);
        if (comment.isPresent()) {
            return comment.get().getLibCommentaire();
        }
        return "Impossible de récupérer le motif de refus";
    }

    /**
     * Formatte un commentaire pour lui rajouter les informations nécessaires à ce qui doit être ajouté dans la zone de la notice
     *
     * @param demande     demande concernée (pour numéro et type de demande)
     * @param commentaire libellé du motif de refus
     * @return chaine correspondant au commentaire définit à ajouter à la zone
     */
    private String getCommentaireString(Demandes demande, String commentaire) {
        StringBuilder commentaireStr = new StringBuilder();
        if (demande.getTypesDemandes().getIdTypeDemande().equals(Constant.TYPE_DEMANDE_NUMEROTATION)) {
            commentaireStr.append("Demande de numérotation refusée par ISSN le ");
        } else if (demande.getTypesDemandes().getIdTypeDemande().equals(Constant.TYPE_DEMANDE_CORRECTION)) {
            commentaireStr.append("Demande de correction refusée par ISSN le ");
        }
        commentaireStr.append(dateNowFormat);
        commentaireStr.append(". Raison refus : ");
        commentaireStr.append(commentaire);
        commentaireStr.append(" (identifiant Cidemis : ");
        commentaireStr.append(demande.getIdDemande());
        commentaireStr.append(")");
        return commentaireStr.toString();
    }

    /**
     * Traite la notice pour le cas d'une demande de correction (suppression 830 existante, ajout nouvelle 830 avec motif de refus)
     *
     * @param demande
     * @param commentaire
     * @throws CBSException
     * @throws ZoneException
     */
    private void traitementCBSCOR(Demandes demande, String commentaire) throws CBSException, ZoneException {
        try {
            Biblio notice = chercherNotice(demande.getNotice().getPpn());
            List<Zone> listeZones = notice.findZoneWithPattern("830", "$a", pattern + demande.getIdDemande());
            if (!listeZones.isEmpty()) {
                notice.deleteZoneWithValue("830", "$a", pattern + demande.getIdDemande());
            }
            notice.addZone("830", "$a", getCommentaireString(demande, commentaire));
            processCBS.modifierNotice("1", notice.toString().substring(1, notice.toString().length() - 1));
        } catch (CBSException ex) {
            log.error("erreur dans l'opération de mise à jour dans le Sudoc : " + ex.getMessage());
        } catch (ZoneException ex) {
            log.error("Erreur dans la construction de la notice : PPN : " + demande.getNotice().getPpn() + " : " + ex.getMessage());
        }

    }

    /**
     * Traite la notice pour le cas d'une demande de numérotation (suppression 301 existante, ajout nouvelle 301 avec motif de refus)
     *
     * @param demande
     * @param commentaire
     * @throws CBSException
     * @throws ZoneException
     */
    private void traitementCbsNUM(Demandes demande, String commentaire) throws CBSException, ZoneException {
        try {
            Biblio notice = chercherNotice(demande.getNotice().getPpn());
            List<Zone> listeZones = notice.findZoneWithPattern("301", "$a", pattern + demande.getIdDemande());
            if (!listeZones.isEmpty()) {
                notice.deleteZoneWithValue("301", "$a", pattern + demande.getIdDemande());
            }
            notice.addZone("301", "$a", getCommentaireString(demande, commentaire));
            processCBS.modifierNotice("1", notice.toString().substring(1, notice.toString().length() - 1));
        } catch (CBSException ex) {
            log.error("erreur dans l'opération de mise à jour dans le Sudoc : " + ex.getMessage());
        } catch (ZoneException ex) {
            log.error("Erreur dans la construction de la notice : PPN : " + demande.getNotice().getPpn() + " : " + ex.getMessage());
        }
    }

    /**
     * Effectue une recherche dans le cbs sur un ppn donné et retourne la notice Biblio correspondante
     *
     * @param ppn
     * @return objet Biblio correspondant à la notice trouvée
     * @throws CBSException
     * @throws ZoneException
     */
    private Biblio chercherNotice(String ppn) throws CBSException, ZoneException {
        processCBS.search("che ppn " + ppn);
        if (processCBS.getNbNotices() != 0) {
            processCBS.affUnma();
            String resu = Constants.STR_1F + Utilitaire.recupEntre(processCBS.editer("1"), Constants.STR_1F, Constants.STR_1E) + Constants.STR_1E;
            return new Biblio(resu);
        }
        throw new CBSException("XX", "Aucune notice ne correspond à la recherche");
    }


}
