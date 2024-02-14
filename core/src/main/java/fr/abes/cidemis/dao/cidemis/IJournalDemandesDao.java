package fr.abes.cidemis.dao.cidemis;

import fr.abes.cidemis.model.cidemis.CbsUsers;
import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.model.cidemis.EtatsDemandes;
import fr.abes.cidemis.model.cidemis.JournalDemandes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IJournalDemandesDao extends JpaRepository<JournalDemandes, Integer> {
    List<JournalDemandes> findAllByDemandes(Demandes demande);

    List<JournalDemandes> findAllByEtatsdemandes(EtatsDemandes etat);

    List<JournalDemandes> findAllByCbsUsers(CbsUsers user);

    @Query(value = "SELECT SEQ_JOURNAL_DEMANDES.nextval FROM dual", nativeQuery = true)
    Integer getNextSeriesId();
}
