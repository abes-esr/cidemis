package fr.abes.cidemis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.cbs.exception.CBSException;
import fr.abes.cbs.exception.ZoneException;
import fr.abes.cidemis.model.cidemis.*;
import fr.abes.cidemis.model.dto.DemandeDto;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IDemandesService {
    Demandes save(Demandes demandes);

    boolean delete(Demandes demandes);

    Demandes findDemande(Integer idDemande);

    List<Demandes> findDemandesByCbsUsers(CbsUsers cbsUsers, boolean demandesAchieved, boolean demandesArchived);

    List<Demandes> findDemandesByPPN(String ppn);

    List<Demandes> findDemandesByISSN(String issn, String ppn);

    List<Demandes> findDemandesMailingCieps();

    List<Demandes> findDemandesMailingCiepsForMailing();

    List<Demandes> findDemandesForUpdateRefus(Date borneInf, Date borneSup);

    void archiverDemande(Demandes demande, CbsUsers user);

    boolean canUserModifyDemande(CbsUsers user, Demandes demande);

    boolean canUserArchiveDemande(CbsUsers user, Demandes demande);

    boolean canUserDeleteDemande(CbsUsers user, Demandes demande);

    boolean save(JournalDemandes journalDemandes);

    boolean delete(JournalDemandes journalDemandes);

    List<JournalDemandes> findAllJournalDemandes();

    List<JournalDemandes> findJournalDemandesByEtatsdemandes(EtatsDemandes etatsdemandes);

    List<JournalDemandes> findJournalDemandesByDemandes(Demandes demandes);

    List<JournalDemandes> findJournalDemandesByCbsUsers(CbsUsers cbsUsers);

    boolean saveJournalDemandesListByEtatsdemandes(EtatsDemandes etatsdemandes);

    boolean saveDemandesListByEtatsdemandes(EtatsDemandes etatsdemandes);

    boolean saveDemandesListByTypesdemandes(TypesDemandes typesdemandes);

    Demandes creerDemande(DemandeDto demande, CbsUsers user, RegistryUser registryuser, String url, String port, String password, String path) throws ZoneException, CBSException, IOException;

    Map<String, String> getDemandemap(Demandes demande);

    void envoiMail(Demandes demande, CbsUsers user) throws JsonProcessingException;

}
