package fr.abes.cidemis.service.impl;

import fr.abes.cidemis.dao.cidemis.CidemisDaoProvider;
import fr.abes.cidemis.model.cidemis.DefaultTaggues;
import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.model.cidemis.Taggues;
import fr.abes.cidemis.service.ITagguesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TagguesService implements ITagguesService {
    @Autowired
    private CidemisDaoProvider dao;

    /**
     *
     * services de la table taggues
     */
    @Override
    public Boolean save(Taggues taggues) {
        if (taggues == null) {
            log.error( "Erreur dans le parametre d'entrée , la variable 'taggues' vaut 'null'.");
            return false;
        }

        try {
            this.dao.getTagguesDao().save(taggues);
            log.info( "Sauvegarde: Taggues. Id_Taggue:" + taggues.getIdTaggue().toString() + " OK");
            return true;
        } catch (Exception ex) {
            log.error( "function save(). Commentaires. Id_commentaire:" + taggues.getIdTaggue().toString(),
                    ex);
            return false;
        }
    }

    @Override
    public Boolean delete(Taggues taggues) {
        if (taggues == null) {
            log.error( "Erreur dans le parametre d'entrée , la variable 'taggues' vaut 'null'.");
            return false;
        }

        try {
            this.dao.getTagguesDao().delete(taggues);
            return true;
        } catch (Exception ex) {
            log.error( "function delete(). Taggues. Id_taggue:" + taggues.getIdTaggue().toString(), ex);
            return false;
        }
    }

    @Override
    public Taggues findTaggues(Integer idTaggue) {
        Optional<Taggues> taggues = this.dao.getTagguesDao().findById(idTaggue);
        if (!taggues.isPresent())
            log.warn(
                    "La fonction 'findTaggues' n'a retournée aucun résultat. Id_commentaire: " + idTaggue);
        return taggues.get();
    }

    @Override
    public List<Taggues> findAllTaggues() {
        List<Taggues> tagguesList = this.dao.getTagguesDao().findAll();

        if (tagguesList.isEmpty())
            log.debug( "La fonction 'findAllTaggues' n'a retournée aucun résultat.");

        return tagguesList;
    }

    @Override
    public Taggues findTagguesByDemandes(Demandes demandes) {
        return this.dao.getTagguesDao().findAllByDemande(demandes);
    }

    /**
     *
     * services de la table taggues
     */
    @Override
    public List<DefaultTaggues> findAllDefaultTaggues() {
        List<DefaultTaggues> tagguesList = this.dao.getDefaultTagguesDao().findAll();

        if (tagguesList.isEmpty())
            log.warn( "La fonction 'findAllDefaultTaggues' n'a retournée aucun résultat.");

        return tagguesList;
    }

    @Override
    public DefaultTaggues findDefaultTagguesByLibelle(String libelle) {
        return this.dao.getDefaultTagguesDao().findByLibelleTaggue(libelle);
    }


}
