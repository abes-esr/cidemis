package fr.abes.cidemis.service;

import fr.abes.cidemis.model.cidemis.DefaultTaggues;
import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.model.cidemis.Taggues;

import java.util.List;

public interface ITagguesService {
    Boolean save(Taggues taggues);

    Boolean delete(Taggues taggues);

    Taggues findTaggues(Integer idTaggue);

    List<Taggues> findAllTaggues();

    Taggues findTagguesByDemandes(Demandes demandes);

    List<DefaultTaggues> findAllDefaultTaggues();

    DefaultTaggues findDefaultTagguesByLibelle(String libelle);
}
