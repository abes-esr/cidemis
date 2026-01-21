package fr.abes.cidemis.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.cbs.exception.CBSException;
import fr.abes.cbs.exception.ZoneException;
import fr.abes.cbs.process.ProcessCBS;
import fr.abes.cidemis.components.Fichier;
import fr.abes.cidemis.components.NoticeHelper;
import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.dao.cidemis.CidemisDaoProvider;
import fr.abes.cidemis.mail.CidemisMail;
import fr.abes.cidemis.mail.CidemisTemplatesHtml;
import fr.abes.cidemis.model.cidemis.*;
import fr.abes.cidemis.model.dto.DemandeDto;
import fr.abes.cidemis.service.CidemisManageService;
import fr.abes.cidemis.service.IDemandesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class DemandesService implements IDemandesService {
    private final CidemisManageService service;
    private final CidemisDaoProvider dao;
    private final CidemisMail mail;

    private DemandeDto demandeDto;

    @Value("${cidemis.url}")
    private String cidemisUrl;

    private String cbsUrl;
    private String cbsPort;

    private String cbsPassword;
    private String path;

    public DemandesService(CidemisManageService service, CidemisDaoProvider dao, CidemisMail mail) {
        this.service = service;
        this.dao = dao;
        this.mail = mail;
    }


    /**
     * Services de la table Demandes
     */
    @Override
    public Demandes save(Demandes demandes) {
        if (demandes == null) {
            log.error("Erreur dans le parametre d'entrée , la variable 'demandes' vaut 'null'.");
            return null;
        }

        try {
            demandes.setDateModif(new Date());
            this.dao.getDemandesDao().save(demandes);
            log.info("Sauvegarde: Demandes. Id_demande:" + demandes.getIdDemande().toString() + " OK");
        } catch (Exception ex) {
            log.error("function save(). Demandes. Iddemande:" + demandes.getIdDemande().toString(), ex);
        }
        return demandes;
    }


    @Override
    public boolean delete(Demandes demandes) {
        if (demandes == null) {
            log.error("Erreur dans le parametre d'entrée , la variable 'demandes' vaut 'null'.");
            return false;
        }

        try {
            this.dao.getDemandesDao().delete(demandes);
            return true;
        } catch (Exception ex) {
            log.error("function delete(). Demandes. Id_demande:" + demandes.getIdDemande().toString(), ex);
            return false;
        }
    }

    @Override
    public Demandes findDemande(Integer idDemande) {
        Optional<Demandes> demandes = this.dao.getDemandesDao().findById(idDemande);

        if (demandes.isEmpty()) {
            log.debug("La fonction 'findDemandes' n'a retournée aucun résultat. Id_demande:" + idDemande);
            return null;
        }
        Demandes demande = demandes.get();
        demande.setTaggues(service.getTaggues().findTagguesByDemandes(demande));
        if (demande.getNotice() != null && demande.getNotice().getStatutNotice().equals("d")) {
            demande.setNoticeSupprimeeSudoc();
        }
        return demande;
    }

    /**
     * Recupère une liste de demandes
     *
     * @param cbsUsers         L'utilisateur représenté par une ligne en base de donnée
     * @param demandesAchieved Booléen représenté en front par une case à cocher, si inclusion des demandes terminées
     * @param demandesArchived Booléen représenté en front par une case à cocher, si inclusion des demandes archivées
     * @return une liste de demandes
     */
    @Override
    public List<Demandes> findDemandesByCbsUsers(CbsUsers cbsUsers, boolean demandesAchieved, boolean demandesArchived) {
        long startTime = System.nanoTime();
        List<Demandes> demandesList;
        if (demandesAchieved) { //Si on inclut les demandes terminées
            if (demandesArchived) { //Demandes terminées OUI, Demandes archivées OUI
                demandesList = getDemandesAll(cbsUsers);
            } else { //Demandes terminées OUI, Demandes archivées NON
                demandesList = getDemandesExceptArchived(cbsUsers);
            }
        } else { //Si on inclut pas les demandes terminées
            if (demandesArchived) { //Demandes terminées NON, Demandes archivées OUI
                demandesList = getDemandesExceptAchieved(cbsUsers);
            } else { //Demandes terminées NON, Demandes archivées NON
                demandesList = getDemandesExceptAchievedAndArchived(cbsUsers);
            }
        }
        updateNoticesSupprimees(demandesList);
        log.info("FIN" + (System.nanoTime() - startTime) / 1000);
        return demandesList;
    }

    private void updateNoticesSupprimees(List<Demandes> demandesList) {
        if (!demandesList.isEmpty()) {
            for (Demandes demande : demandesList) {
                if (demande.getNotice() != null && demande.getNotice().getStatutNotice().equals("d")) {
                    demande.setNoticeSupprimeeSudoc();
                }
            }
        }
    }

    private List<Demandes> getDemandesExceptAchievedAndArchived(CbsUsers cbsUsers) {
        List<Demandes> demandesList = new ArrayList<>();
        if (cbsUsers.getRoles().getIdRole().equals(Constant.ROLE_ABES))
            demandesList = this.dao.getDemandesDao().findAllExceptAchievedAndArchived();
        else if (cbsUsers.getRoles().getIdRole().equals(Constant.ROLE_CATALOGUEUR))
            demandesList = this.dao.getDemandesDao().findDemandesByCbsUsersExceptAchievedAndArchived(cbsUsers.getUserNum()); //Voir avec pierre
        else if (cbsUsers.getRoles().getIdRole().equals(Constant.ROLE_CORCAT))
            demandesList = this.dao.getDemandesDao().findDemandesByIlnExceptAchievedAndArchived(cbsUsers.getIln());
        else if (cbsUsers.getRoles().getIdRole().equals(Constant.ROLE_ISSN))
            demandesList = this.dao.getDemandesDao().findDemandesISSNExceptAchievedAndArchived();
        else if (cbsUsers.getRoles().getIdRole().equals(Constant.ROLE_CIEPS))
            demandesList = this.dao.getDemandesDao().findDemandesCIEPSExceptAchievedAndArchived();
        return demandesList;
    }

    private List<Demandes> getDemandesExceptAchieved(CbsUsers cbsUsers) {
        List<Demandes> demandesList = new ArrayList<>();
        if (cbsUsers.getRoles().getIdRole().equals(Constant.ROLE_ABES))
            demandesList = this.dao.getDemandesDao().findAllExceptDone();
        else if (cbsUsers.getRoles().getIdRole().equals(Constant.ROLE_CATALOGUEUR))
            demandesList = this.dao.getDemandesDao().findDemandesByCbsUsersExceptDone(cbsUsers.getUserNum());
        else if (cbsUsers.getRoles().getIdRole().equals(Constant.ROLE_CORCAT))
            demandesList = this.dao.getDemandesDao().findDemandesByIlnExceptDone(cbsUsers.getIln());
        else if (cbsUsers.getRoles().getIdRole().equals(Constant.ROLE_ISSN))
            demandesList = this.dao.getDemandesDao().findDemandesISSNExceptDone();
        else if (cbsUsers.getRoles().getIdRole().equals(Constant.ROLE_CIEPS))
            demandesList = this.dao.getDemandesDao().findDemandesCIEPSExceptDone();
        return demandesList;
    }

    private List<Demandes> getDemandesExceptArchived(CbsUsers cbsUsers) {
        List<Demandes> demandesList = new ArrayList<>();
        if (cbsUsers.getRoles().getIdRole().equals(Constant.ROLE_ABES))
            demandesList = this.dao.getDemandesDao().findAllExceptArchived();
        else if (cbsUsers.getRoles().getIdRole().equals(Constant.ROLE_CATALOGUEUR))
            demandesList = this.dao.getDemandesDao().findDemandesByCbsUsersExceptArchived(cbsUsers.getUserNum());
        else if (cbsUsers.getRoles().getIdRole().equals(Constant.ROLE_CORCAT))
            demandesList = this.dao.getDemandesDao().findDemandesByIlnExceptArchived(cbsUsers.getIln());
        else if (cbsUsers.getRoles().getIdRole().equals(Constant.ROLE_ISSN))
            demandesList = this.dao.getDemandesDao().findDemandesISSNExceptArchived();
        else if (cbsUsers.getRoles().getIdRole().equals(Constant.ROLE_CIEPS))
            demandesList = this.dao.getDemandesDao().findDemandesCIEPSExceptArchived();
        return demandesList;
    }

    private List<Demandes> getDemandesAll(CbsUsers cbsUsers) {
        List<Demandes> demandesList = new ArrayList<>();
        if (cbsUsers.getRoles().getIdRole().equals(Constant.ROLE_ABES))
            demandesList = this.dao.getDemandesDao().findAll();
        else if (cbsUsers.getRoles().getIdRole().equals(Constant.ROLE_CATALOGUEUR))
            demandesList = this.dao.getDemandesDao().findDemandesByCbsUsersAll(cbsUsers.getUserNum());
        else if (cbsUsers.getRoles().getIdRole().equals(Constant.ROLE_CORCAT))
            demandesList = this.dao.getDemandesDao().findDemandesByIlnAll(cbsUsers.getIln());
        else if (cbsUsers.getRoles().getIdRole().equals(Constant.ROLE_ISSN))
            demandesList = this.dao.getDemandesDao().findDemandesISSNAll();
        else if (cbsUsers.getRoles().getIdRole().equals(Constant.ROLE_CIEPS))
            demandesList = this.dao.getDemandesDao().findDemandesCIEPSAll();
        return demandesList;
    }

    @Override
    public List<Demandes> findDemandesByPPN(String ppn) {
        List<Demandes> demandesList = this.dao.getDemandesDao().findDemandesByPPN(ppn);

        if (demandesList.isEmpty())
            log.debug("La fonction 'findDemandesByPPN' n'a retournée aucun résultat. ppn:" + ppn);
        updateNoticesSupprimees(demandesList);
        return demandesList;
    }

    @Override
    public List<Demandes> findDemandesByISSN(String issn, String ppn) {
        List<Demandes> demandesList = this.dao.getDemandesDao().findDemandesByISSN(issn, ppn);

        if (demandesList.isEmpty())
            log.debug("La fonction 'findDemandesByISSN' n'a retournée aucun résultat. ISSN:" + issn);
        updateNoticesSupprimees(demandesList);
        return demandesList;
    }

    @Override
    public List<Demandes> findDemandesMailingCieps() {
        List<Demandes> demandesList = this.dao.getDemandesDao().findDemandesCIEPSForMailing();

        if (demandesList.isEmpty())
            log.debug("La fonction 'findDemandesMailingCieps' n'a retournée aucun résultat.");
        updateNoticesSupprimees(demandesList);
        return demandesList;
    }

    @Override
    public List<Demandes> findDemandesMailingCiepsForMailing() {
        List<Demandes> demandesList = this.dao.getDemandesDao().findDemandesCIEPSForMailing();

        if (demandesList.isEmpty())
            log.debug("La fonction 'findDemandesMailingCiepsForMailing' n'a retournée aucun résultat.");

        return demandesList;
    }

    /**
     * Vérifie que l'utilisateur peut modifier la demande demande en fonction du
     * role
     *
     * @param user
     * @param demande
     * @return true si l'utilisateur peut modifier la demande, false sinon
     */
    @Override
    public boolean canUserModifyDemande(CbsUsers user, Demandes demande) {
        switch (user.getRoles().getIdRole()) {
            case Constant.ROLE_ABES:
                return true;
            case Constant.ROLE_CATALOGUEUR:
                return demande.getCbsUsers().getUserNum().equals(user.getUserNum()) && demande.isStateIn(new int[]{
                        Constant.ETAT_EN_ATTENTE_VALIDATION_CATALOGUEUR, Constant.ETAT_EN_ATTENTE_PRECISION_CATALOGUEUR});
            case Constant.ROLE_CORCAT:
                return demande.getIln().equals(user.getIln())
                        && (demande.getEtatsDemandes().getIdEtatDemande().equals(Constant.ETAT_VALIDEE_PAR_CATALOGUEUR)
                        || demande.isStateIn(new int[]{Constant.ETAT_PRECISION_PAR_CATALOGUEUR,
                        Constant.ETAT_EN_ATTENTE_PRECISION_RESPONSABLE_CR,
                        Constant.ETAT_EN_ATTENTE_VALIDATION_RESPONSABLE_CR}));
            case Constant.ROLE_ISSN:
                return demande.isStateIn(
                        new int[]{Constant.ETAT_VALIDEE_PAR_RESPONSABLE_CR, Constant.ETAT_PRECISION_PAR_RESPONSABLE_CR})
                        && (user.getUserKey().equals(Constant.ADMIN_ISSN)
                        || user.getIdProfil().equals(demande.getIdProfil()
                ));
            case Constant.ROLE_CIEPS:
                return demande.isStateIn(new int[]{Constant.ETAT_VALIDEE_PAR_RESPONSABLE_CR_VERS_INTERNATIONAL,
                        Constant.ETAT_PRECISION_PAR_RESPONSABLE_CR, Constant.ETAT_VERS_INTERNATIONAL});
            default:
                return false;
        }
    }

    @Override
    public boolean canUserArchiveDemande(CbsUsers user, Demandes demande) {
        switch (user.getRoles().getIdRole()) {
            case Constant.ROLE_ABES:
            case Constant.ROLE_CORCAT:
                return ((demande.getEtatsDemandes().getIdEtatDemande().equals(Constant.ETAT_TRAITEMENT_TERMINE_REFUSEE)) ||
                        (demande.getEtatsDemandes().getIdEtatDemande().equals(Constant.ETAT_TRAITEMENT_TERMINE_ACCEPTEE)) ||
                        (demande.getEtatsDemandes().getIdEtatDemande().equals(Constant.ETAT_TRAITEMENT_REJETEE_PAR_CR)));
            default:
                return false;
        }
    }

    /**
     * Si c'est un administrateur ou si c'est le créateur de la demande et
     * qu'elle est dans l'état qu'il lui corresponds
     *
     * @param user    : l'utilisateur à vérifier
     * @param demande : la demande à vérifier
     * @return true if user can delete it
     */
    public boolean canUserDeleteDemande(CbsUsers user, Demandes demande) {
        boolean canDelete = false;

        // Si c'est un catalogueur
        if (user.getRoles().getIdRole().equals(Constant.ROLE_CATALOGUEUR)) {
            canDelete = demande.getEtatsDemandes().getIdEtatDemande()
                    .equals(Constant.ETAT_EN_ATTENTE_VALIDATION_CATALOGUEUR)
                    && demande.getCbsUsers().getUserNum().equals(user.getUserNum());
        }
        // Si c'est un administrateur
        else if (user.getRoles().getIdRole().equals(Constant.ROLE_ABES)) {
            canDelete = true;
        }
        // SI c'est un correspondant catalogage
        else if (user.getRoles().getIdRole().equals(Constant.ROLE_CORCAT)) {
            canDelete = demande.getEtatsDemandes().getIdEtatDemande()
                    .equals(Constant.ETAT_EN_ATTENTE_VALIDATION_RESPONSABLE_CR)
                    && demande.getCbsUsers().getUserNum().equals(user.getUserNum());
        }

        return canDelete;

    }


    @Override
    public void archiverDemande(Demandes demande, CbsUsers user) {
        demande.setEtatsDemandes(service.getReference().findEtatsdemandes(Constant.ETAT_ARCHIVEE));
        saveJournal(demande, user);
        service.getDemande().save(demande);
    }

    private void saveJournal(Demandes demande, CbsUsers user) {
        if (!demande.getLastjournal().getEtatsdemandes().getIdEtatDemande()
                .equals(demande.getEtatsDemandes().getIdEtatDemande())) {
            JournalDemandes journal = new JournalDemandes(this.dao.getJournalDemandesDao().getNextSeriesId());
            journal.setDemandes(demande);
            journal.setCbsUsers(user);
            journal.setEtatsdemandes(demande.getEtatsDemandes());
            journal.setDateEntree(new Date());
            demande.getJournalDemandesList().add(journal);
        }
    }

    @Override
    public boolean save(JournalDemandes journalDemandes) {
        if (journalDemandes == null) {
            log.error("Erreur dans le parametre d'entrée , la variable 'journal_demandes' vaut 'null'.");
            return false;
        }

        try {
            this.dao.getJournalDemandesDao().save(journalDemandes);
            log.info("Creation: Journal_Demandes. Id_journaldemande:"
                    + journalDemandes.getIdJournalDemande().toString() + " OK");
            return true;
        } catch (Exception ex) {
            log.error("function save(). Journal_Demandes. Id_journaldemande:"
                    + journalDemandes.getIdJournalDemande().toString(), ex);
            return false;
        }
    }

    @Override
    public boolean delete(JournalDemandes journalDemandes) {
        if (journalDemandes == null) {
            log.error("Erreur dans le parametre d'entrée , la variable 'JournalDemandes' vaut 'null'.");
            return false;
        }

        try {
            this.dao.getJournalDemandesDao().delete(journalDemandes);
            return true;
        } catch (Exception ex) {
            log.error("function delete(). Journal_Demandes. Id_journaldemande:"
                    + journalDemandes.getIdJournalDemande().toString(), ex);
            return false;
        }
    }

    @Override
    public List<JournalDemandes> findAllJournalDemandes() {
        List<JournalDemandes> journalDemandesList = this.dao.getJournalDemandesDao().findAll();

        if (journalDemandesList.isEmpty())
            log.debug("La fonction 'findAllJournal_Demandes' n'a retournée aucun résultat.");

        return journalDemandesList;
    }

    @Override
    public List<JournalDemandes> findJournalDemandesByEtatsdemandes(EtatsDemandes etatsdemandes) {
        List<JournalDemandes> journalDemandesList = this.dao.getJournalDemandesDao()
                .findAllByEtatsdemandes(etatsdemandes);

        if (journalDemandesList.isEmpty())
            log.debug(
                    "La fonction 'findJournal_DemandesByEtatsdemandes' n'a retournée aucun résultat. Id_etatdemande:"
                            + etatsdemandes.getIdEtatDemande().toString());

        return journalDemandesList;
    }

    @Override
    public List<JournalDemandes> findJournalDemandesByDemandes(Demandes demandes) {
        List<JournalDemandes> journalDemandesList = this.dao.getJournalDemandesDao().findAllByDemandes(demandes);

        if (journalDemandesList.isEmpty())
            log.debug(
                    "La fonction 'findJournal_DemandesByDemandes' n'a retournée aucun résultat. Id_demande:"
                            + demandes.getIdDemande().toString());

        return journalDemandesList;
    }

    @Override
    public List<JournalDemandes> findJournalDemandesByCbsUsers(CbsUsers cbsUsers) {
        List<JournalDemandes> journalDemandesList = this.dao.getJournalDemandesDao().findAllByCbsUsers(cbsUsers);

        if (journalDemandesList.isEmpty())
            log.debug(
                    "La fonction 'findJournal_DemandesByCbs_Users' n'a retournée aucun résultat. User_num:"
                            + cbsUsers.getUserNum().toString());

        return journalDemandesList;
    }

    @Override
    public boolean saveJournalDemandesListByEtatsdemandes(EtatsDemandes etatsdemandes) {
        List<JournalDemandes> journalDemandesList = etatsdemandes.getJournalDemandesList();
        List<JournalDemandes> journalDemandesBDDList = this.dao.getJournalDemandesDao()
                .findAllByEtatsdemandes(etatsdemandes);
        boolean find;

        for (JournalDemandes journal_demandesBDD : journalDemandesBDDList) {
            find = false;

            for (JournalDemandes journal_demandes : journalDemandesList)
                find |= journal_demandesBDD.getIdJournalDemande().equals(journal_demandes.getIdJournalDemande());

            if (!find)
                this.dao.getJournalDemandesDao().delete(journal_demandesBDD);
        }

        return (!dao.getJournalDemandesDao().saveAll(journalDemandesList).isEmpty());
    }

    @Override
    public boolean saveDemandesListByEtatsdemandes(EtatsDemandes etatsdemandes) {
        List<Demandes> demandesList = etatsdemandes.getDemandesList();
        List<Demandes> demandesBDDList = this.dao.getDemandesDao().findDemandesByEtatsDemandes(etatsdemandes);
        boolean find;

        for (Demandes demandesBDD : demandesBDDList) {
            find = false;

            for (Demandes demandes : demandesList)
                find |= demandesBDD.getIdDemande().equals(demandes.getIdDemande());

            if (!find)
                this.dao.getDemandesDao().delete(demandesBDD);
        }

        return (!dao.getDemandesDao().saveAll(demandesList).isEmpty());
    }

    @Override
    public boolean saveDemandesListByTypesdemandes(TypesDemandes typesdemandes) {
        List<Demandes> demandesList = typesdemandes.getDemandesList();
        List<Demandes> demandesBDDList = this.dao.getDemandesDao().findDemandesByTypesDemandes(typesdemandes);
        boolean find;

        for (Demandes demandesBDD : demandesBDDList) {
            find = false;
            for (Demandes demandes : demandesList)
                find |= demandesBDD.getIdDemande().equals(demandes.getIdDemande());
            if (!find)
                this.dao.getDemandesDao().delete(demandesBDD);
        }

        return (!dao.getDemandesDao().saveAll(demandesList).isEmpty());
    }

    @Override
    public List<Demandes> findDemandesForUpdateRefus(Date borneInf, Date borneSup) {
        return dao.getDemandesDao().findAllRefuseByCieps(borneInf, borneSup);
    }

    @Override
    public Demandes creerDemande(DemandeDto demandeDto, CbsUsers user, RegistryUser registryuser, String cbsUrl, String cbsPort, String cbsPassword, String path) throws ZoneException, CBSException, RestClientException, IOException {
        log.info(
                "CreerDemande. connexion.getUser().getUserkey() = " + user.getUserKey());
        this.cbsUrl = cbsUrl;
        this.cbsPort = cbsPort;
        this.cbsPassword = cbsPassword;
        this.path = path;
        this.demandeDto = demandeDto;
        CidemisNotices notice;
        // Ce ppn peut être ajouté par le CorCat dans le cadre d'une demande de
        // création de notice, le paramètre numPPN sera alors rempli avec le ppn
        // de la notice nouvellement créée
        if (this.demandeDto.getNumPpn() != null && this.demandeDto.getNumPpn().matches("(\\d{9})|((?i)\\d{8}X{1})")) {
            notice = service.getTools().findCidemisNotice(this.demandeDto.getNumPpn());
            if (!notice.getPpn().isEmpty()) {
                this.demandeDto.setPpn(this.demandeDto.getNumPpn());
            }
        }
        Demandes demande;
        // Mise à jour de la demande
        if (this.demandeDto.getIdDemande() == -1) {
            demande = new Demandes(dao.getDemandesDao().getNextSeriesId());
            demande = this.processNewDemande(demande, user);
        } else {
            demande = this.processUpdateDemande(user);
        }
        demande.setCommentairesList(dao.getCommentairesDao().findCommentairesByDemandeOrderByDateCommentaire(demande));
        if (!this.demandeDto.getIssn().isEmpty())
            demande.setIssn(this.demandeDto.getIssn());
        if (!this.demandeDto.getZones().isEmpty())
            demande.setZones(this.demandeDto.getZones());
        if (!this.demandeDto.getRcrDemandeur().isEmpty())
            demande.setRcrDemandeur(this.demandeDto.getRcrDemandeur());

        // Si c'est l'admin ISSN alors il a l'option pour modifier le profil
        // attribué à une demande.
        if (user.getUserKey().equals(Constant.ADMIN_ISSN))
            demande.setIdProfil(this.demandeDto.getIdProfil());
        if (demande.getTypesDemandes().getIdTypeDemande() == Constant.TYPE_DEMANDE_CREATION) {
            demande.setTitre(this.demandeDto.getTitre());
        } else {
            if (!demande.getNotice().getPpn().isEmpty()) {
                if (demande.getTitre() == null || !demande.getTitre().equals("NOTICE SUPPRIMÉE DU SUDOC"))
                    demande.setTitre(demande.getNotice().getTitre());
            }
            if (demande.getTitre() != null && !demande.getTitre().equals("NOTICE SUPPRIMÉE DU SUDOC")) {
                // Mise à jour dans le CBS de la notice
                this.updateOnCBS(demande, user, registryuser);
            }
        }
        // Mise à jour des autres informations relatives à la demande
        this.updateComments(demande, user);
        this.updateTaggues(demande);
        this.updateJustificatifs(demande, user);
        this.updateJournal(demande, user);

        demande.setNbCommentaires(demande.getCommentairesList().size());
        demande.setNbPiecesJustificatives(demande.getPiecesJustificativeslist().size());
        final int[] nbCommentIssn = {0};
        Arrays.stream(demande.getCommentairesList().toArray(new Commentaires[0])).forEach(commentaires -> {
            if (commentaires.getVisibleISSN()) {
                nbCommentIssn[0]++;
            }
        });
        demande.setNbCommentairesISSN(nbCommentIssn[0]);

        // Sauvegarde de la demande en BDD
        return service.getDemande().save(demande);

    }

    public void envoiMail(Demandes demande, CbsUsers user) throws RestClientException, JsonProcessingException {
        // Si la demande part au CIEPS on envoie un MAIL
        if (demande.sendMailToCieps(user)) {
            mail.sendMailCIEPS(demande);
        } else {
            // Envoi de mail en fonction de l'action dans le formulaire
            if ("valider".equals(this.demandeDto.getAction())
                    || "precisions".equals(this.demandeDto.getAction())
                    || "refuser".equals(this.demandeDto.getAction())
                    || "accepter".equals(this.demandeDto.getAction())
                    || "rejeter".equals(this.demandeDto.getAction())) {
                if (!user.getRoles().getIdRole().equals(Constant.ROLE_ABES)) {
                    this.envoiMailChangementStatut(demande, user);
                }
            }
        }
    }

    /**
     * Créer la demande dans le cadre d'une nouvelle demande
     */
    private Demandes processNewDemande(Demandes demande, CbsUsers user) {
        demande.setNotice(service.getTools().findCidemisNotice(this.demandeDto.getPpn()));

        demande.setTypesDemandes(service.getReference().findTypesdemandes(this.demandeDto.getTypesDemandes()));
        demande.setIdProfil(Constant.PROFIL_PAS_PROFIL);
        demande.setDateDemande(new Date());
        demande.setRcrDemandeur(user.getLibrary());
        demande.setCbsUsers(user);
        demande.setIln(user.getIln());

        if ("valider".equals(this.demandeDto.getAction())) {
            if (user.getRoles().getIdRole().equals(Constant.ROLE_CATALOGUEUR)) {
                this.changeEtat(service.getReference().findEtatsdemandes(Constant.ETAT_VALIDEE_PAR_CATALOGUEUR), demande);
            } else if (user.getRoles().getIdRole().equals(Constant.ROLE_CORCAT)) {
                demande.setIdProfil(demande.getNotice().getIdprofil());
                demande = setEtatISSN(demande, false);
                this.setCentreISSN(demande, false);
            }
        } else if ("creernotice".equals(this.demandeDto.getAction())) {
            if (user.getRoles().getIdRole().equals(Constant.ROLE_CORCAT)) {
                demande.setIdProfil(demande.getNotice().getIdprofil());
                demande.setTypesDemandes(service.getReference().findTypesdemandes(Constant.TYPE_DEMANDE_NUMEROTATION));
                demande = setEtatISSN(demande, false);
                setCentreISSN(demande, true);
            }
        } else {
            if (user.getRoles().getIdRole().equals(Constant.ROLE_CATALOGUEUR)) {
                this.changeEtat(service.getReference().findEtatsdemandes(Constant.ETAT_EN_ATTENTE_VALIDATION_CATALOGUEUR), demande);
            } else if (user.getRoles().getIdRole().equals(Constant.ROLE_CORCAT)) {
                this.changeEtat(service.getReference().findEtatsdemandes(Constant.ETAT_EN_ATTENTE_VALIDATION_RESPONSABLE_CR), demande);
            }
        }
        return demande;
    }

    /**
     * Mets à jour la demande et notamment son état dans le cadre d'une
     * modification d'une demande déjà existante
     */
    private Demandes processUpdateDemande(CbsUsers user) throws RestClientException {
        Demandes demande = service.getDemande().findDemande(this.demandeDto.getIdDemande());
        demande.setNotice(service.getTools().findCidemisNotice(this.demandeDto.getPpn()));
        if (demande != null && service.getDemande().canUserModifyDemande(user, demande)) {
            if ("valider".equals(this.demandeDto.getAction())) {
                switch (demande.getEtatsDemandes().getIdEtatDemande()) {
                    case Constant.ETAT_EN_ATTENTE_VALIDATION_RESPONSABLE_CR:
                    case Constant.ETAT_VALIDEE_PAR_CATALOGUEUR:
                    case Constant.ETAT_EN_ATTENTE_PRECISION_RESPONSABLE_CR:
                    case Constant.ETAT_PRECISION_PAR_CATALOGUEUR:
                        demande.setIdProfil(demande.getNotice().getIdprofil());
                        demande = setEtatISSN(demande, false);
                        this.setCentreISSN(demande, false);
                        break;
                    case Constant.ETAT_VALIDEE_PAR_RESPONSABLE_CR:
                    case Constant.ETAT_VALIDEE_PAR_RESPONSABLE_CR_VERS_INTERNATIONAL:
                    case Constant.ETAT_PRECISION_PAR_RESPONSABLE_CR:
                        this.changeEtat(service.getReference().findEtatsdemandes(Constant.ETAT_TRAITEMENT_TERMINE_ACCEPTEE), demande);
                        break;
                    case Constant.ETAT_EN_ATTENTE_VALIDATION_CATALOGUEUR:
                        this.changeEtat(service.getReference().findEtatsdemandes(Constant.ETAT_VALIDEE_PAR_CATALOGUEUR), demande);
                        break;
                    case Constant.ETAT_EN_ATTENTE_PRECISION_CATALOGUEUR:
                        this.changeEtat(service.getReference().findEtatsdemandes(Constant.ETAT_PRECISION_PAR_CATALOGUEUR), demande);
                        break;
                    default:
                        break;
                }
            } else if ("enregistrer".equals(this.demandeDto.getAction())) {
                switch (demande.getEtatsDemandes().getIdEtatDemande()) {
                    case Constant.ETAT_VALIDEE_PAR_CATALOGUEUR:
                    case Constant.ETAT_PRECISION_PAR_CATALOGUEUR:
                        this.changeEtat(service.getReference().findEtatsdemandes(Constant.ETAT_EN_ATTENTE_VALIDATION_RESPONSABLE_CR), demande);
                        break;
                    default:
                        break;
                }
            } else if ("precisions".equals(this.demandeDto.getAction())) {
                switch (demande.getEtatsDemandes().getIdEtatDemande()) {
                    case Constant.ETAT_PRECISION_PAR_CATALOGUEUR:
                    case Constant.ETAT_VALIDEE_PAR_CATALOGUEUR:
                    case Constant.ETAT_EN_ATTENTE_VALIDATION_RESPONSABLE_CR:
                    case Constant.ETAT_EN_ATTENTE_PRECISION_RESPONSABLE_CR:
                        this.changeEtat(service.getReference().findEtatsdemandes(Constant.ETAT_EN_ATTENTE_PRECISION_CATALOGUEUR), demande);
                        break;
                    case Constant.ETAT_PRECISION_PAR_RESPONSABLE_CR:
                    case Constant.ETAT_VALIDEE_PAR_RESPONSABLE_CR_VERS_INTERNATIONAL:
                    case Constant.ETAT_VERS_INTERNATIONAL:
                    case Constant.ETAT_VALIDEE_PAR_RESPONSABLE_CR:
                        this.changeEtat(service.getReference().findEtatsdemandes(Constant.ETAT_EN_ATTENTE_PRECISION_RESPONSABLE_CR), demande);
                        break;
                    default:
                        break;
                }
            } else if ("creernotice".equals(this.demandeDto.getAction())) {
                if (user.getRoles().getIdRole().equals(Constant.ROLE_CORCAT)) {
                    demande.setIdProfil(demande.getNotice().getIdprofil());
                    demande.setTypesDemandes(
                            service.getReference().findTypesdemandes(Constant.TYPE_DEMANDE_NUMEROTATION));
                    demande = setEtatISSN(demande, false);
                    this.setCentreISSN(demande, true);
                }
            } else if ("refuser".equals(this.demandeDto.getAction())) {
                this.changeEtat(service.getReference().findEtatsdemandes(Constant.ETAT_TRAITEMENT_TERMINE_REFUSEE), demande);
            } else if ("accepter".equals(this.demandeDto.getAction())) {
                this.changeEtat(service.getReference().findEtatsdemandes(Constant.ETAT_TRAITEMENT_TERMINE_ACCEPTEE), demande);
            } else if ("rejeter".equals(this.demandeDto.getAction())) {
                this.changeEtat(service.getReference().findEtatsdemandes(Constant.ETAT_TRAITEMENT_REJETEE_PAR_CR), demande);
            }
        } else {
            log.error(
                    "CreerDemande. UpdateDemande. La demande n'existe pas ou l'utilisateur ne peut pas la modifier ID_DEMANDE = "
                            + this.demandeDto.getIdDemande());
        }
        return demande;
    }

    private void changeEtat(EtatsDemandes etat, Demandes demande) {
        demande.setEtatsDemandes(etat);
    }

    private void envoiMailChangementStatut(Demandes demande, CbsUsers user) throws RestClientException {
        service.getControleEnvoiMail().whichRoleOfUserToSendEmail(user, demande).forEach(role -> {
            service.getUsers().findCbsUsersToSendEmail(demande, role).forEach(cbsUsers -> {
                String subjectOfMail = "[AVIS CIDEMIS] : changement de statut de la demande n°"
                        + demande.getIdDemande() + " : " + demande.getEtatsDemandes().getLibelleEtatDemande();
                String receiverOfMail = cbsUsers.getUserEmail();
                String objectOfMail = CidemisTemplatesHtml.getCidemisMailChangementStatut(demande, cidemisUrl);
                try {
                    mail.sendMailWhenStatusChange(new String[]{subjectOfMail, receiverOfMail, objectOfMail});
                } catch (JsonProcessingException e) {
                    throw new RestClientException(e.getMessage());
                }
            });
        });
    }

    /**
     * Mets à jour la notice dans le CBS selon l'état
     */
    private void updateOnCBS(Demandes demande, CbsUsers user, RegistryUser registryUser) throws ZoneException, CBSException, IOException {
        ProcessCBS cbs = new ProcessCBS();
        cbs.authenticate(cbsUrl, cbsPort, 'M' + registryUser.getLibrary(), cbsPassword);

        NoticeHelper noticehelper = new NoticeHelper(cbs);
        String idCidemis = " (identifiant Cidemis : " + demande.getIdDemande() + ")";

        // Si le code est différent (donc une réattribution) de celui stocké
        // dans la notice, alors on modifie la demande, la notice, et on ajoute
        // un commentaire notifiant de la redirection
        if (!this.demandeDto.getCodePays().isEmpty() && !this.demandeDto.getCodePays().equals(demande.getNotice().getPays())) {
            demande.getNotice().setPays(this.demandeDto.getCodePays());
            demande = setEtatISSN(demande, true);
            noticehelper.modifierZoneNotice(demande.getNotice().getPpn(), "102", "$a", this.demandeDto.getCodePays());
            Commentaires commentaire = new Commentaires();
            commentaire.setCbsUsers(user);
            commentaire.setLibCommentaire("La demande a été redirigée vers : "
                    + Constant.getCodePaysSorted().get(this.demandeDto.getCodePays()).getPays() + " (" + this.demandeDto.getCodePays() + ").");
            commentaire.setDemande(demande);
            commentaire.setDateCommentaire(new Date());
            commentaire.setVisibleISSN(this.demandeDto.getCommentaireVisibleIssn());
            demande.getCommentairesList().add(commentaire);
        }

        // Validée à destination de ... : ZONES 830$a et 301$a : On rempli la
        // zone quand la demande passe en "validée"
        if (demande.isStateIn(new int[]{Constant.ETAT_VALIDEE_PAR_CATALOGUEUR, Constant.ETAT_VALIDEE_PAR_RESPONSABLE_CR,
                Constant.ETAT_VALIDEE_PAR_RESPONSABLE_CR_VERS_INTERNATIONAL, Constant.ETAT_VERS_INTERNATIONAL}))
            this.updateCBSValidated(noticehelper, idCidemis, demande, user);
            // Accepté : NUMERO ISSN et CENTRE ISSN
        else if (demande.getEtatsDemandes().getIdEtatDemande().equals(Constant.ETAT_TRAITEMENT_TERMINE_ACCEPTEE))
            this.updateCBSAcceptedAndDone(noticehelper, idCidemis, demande);
            // Refusé : Cas d'un refus de la part d'ISSN ou CIEPS
        else if (demande.getEtatsDemandes().getIdEtatDemande().equals(Constant.ETAT_TRAITEMENT_TERMINE_REFUSEE))
            this.updateCBSRefusedByISSNOrCIEPS(noticehelper, idCidemis, demande);
            // Rejetée
        else if (demande.getEtatsDemandes().getIdEtatDemande().equals(Constant.ETAT_TRAITEMENT_REJETEE_PAR_CR))
            this.updateCBSRejected(noticehelper, idCidemis, demande);
    }

    /**
     * Enregistre le commentaire saisi Il est possible de modifier le dernier
     * commentaire si c'est l'utilisateur qui l'a crée
     */
    private Demandes updateComments(Demandes demande, CbsUsers user) {
        if (!this.demandeDto.getCommentaireTxt().isEmpty()) {
            Commentaires commentaire = dao.getCommentairesDao().findById(this.demandeDto.getLastIdCommentaire()).orElse(new Commentaires());
            commentaire.setCbsUsers(user);
            commentaire.setLibCommentaire(this.demandeDto.getCommentaireTxt());
            commentaire.setDemande(demande);
            commentaire.setDateCommentaire(new Date());
            commentaire.setVisibleISSN(this.demandeDto.getCommentaireVisibleIssn());

            if (this.demandeDto.getLastIdCommentaire() <= 0) {
                demande.getCommentairesList().add(commentaire);
            } else {
                ListIterator<Commentaires> listIterator = demande.getCommentairesList().listIterator();
                while (listIterator.hasNext()) {
                    Commentaires comment = listIterator.next();
                    if (comment.getIdCommentaire().equals(commentaire.getIdCommentaire())) {
                        listIterator.set(commentaire);
                    }
                }
            }
        }
        return demande;
    }

    /**
     * enregistre le tag saisi dans la demande
     */
    private Demandes updateTaggues(Demandes demande) {
        Taggues taggues;
        if (!this.demandeDto.getTaggueTxt().isEmpty()) {
            Taggues newtag = new Taggues();
            newtag.setDemande(demande);
            newtag.setLibelleTaggue(this.demandeDto.getTaggueTxt());
            newtag.setCouleurTaggue(calculCouleur(this.demandeDto.getTaggueTxt()));
            taggues = newtag;
            demande.setTaggues(taggues);
        }
        return demande;
    }

    private String calculCouleur(String libelle) {
        DefaultTaggues taggue = dao.getDefaultTagguesDao().findByLibelleTaggue(libelle);
        return (taggue != null) ? taggue.getCouleurTaggue() : Constant.DEFAULT_COLOR;
    }

    /**
     * Enregistre les PJ relatives à la demandes
     */
    private Demandes updateJustificatifs(Demandes demande, CbsUsers user) {
        List<PiecesJustificatives> justificatifs = dao.getPiecesJustificativesDao().findAllByDemande(demande);
        List<PiecesJustificatives> justificatifsToRemove = new ArrayList<>();

        File delete;
        PiecesJustificatives justif;
        File justiffile;
        if (justificatifs != null) {
            // Gestion de la suppression des fichiers déjà dans la bdd
            if (this.demandeDto.getFilesToDelete() != null) {
                for (String filetodelete : this.demandeDto.getFilesToDelete()) {
                    for (PiecesJustificatives j : justificatifs) {
                        // Si l'utilisateur qui a uploadé le fichier est le même que
                        // celui voulant le supprimer alors c'est OK | Le CorCat a le
                        // droit de tout supprimer
                        if (j.getIdPiece() == Integer.parseInt(filetodelete)
                                && (j.getCbsUsers().getUserNum().equals(user.getUserNum())
                                || j.getCbsUsers().getRoles().getIdRole() == Constant.ROLE_CATALOGUEUR)) {
                            justificatifsToRemove.add(j);
                            delete = new File(j.getPathFichier(path));
                            this.supprimerFichier(delete);

                        }
                    }
                }
            }
        } else {
            justificatifs = new ArrayList<>();
        }
        if (!justificatifsToRemove.isEmpty()) {
            justificatifs.removeAll(justificatifsToRemove);
            dao.getPiecesJustificativesDao().deleteAll(justificatifsToRemove);
        }
        // Création du répertoire de la demande pour dépot des fichiers si
        // inexistant
        File pathFiles = new File(
                path + demande.getIdDemande().toString());
        this.creerDossier(pathFiles);

        // Récupère les fichiers à sauvegarder
        for (Fichier fichier : this.demandeDto.getFichiers()) {
            justif = new PiecesJustificatives(dao.getPiecesJustificativesDao().getNextSeriesId());
            justiffile = new File(
                    pathFiles.getPath() + "/" + justif.getIdPiece() + "_" + fichier.getFilename());

            try {
                deplacerFichier(fichier.getFile(), justiffile);
                justif.setCbsUsers(user);
                justif.setDemande(demande);
                justif.setLienPiece(fichier.getFilename());
                justificatifs.add(justif);
            } catch (InterruptedException e) {
                log.error("Erreur lors du dépot du fichier " + fichier.getFilename());
            } catch (IOException e) {
                log.error("Erreur lors du dépot du fichier " + fichier.getFilename());
            }
        }

        demande.setPiecesJustificativeslist(justificatifs);
        return demande;
    }

    /**
     * Enregistre le journal de la demande (pour avoir un historique complets de
     * la demande)
     */
    private Demandes updateJournal(Demandes demande, CbsUsers user) {
        demande.setJournalDemandesList(dao.getJournalDemandesDao().findAllByDemandes(demande)); //retourne size 0 si aucune entrée en journal

        if (demande.getJournalDemandesList() != null && !demande.getJournalDemandesList().isEmpty()) {
            // Si il y a au moins une entrée dans le journal
            // Si le nouvel état de la demande est différent de l'état de la
            // dernière entrée dans le journal alors on rajoute une entrée au
            // journal
            saveJournal(demande, user);
        } else {
            JournalDemandes journal = new JournalDemandes(this.dao.getJournalDemandesDao().getNextSeriesId());
            journal.setDemandes(demande);
            journal.setCbsUsers(user);
            journal.setEtatsdemandes(demande.getEtatsDemandes());
            journal.setDateEntree(new Date());
            demande.getJournalDemandesList().add(journal);
        }
        return demande;
    }

    // /////////////////////////////////////////////
    // Mise à jour par le CBS
    // /////////////////////////////////////////////

    /**
     * Mets à jour la notice dans le cas d'une validation par CorCat ou Catalogueur
     *
     * @param noticehelper
     * @param idCidemis
     * @return
     */
    private void updateCBSValidated(NoticeHelper noticehelper, String idCidemis, Demandes demande, CbsUsers user) throws CBSException, ZoneException, IOException {
        String auteur;
        String dateNowFormat = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (user.getRoles().getIdRole().equals(Constant.ROLE_CORCAT))
            auteur = "ILN " + demande.getIln();
        else
            auteur = user.getShortName();

        // Initialisation des informations selon l'utilisateur
        String regexIdCidemis = ".*[(]identifiant Cidemis : " + demande.getIdDemande() + "[)]";
        String zone = "";
        String textCatalogueur = "";
        String textCR = "";
        Boolean zoneExist = false;

        if (demande.getTypesDemandes().getIdTypeDemande().equals(Constant.TYPE_DEMANDE_CORRECTION)) {
            zone = "830";
            textCatalogueur = "Demande de correction ISSN en cours pour la zone " + demande.getZones().replaceAll("\\$", " ") + idCidemis;
            textCR = "Demande de correction ISSN en cours pour la zone " + demande.getZones().replaceAll("\\$", " ") + " par " + auteur
                    + " le " + dateNowFormat + idCidemis;

            for (String note : demande.getNotice().getNoteGeneraleCatalogueur())
                zoneExist = zoneExist || note.matches(regexIdCidemis);
        } else if (demande.getTypesDemandes().getIdTypeDemande().equals(Constant.TYPE_DEMANDE_NUMEROTATION)) {
            zone = "301";
            textCatalogueur = "Demande de numérotation ISSN en cours" + idCidemis;
            textCR = "Demande de numérotation ISSN en cours par " + auteur + " le " + dateNowFormat + idCidemis;

            for (String note : demande.getNotice().getNoteIdentifiants())
                zoneExist = zoneExist || note.matches(regexIdCidemis);
        }

        // Mise à jour des zones si je les ai trouvés et que c'est le bon role
        if (!zoneExist && !zone.isEmpty() && user.getRoles().getIdRole().equals(Constant.ROLE_CATALOGUEUR)) {
            noticehelper.ajoutZoneNotice(demande.getNotice().getPpn(), zone, "$a", textCatalogueur);
        } else if (!zone.isEmpty() && user.getRoles().getIdRole().equals(Constant.ROLE_CORCAT)) {
            if (!zoneExist) {
                noticehelper.ajoutZoneNotice(demande.getNotice().getPpn(), zone, "$a", textCR);
            } else {
                noticehelper.chercherEtSupprimerZoneNotice(demande.getNotice().getPpn(), zone, "$a", idCidemis);
                noticehelper.ajoutZoneNotice(demande.getNotice().getPpn(), zone, "$a", textCR);
            }
        }
    }

    /**
     * Mets à jour la notice dans le cas d'une acceptation par ISSN ou Cieps
     *
     * @param noticehelper
     * @param idCidemis
     * @return
     */
    private void updateCBSAcceptedAndDone(NoticeHelper noticehelper, String idCidemis, Demandes demande) throws CBSException, ZoneException, IOException {
        if (demande.getTypesDemandes().getIdTypeDemande().equals(Constant.TYPE_DEMANDE_NUMEROTATION)) {
            // Si l'ISSN n'est pas déjà présent dans la notice
            if (!demande.getIssn().isEmpty() &&
                    demande.getNotice().getIssn() == null || !demande.getNotice().getIssn().contains(demande.getIssn()))
                noticehelper.ajoutZoneNotice(demande.getNotice().getPpn(), "011", "$a", demande.getIssn());

            // On met à jour le centre ISSN s'il ne corresponds pas (deux cas,
            // il n'existe pas dans la notice ou pas)
            if (!demande.getCentreISSN().equals(demande.getNotice().getCentreISSN()) && demande.getNotice().getCentreISSN() == null)
                noticehelper.ajoutZoneNotice(demande.getNotice().getPpn(), "802", "$a", demande.getCentreISSN());
            else if (!demande.getCentreISSN().equals(demande.getNotice().getCentreISSN()))
                noticehelper.modifierZoneNotice(demande.getNotice().getPpn(), "802", "$a", demande.getCentreISSN());

            // Et j'enregistre l'ID Cidemis dans la notice
            noticehelper.chercherEtSupprimerZoneNotice(demande.getNotice().getPpn(), "301", "$a", idCidemis);
        } else if (demande.getTypesDemandes().getIdTypeDemande().equals(Constant.TYPE_DEMANDE_CORRECTION)) {
            noticehelper.chercherEtSupprimerZoneNotice(demande.getNotice().getPpn(), "830", "$a", idCidemis);
        }
    }

    /**
     * Mets à jour la notice dans le cas d'un refus de la part de ISSN ou CIEPS
     *
     * @param noticehelper
     * @param idCidemis
     * @return
     */
    private void updateCBSRefusedByISSNOrCIEPS(NoticeHelper noticehelper, String idCidemis, Demandes demande) throws CBSException, ZoneException, IOException {
        String dateNowFormat = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        if (demande.getTypesDemandes().getIdTypeDemande().equals(Constant.TYPE_DEMANDE_CORRECTION)) {
            noticehelper.chercherEtSupprimerZoneNotice(demande.getNotice().getPpn(), "830", "$a", idCidemis);
            noticehelper.ajoutZoneNotice(demande.getNotice().getPpn(), "830", "$a",
                    "Demande de correction ISSN refusée pour la zone " + demande.getZones() + " le " + dateNowFormat
                            + ". Raison refus: " + this.demandeDto.getCommentaireTxt().replaceAll("[\n\r]", "") + idCidemis);
        } else if (demande.getTypesDemandes().getIdTypeDemande().equals(Constant.TYPE_DEMANDE_NUMEROTATION)) {
            noticehelper.chercherEtSupprimerZoneNotice(demande.getNotice().getPpn(), "301", "$a", idCidemis);
            noticehelper.ajoutZoneNotice(demande.getNotice().getPpn(), "301", "$a",
                    "Demande de numérotation refusée par ISSN le " + dateNowFormat + ". Raison refus: "
                            + this.demandeDto.getCommentaireTxt().replaceAll("[\n\r]", "") + idCidemis);
        }
    }

    /**
     * Mets à jour la notice dans le cas d'un refus de la part d'un CorCat
     *
     * @param noticehelper
     * @param idCidemis
     * @return
     */
    private void updateCBSRejected(NoticeHelper noticehelper, String idCidemis, Demandes demande) throws CBSException, ZoneException, IOException {
        if (demande.getTypesDemandes().getIdTypeDemande().equals(Constant.TYPE_DEMANDE_CORRECTION))
            noticehelper.chercherEtSupprimerZoneNotice(demande.getNotice().getPpn(), "830", "$a", idCidemis);
        else if (demande.getTypesDemandes().getIdTypeDemande().equals(Constant.TYPE_DEMANDE_NUMEROTATION))
            noticehelper.chercherEtSupprimerZoneNotice(demande.getNotice().getPpn(), "301", "$a", idCidemis);
    }

    // /////////////////////////////////////////////
    // Autres
    // /////////////////////////////////////////////

    /**
     * Mets à jour l'état de la demande selon son code pays
     *
     * @param demande
     * @param fromIssn true si la demande provient d'une redirection depuis la BNF
     * @return
     */
    private Demandes setEtatISSN(Demandes demande, boolean fromIssn) {
        if (Constant.getCodePaysFr().contains(demande.getNotice().getPays()))
            demande.setEtatsDemandes(service.getReference().findEtatsdemandes(Constant.ETAT_VALIDEE_PAR_RESPONSABLE_CR));
        else if (fromIssn)
            demande.setEtatsDemandes(service.getReference().findEtatsdemandes(Constant.ETAT_VERS_INTERNATIONAL));
        else
            demande.setEtatsDemandes(service.getReference().findEtatsdemandes(Constant.ETAT_VALIDEE_PAR_RESPONSABLE_CR_VERS_INTERNATIONAL));
        return demande;
    }

    /**
     * Mets à jour le centre ISSN d'une demande selon le code pays de la notice
     * uniquement si c'est une demande de correction ou de numérotation
     *
     * @param demande
     * @param force   true to donc check the demande type
     * @return demande
     */
    private Demandes setCentreISSN(Demandes demande, boolean force) {
        if ((force || demande.getTypesDemandes().getIdTypeDemande() == Constant.TYPE_DEMANDE_NUMEROTATION
                || demande.getTypesDemandes().getIdTypeDemande() == Constant.TYPE_DEMANDE_CORRECTION)
                && demande.getCentreISSN() == null) {
            String codePaysNotice = demande.getNotice().getPays().split(" ")[0];
            codePaysNotice = "ZZ".equals(codePaysNotice) ? "XX" : codePaysNotice;
            demande.setCentreISSN(Constant.getCodePaysSorted().get(codePaysNotice).getCodecentreissn());
        }
        return demande;
    }

    // /////////////////////////////////////////////
    // Fonction de gestion des dossiers et des fichiers
    // /////////////////////////////////////////////

    /**
     * Déplace un fichier de source vers destination
     *
     * @param source
     * @param destination
     * @return
     */
    private boolean deplacerFichier(File source, File destination) throws IOException, InterruptedException {
        boolean result = false;
        if (!destination.exists()) {
            result = source.renameTo(destination);
            if (!result) {
                String[] cmd = new String[]{"mv", source.getAbsolutePath(), destination.getAbsolutePath()};
                Runtime r = Runtime.getRuntime();
                Process p = r.exec(cmd);
                p.waitFor();
                result = true;
            }
        }
        return result;

    }

    /**
     * Créer un dossier folder
     *
     * @param folder
     * @return
     */
    private boolean creerDossier(File folder) {
        if (!folder.exists()) {
            try {
                if (!folder.mkdir())
                    throw new IOException();
                else
                    return true;
            } catch (IOException e) {
                log.error("CreerDemande Exception creerDossier " + folder.getPath(), e);
                return false;
            }
        }
        return false;
    }

    /**
     * Supprimer le fichier file
     *
     * @param file
     * @return
     */
    private void supprimerFichier(File file) {
        if (file.exists()) {
            if (!file.delete())
                log.warn("Erreur à la suppression du fichier " + file.getName());
        }
    }

    // Utilisé pour optimiser le temps d'affichage du tableau de bord
    public Map<String, String> getDemandemap(Demandes demande) {
        Map<String, String> demandeMap = new HashMap<>();

        demandeMap.put("col_date", demande.getDateDemandeFormatee());
        demandeMap.put("col_date_modif", demande.getDateModifFormatee());

        switch (demande.getTypesDemandes().getIdTypeDemande()) {
            case Constant.TYPE_DEMANDE_CORRECTION:
                demandeMap.put(Constant.COL_DEMANDE_TYPE, "COR");
                break;
            case Constant.TYPE_DEMANDE_NUMEROTATION:
                demandeMap.put(Constant.COL_DEMANDE_TYPE, "NUM");
                break;
            case Constant.TYPE_DEMANDE_CREATION:
                demandeMap.put(Constant.COL_DEMANDE_TYPE, "CRE");
                break;
            default:
                break;
        }

        demandeMap.put("col_demande_num", demande.getIdDemande().toString());
        demandeMap.put("col_ppn", (demande.getNotice() != null) ? demande.getNotice().getPpn() : "");
        demandeMap.put("col_titre", demande.getTitre());
        demandeMap.put("col_etat", demande.getEtatsDemandes().getLibelleEtatDemande());
        demandeMap.put("col_iln", demande.getIln());
        demandeMap.put("col_issn", demande.getConditionnalIssn());
        demandeMap.put("col_frbnf", (demande.getNotice() != null) ? demande.getNotice().getFrbnf() : "");
        demandeMap.put("col_publication_type", (demande.getNotice() != null) ? demande.getNotice().getTypeRessource() : "");
        demandeMap.put("col_support_type", (demande.getNotice() != null) ? demande.getNotice().getTypeDocumentLibelle() : "");
        demandeMap.put("col_publication_pays", (demande.getNotice() != null) ? demande.getNotice().getPays() : "");
        demandeMap.put("col_statut_de_vie", (demande.getNotice() != null) ? (demande.getNotice().getStatutdevie() ? "Mort" : "Vivant") : "");
        demandeMap.put("col_publication_date", (demande.getNotice() != null) ? demande.getNotice().getDatePublication() : "");
        demandeMap.put("col_rcr", demande.getRcrDemandeur());

        return demandeMap;
    }
}
