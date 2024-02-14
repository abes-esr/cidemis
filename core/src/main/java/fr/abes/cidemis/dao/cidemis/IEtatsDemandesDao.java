package fr.abes.cidemis.dao.cidemis;

import fr.abes.cidemis.model.cidemis.EtatsDemandes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IEtatsDemandesDao extends JpaRepository<EtatsDemandes, Integer> {
    @Query("select e.libelleEtatDemande from EtatsDemandes e")
    List<String> findAllEtatsdemandesLib();

}
