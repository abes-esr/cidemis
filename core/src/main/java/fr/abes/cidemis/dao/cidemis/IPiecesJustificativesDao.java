package fr.abes.cidemis.dao.cidemis;

import fr.abes.cidemis.model.cidemis.CbsUsers;
import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.model.cidemis.PiecesJustificatives;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IPiecesJustificativesDao extends JpaRepository<PiecesJustificatives, Integer> {
    List<PiecesJustificatives> findAllByDemande(Demandes demande);

    List<PiecesJustificatives> findAllByCbsUsers(CbsUsers cbsUser);

    Integer countAllByDemande(Demandes demandes);

    @Query(value = "SELECT SEQ_PIECES_JUSTIFICATIVES.nextval FROM dual", nativeQuery = true)
    Integer getNextSeriesId();
}
