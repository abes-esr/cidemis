package fr.abes.cidemis.dao.cidemis;

import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.model.cidemis.Taggues;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITagguesDao extends JpaRepository<Taggues, Integer> {
    Taggues findAllByDemande(Demandes demande);
}
