package fr.abes.cidemis.service.impl;

import fr.abes.cidemis.dao.cidemis.CidemisDaoProvider;
import fr.abes.cidemis.model.cidemis.CbsUsers;
import fr.abes.cidemis.model.cidemis.Commentaires;
import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.service.CidemisManageService;
import fr.abes.cidemis.service.ICommentairesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CommentairesService implements ICommentairesService {

    @Autowired
    private CidemisDaoProvider dao;
    @Autowired
    private CidemisManageService service;


    /**
     *
     * Services de la table Commentaires
     */
    @Override
    public Boolean save(Commentaires commentaires) {
        if (commentaires == null) {
            log.error("Erreur dans le parametre d'entrée , la variable 'commentaires' vaut 'null'.");
            return false;
        }

        try {
            this.dao.getCommentairesDao().save(commentaires);
            log.info("Sauvegarde: Commentaires. Id_commentaire:" + commentaires.getIdCommentaire().toString() + " OK");
            return true;
        } catch (Exception ex) {
            log.error("function save(). Commentaires. Id_commentaire:" + commentaires.getIdCommentaire().toString(), ex);
            return false;
        }
    }

    @Override
    public Boolean delete(Commentaires commentaires) {
        if (commentaires == null) {
            log.error("Erreur dans le parametre d'entrée , la variable 'commentaires' vaut 'null'.");
            return false;
        }

        try {
            this.dao.getCommentairesDao().delete(commentaires);
            return true;
        } catch (Exception ex) {
            log.error("function delete(). Commentaires. Id_commentaire:" + commentaires.getIdCommentaire().toString(), ex);
            return false;
        }
    }

    @Override
    public Commentaires findCommentaires(Integer idCommentaire) {
        Optional<Commentaires> commentaires = this.dao.getCommentairesDao().findById(idCommentaire);
        if (!commentaires.isPresent())
            log.debug("La fonction 'findCommentaires' n'a retournée aucun résultat. Id_commentaire: " + idCommentaire);
        return commentaires.get();
    }

    @Override
    public List<Commentaires> findAllCommentaires() {
        List<Commentaires> commentairesList = this.dao.getCommentairesDao().findAll();

        if (commentairesList.isEmpty())
            log.debug("La fonction 'findAllCommentaires' n'a retournée aucun résultat.");

        return commentairesList;
    }

    @Override
    public List<Commentaires> findCommentairesByDemandes(Demandes demandes) {
        return this.dao.getCommentairesDao().findCommentairesByDemandeOrderByDateCommentaire(demandes);
    }

    @Override
    public Commentaires findLastCommentairesByDemande(Demandes demande) {
        Commentaires commentaire = this.dao.getCommentairesDao().findFirstByDemandeOrderByDateCommentaireDesc(demande);

        if (commentaire == null)
            log.debug("La fonction 'findLastCommentairesByDemande' n'a retournée aucun résultat. Id_demande:"
                            + demande.getIdDemande().toString());

        return commentaire;
    }

    @Override
    public List<Commentaires> findCommentairesByCbsUsers(CbsUsers cbsUsers) {
        List<Commentaires> commentairesList = this.dao.getCommentairesDao().findCommentairesByCbsUsersOrderByDateCommentaire(cbsUsers);

        if (commentairesList.isEmpty())
            log.debug("La fonction 'findCommentairesByCbs_Users' n'a retournée aucun résultat. User_num:"
                    + cbsUsers.getUserNum().toString());

        return commentairesList;
    }

    @Override
    public Integer getNbCommentairesByDemande(Demandes de) {
        return this.dao.getCommentairesDao().countAllByDemande(de);
    }

    @Override
    public Integer getNbCommentairesISSNByDemande(Demandes de) {
        return this.dao.getCommentairesDao().countAllByDemandeAndVisibleISSN(de);
    }
}
