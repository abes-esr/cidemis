package fr.abes.cidemis.service;

import fr.abes.cidemis.exception.DaoException;
import fr.abes.cidemis.model.cidemis.CbsUsers;
import fr.abes.cidemis.model.cidemis.Commentaires;
import fr.abes.cidemis.model.cidemis.Demandes;

import java.util.List;

public interface ICommentairesService {
    Boolean save(Commentaires commentaires) throws DaoException;

    Boolean delete(Commentaires commentaires);

    Commentaires findCommentaires(Integer idCommentaire);

    List<Commentaires> findAllCommentaires();

    List<Commentaires> findCommentairesByDemandes(Demandes demandes);

    Commentaires findLastCommentairesByDemande(Demandes demande);

    List<Commentaires> findCommentairesByCbsUsers(CbsUsers cbsUsers);

    Integer getNbCommentairesByDemande(Demandes de);

    Integer getNbCommentairesISSNByDemande(Demandes de);
}
