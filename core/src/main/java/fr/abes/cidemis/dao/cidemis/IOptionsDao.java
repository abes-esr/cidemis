package fr.abes.cidemis.dao.cidemis;

import fr.abes.cidemis.model.cidemis.CbsUsers;
import fr.abes.cidemis.model.cidemis.Options;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IOptionsDao extends JpaRepository<Options, Integer> {
    List<Options> findOptionsByCbsUsers(CbsUsers user);

    @Query("select o from Options o where o.cbsUsers = :user and o.type = 'colonne' order by o.value")
    List<Options> findOptionsColonnesByCbsUsers(@Param("user") CbsUsers user);
}
