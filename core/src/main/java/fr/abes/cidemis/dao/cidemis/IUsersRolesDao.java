package fr.abes.cidemis.dao.cidemis;

import fr.abes.cidemis.model.cidemis.UsersRoles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IUsersRolesDao extends JpaRepository<UsersRoles, Integer> {
    List<UsersRoles> findUsersRolesByUserGroup(String userGroup);
}
