package fr.abes.cidemis.dao.cidemis;

import fr.abes.cidemis.model.cidemis.DefaultTaggues;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDefaultTagguesDao extends JpaRepository<DefaultTaggues, Integer> {
    DefaultTaggues findByLibelleTaggue(String libelle);
}
