package fr.abes.cidemis.dao.cidemis;

import fr.abes.cidemis.model.cidemis.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRolesDao extends JpaRepository<Roles, Integer> {
}
