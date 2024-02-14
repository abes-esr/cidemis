package fr.abes.cidemis.dao.cidemis;

import fr.abes.cidemis.model.cidemis.CbsUsers;
import fr.abes.cidemis.model.cidemis.Commentaires;
import fr.abes.cidemis.model.cidemis.Demandes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ICommentairesDao extends JpaRepository<Commentaires, Integer> {
    Commentaires findFirstByDemandeOrderByDateCommentaireDesc(Demandes d);

    List<Commentaires> findCommentairesByDemandeOrderByDateCommentaire(Demandes d);

    List<Commentaires> findCommentairesByCbsUsersOrderByDateCommentaire(CbsUsers user);

    Optional<Commentaires> findFirstByCbsUsersAndDemandeOrderByDateCommentaireDesc(CbsUsers user, Demandes demande);

    Integer countAllByDemande(Demandes demandes);

    @Query("from Commentaires c where c.demande=:demandes and c.visibleISSN=true")
    Integer countAllByDemandeAndVisibleISSN(Demandes demandes);
}
