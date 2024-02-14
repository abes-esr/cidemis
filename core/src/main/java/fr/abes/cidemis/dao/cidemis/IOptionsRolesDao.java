package fr.abes.cidemis.dao.cidemis;

import fr.abes.cidemis.model.cidemis.OptionsRoles;
import fr.abes.cidemis.model.cidemis.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IOptionsRolesDao extends JpaRepository<OptionsRoles, Integer> {
    List<OptionsRoles> findAllByRoles(Roles role);
}
