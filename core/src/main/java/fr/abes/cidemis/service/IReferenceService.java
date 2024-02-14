package fr.abes.cidemis.service;

import fr.abes.cidemis.model.cidemis.EtatsDemandes;
import fr.abes.cidemis.model.cidemis.TypesDemandes;
import fr.abes.cidemis.model.cidemis.ZoneCorrection;

import java.util.List;

public interface IReferenceService {

    Boolean save(TypesDemandes typesdemandes);

    Boolean delete(TypesDemandes typesdemandes);

    TypesDemandes findTypesdemandes(Integer idTypedemande);

    List<TypesDemandes> findAllTypesdemandes();

    Boolean save(EtatsDemandes etatsdemandes);

    Boolean delete(EtatsDemandes etatsdemandes);

    EtatsDemandes findEtatsdemandes(Integer idEtatDemande);

    List<EtatsDemandes> findAllEtatsdemandes();

    List<String> findAllEtatsdemandesLib();

    List<ZoneCorrection> findAllZonesCorrection();
}
