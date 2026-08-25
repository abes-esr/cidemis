package fr.abes.cidemis.service;

import java.util.List;

import fr.abes.cidemis.model.cidemis.EtatsDemandes;
import fr.abes.cidemis.model.cidemis.TypesDemandes;
import fr.abes.cidemis.model.cidemis.ZoneCorrection;

public interface IReferenceService {

    TypesDemandes findTypesdemandes(Integer idTypedemande);

    List<TypesDemandes> findAllTypesdemandes();

    EtatsDemandes findEtatsdemandes(Integer idEtatDemande);

    List<EtatsDemandes> findAllEtatsdemandes();

    List<String> findAllEtatsdemandesLib();

    List<ZoneCorrection> findAllZonesCorrection();
}
