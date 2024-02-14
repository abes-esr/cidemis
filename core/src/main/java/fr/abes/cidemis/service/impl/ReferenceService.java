package fr.abes.cidemis.service.impl;

import fr.abes.cidemis.dao.cidemis.CidemisDaoProvider;
import fr.abes.cidemis.model.cidemis.EtatsDemandes;
import fr.abes.cidemis.model.cidemis.TypesDemandes;
import fr.abes.cidemis.model.cidemis.ZoneCorrection;
import fr.abes.cidemis.service.CidemisManageService;
import fr.abes.cidemis.service.IReferenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ReferenceService implements IReferenceService {
    @Autowired
    private CidemisDaoProvider dao;

    @Autowired
    private CidemisManageService service;


    /**
     * types demandes
     */

    @Override
    public Boolean save(TypesDemandes typesdemandes) {
        if (typesdemandes == null) {
            log.error("Erreur dans le parametre d'entrée , la variable 'typesdemandes' vaut 'null'.");
            return false;
        }
        try {
            this.dao.getTypesDemandesDao().save(typesdemandes);
            log.info("Sauvegarde: Typesdemandes. Id_typedemande:"
                    + typesdemandes.getIdTypeDemande().toString() + " OK");


            if (!this.service.getDemande().saveDemandesListByTypesdemandes(typesdemandes))
                log.warn("Erreur dans l'ajout des Demandess. Typesdemandes. Id_typedemande:"
                        + typesdemandes.getIdTypeDemande().toString() + "'");
            else
                log.info("Les Demandess ont bien été ajouté(e)s .Typesdemandes. Id_typedemande:"
                        + typesdemandes.getIdTypeDemande().toString() + "'");
            return true;
        } catch (Exception ex) {
            log.error(
                    "function save(). Typesdemandes. Id_typedemande:" + typesdemandes.getIdTypeDemande().toString(),
                    ex);
            return false;
        }
    }


    @Override
    public Boolean delete(TypesDemandes typesdemandes) {
        if (typesdemandes == null) {
            log.error("Erreur dans le parametre d'entrée , la variable 'typesdemandes' vaut 'null'.");
            return false;
        }

        try {
            this.dao.getTypesDemandesDao().delete(typesdemandes);
            return true;
        } catch (Exception ex) {
            log.error(
                    "function delete(). Typesdemandes. Id_typedemande:" + typesdemandes.getIdTypeDemande().toString(),
                    ex);
            return false;
        }
    }

    @Override
    public TypesDemandes findTypesdemandes(Integer idTypedemande) {
        Optional<TypesDemandes> typesdemandes = this.dao.getTypesDemandesDao().findById(idTypedemande);

        if (!typesdemandes.isPresent())
            log.warn(
                    "La fonction 'findTypesdemandes' n'a retournée aucun résultat. Id_typedemande:" + idTypedemande);

        return typesdemandes.get();
    }

    @Override
    public List<TypesDemandes> findAllTypesdemandes() {
        List<TypesDemandes> typesdemandesList = this.dao.getTypesDemandesDao().findAll();

        if (typesdemandesList.isEmpty())
            log.warn("La fonction 'findAllTypesdemandes' n'a retournée aucun résultat.");

        return typesdemandesList;
    }

    @Override
    public Boolean save(EtatsDemandes etatsdemandes) {
        if (etatsdemandes == null) {
            log.error("Erreur dans le parametre d'entrée , la variable 'etatsdemandes' vaut 'null'.");
            return false;
        }

        try {
            this.dao.getEtatsDemandesDao().save(etatsdemandes);
            log.info("Sauvegarde: Etatsdemandes. Id_etatdemande:"
                    + etatsdemandes.getIdEtatDemande().toString() + " OK");

            if (!this.service.getDemande().saveDemandesListByEtatsdemandes(etatsdemandes))
                log.warn("Erreur dans l'ajout des Demandess. Etatsdemandes. Id_etatdemande:"
                        + etatsdemandes.getIdEtatDemande().toString() + "'");
            else
                log.info("Les Demandess ont bien été ajouté(e)s .Etatsdemandes. Id_etatdemande:"
                        + etatsdemandes.getIdEtatDemande().toString() + "'");

            if (!this.service.getDemande().saveJournalDemandesListByEtatsdemandes(etatsdemandes))
                log.warn("Erreur dans l'ajout des Journal_Demandess. Etatsdemandes. Id_etatdemande:"
                        + etatsdemandes.getIdEtatDemande().toString() + "'");
            else
                log.info("Les Journal_Demandess ont bien été ajouté(e)s .Etatsdemandes. Id_etatdemande:"
                        + etatsdemandes.getIdEtatDemande().toString() + "'");

            return true;
        } catch (Exception ex) {
            log.error(
                    "function save(). Etatsdemandes. Id_etatdemande:" + etatsdemandes.getIdEtatDemande().toString(),
                    ex);
            return false;
        }
    }


    @Override
    public Boolean delete(EtatsDemandes etatsdemandes) {
        if (etatsdemandes == null) {
            log.error("Erreur dans le parametre d'entrée , la variable 'etatsdemandes' vaut 'null'.");
            return false;
        }

        try {
            this.dao.getEtatsDemandesDao().delete(etatsdemandes);
            return true;
        } catch (Exception ex) {
            log.error(
                    "function delete(). Etatsdemandes. Id_etatdemande:" + etatsdemandes.getIdEtatDemande().toString(),
                    ex);
            return false;
        }
    }

    @Override
    public EtatsDemandes findEtatsdemandes(Integer idEtatDemande) {
        Optional<EtatsDemandes> etatsdemandes = this.dao.getEtatsDemandesDao().findById(idEtatDemande);

        if (!etatsdemandes.isPresent())
            log.warn(
                    "La fonction 'findEtatsdemandes' n'a retourné aucun résultat. Id_etatdemande:" + idEtatDemande);

        return etatsdemandes.get();
    }

    @Override
    public List<EtatsDemandes> findAllEtatsdemandes() {
        List<EtatsDemandes> etatsdemandesList = this.dao.getEtatsDemandesDao().findAll();

        if (etatsdemandesList.isEmpty())
            log.debug("La fonction 'findAllEtatsdemandes' n'a retourné aucun résultat.");

        return etatsdemandesList;
    }

    @Override
    public List<String> findAllEtatsdemandesLib() {
        List<String> etatsdemandesList = this.dao.getEtatsDemandesDao().findAllEtatsdemandesLib();

        if (etatsdemandesList.isEmpty())
            log.debug("La fonction 'findAllEtatsdemandesLib' n'a retourné aucun résultat.");

        return etatsdemandesList;
    }

    @Override
    public List<ZoneCorrection> findAllZonesCorrection() {
        List<ZoneCorrection> zoneCorrectionsList = this.dao.getZoneCorrectionDao().findAllOrderByZone();
        if (zoneCorrectionsList.isEmpty())
            log.debug("La fonction 'findAllZonesCorrection' n'a retourné aucun résultat");

        return zoneCorrectionsList;

    }
}
