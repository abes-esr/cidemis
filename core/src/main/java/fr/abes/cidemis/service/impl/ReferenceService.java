package fr.abes.cidemis.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import fr.abes.cidemis.dao.cidemis.CidemisDaoProvider;
import fr.abes.cidemis.model.cidemis.EtatsDemandes;
import fr.abes.cidemis.model.cidemis.TypesDemandes;
import fr.abes.cidemis.model.cidemis.ZoneCorrection;
import fr.abes.cidemis.service.IReferenceService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReferenceService implements IReferenceService {
    private final CidemisDaoProvider dao;

    public ReferenceService(CidemisDaoProvider dao) {
        this.dao = dao;
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
