package fr.abes.cidemis.dao.cidemis;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.abes.cidemis.model.cidemis.CbsUsers;
import fr.abes.cidemis.model.cidemis.Roles;


public interface ICbsUsersDao extends JpaRepository<CbsUsers, Integer> {
    List<CbsUsers> findCbsUsersByRoles(Roles role);

    CbsUsers findCbsUsersByUserKey(String userKey);

    @Query("select case when (count(user) > 0)  then true else false end from CbsUsers user where user.userKey = :userKey")
    boolean knowIfUserIsCreated(@Param("userKey") String userKey);
}
