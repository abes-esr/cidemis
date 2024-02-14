package fr.abes.cidemis.dao.cidemis;

import fr.abes.cidemis.model.cidemis.TypesDemandes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITypesDemandesDao extends JpaRepository<TypesDemandes, Integer> {
}
