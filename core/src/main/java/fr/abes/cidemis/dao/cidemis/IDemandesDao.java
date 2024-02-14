package fr.abes.cidemis.dao.cidemis;

import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.model.cidemis.EtatsDemandes;
import fr.abes.cidemis.model.cidemis.TypesDemandes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface IDemandesDao extends JpaRepository<Demandes, Integer> {
    //Utilisateur ROLE ABES
    @Query("select d from Demandes d left join fetch d.taggues where d.etatsDemandes.idEtatDemande NOT IN(36, 37, 38, 39)")
    List<Demandes> findAllExceptAchievedAndArchived();
    @Query("select d from Demandes d left join fetch d.taggues where d.etatsDemandes.idEtatDemande NOT IN(36, 37, 38)")
    List<Demandes> findAllExceptDone();
    @Query("select d from Demandes d left join fetch d.taggues where d.etatsDemandes.idEtatDemande NOT IN(39)")
    List<Demandes> findAllExceptArchived();
    @Query("select d from Demandes d left join fetch d.taggues")
    List<Demandes> findAll();

    //Utilisateur ROLE CATALOGUEUR
    @Query("select d from Demandes d left join fetch d.taggues where d.cbsUsers.userNum = :cbsUserNum and d.etatsDemandes.idEtatDemande NOT IN(36, 37, 38, 39)")
    List<Demandes> findDemandesByCbsUsersExceptAchievedAndArchived(@Param("cbsUserNum") Integer cbsUserNum);
    @Query("select d from Demandes d left join fetch d.taggues where d.cbsUsers.userNum = :cbsUserNum and d.etatsDemandes.idEtatDemande NOT IN(36, 37, 38)")
    List<Demandes> findDemandesByCbsUsersExceptDone(@Param("cbsUserNum") Integer cbsUserNum);
    @Query("select d from Demandes d left join fetch d.taggues where d.cbsUsers.userNum = :cbsUserNum and d.etatsDemandes.idEtatDemande NOT IN(39)")
    List<Demandes> findDemandesByCbsUsersExceptArchived(@Param("cbsUserNum") Integer cbsUserNum);
    @Query("select d from Demandes d left join fetch d.taggues where d.cbsUsers.userNum = :cbsUserNum")
    List<Demandes> findDemandesByCbsUsersAll(@Param("cbsUserNum") Integer cbsUserNum);

    //Utilisateur ROLE RESPONSABLE_CR
    @Query("select d from Demandes d left join fetch d.taggues where d.cr = :cr and d.etatsDemandes.idEtatDemande NOT IN(22, 36, 37, 38, 39)")
    List<Demandes> findDemandesByCrExceptAchievedAndArchived(@Param("cr") String cr);
    @Query("select d from Demandes d left join fetch d.taggues where d.cr = :cr and d.etatsDemandes.idEtatDemande NOT IN(22, 36, 37, 38)")
    List<Demandes> findDemandesByCrExceptDone(@Param("cr") String cr);
    @Query("select d from Demandes d left join fetch d.taggues where d.cr = :cr and d.etatsDemandes.idEtatDemande NOT IN(22, 39)")
    List<Demandes> findDemandesByCrExceptArchived(@Param("cr") String cr);
    @Query("select d from Demandes d left join fetch d.taggues where d.cr = :cr and d.etatsDemandes.idEtatDemande NOT IN(22)")
    List<Demandes> findDemandesByCrAll(@Param("cr") String cr);

    //Utilisateur ROLE ISSN
    @Query("select d from Demandes d left join fetch d.taggues where d.notice.pays in ('FR', 'GF', 'GP', 'MQ', 'NC', 'PF' , 'PM', 'RE', 'SM', 'WF', 'YT') "
            + "and d.etatsDemandes.idEtatDemande IN (25, 28, 35) ")
    List<Demandes> findDemandesISSNExceptAchievedAndArchived();
    @Query("select d from Demandes d left join fetch d.taggues where d.notice.pays in ('FR', 'GF', 'GP', 'MQ', 'NC', 'PF' , 'PM', 'RE', 'SM', 'WF', 'YT') "
            + "and d.etatsDemandes.idEtatDemande IN (25, 28, 35, 39) ")
    List<Demandes> findDemandesISSNExceptDone();
    @Query("select d from Demandes d left join fetch d.taggues where d.notice.pays in ('FR', 'GF', 'GP', 'MQ', 'NC', 'PF' , 'PM', 'RE', 'SM', 'WF', 'YT') "
            + "and d.etatsDemandes.idEtatDemande IN (25, 28, 35, 36, 37) ")
    List<Demandes> findDemandesISSNExceptArchived();
    @Query("select d from Demandes d left join fetch d.taggues where d.notice.pays in ('FR', 'GF', 'GP', 'MQ', 'NC', 'PF' , 'PM', 'RE', 'SM', 'WF', 'YT') ")
    List<Demandes> findDemandesISSNAll();

    @Query("select d from Demandes d left join fetch d.taggues where d.dateDemande between :borneInf and :borneSup and d.etatsDemandes.idEtatDemande = 36 and d.notice.pays NOT IN ('FR', 'GF', 'GP', 'MQ', 'NC', 'PF', 'PM', 'RE', 'SM', 'WF', 'YT')")
    List<Demandes> findAllRefuseByCieps(@Param("borneInf") Date borneInf, @Param("borneSup") Date borneSup);

    //Utilisateur ROLE CIEPS
    @Query("select d from Demandes d left join fetch d.taggues where d.notice.pays NOT IN('FR', 'GF', 'GP', 'MQ', 'NC', 'PF', 'PM', 'RE', 'SM', 'WF', 'YT') and " +
            "d.etatsDemandes.idEtatDemande IN(26, 28, 33, 35) " +
            "and d.typesDemandes.idTypeDemande NOT IN (24)")
    List<Demandes> findDemandesCIEPSExceptAchievedAndArchived();
    @Query("select d from Demandes d left join fetch d.taggues where d.notice.pays NOT IN('FR', 'GF', 'GP', 'MQ', 'NC', 'PF', 'PM', 'RE', 'SM', 'WF', 'YT') and " +
            "d.etatsDemandes.idEtatDemande IN(26, 28, 33, 35, 39) " +
            "and d.typesDemandes.idTypeDemande NOT IN (24)")
    List<Demandes> findDemandesCIEPSExceptDone();
    @Query("select d from Demandes d left join fetch d.taggues where d.notice.pays NOT IN('FR', 'GF', 'GP', 'MQ', 'NC', 'PF', 'PM', 'RE', 'SM', 'WF', 'YT') and " +
            "d.etatsDemandes.idEtatDemande IN(26, 28, 33, 35, 36, 37, 38) " +
            "and d.typesDemandes.idTypeDemande NOT IN (24)")
    List<Demandes> findDemandesCIEPSExceptArchived();
    @Query("select d from Demandes d left join fetch d.taggues where d.notice.pays NOT IN('FR', 'GF', 'GP', 'MQ', 'NC', 'PF', 'PM', 'RE', 'SM', 'WF', 'YT') and " +
            "d.etatsDemandes.idEtatDemande IN(26, 28, 33, 35, 36, 37, 39) " +
            "and d.typesDemandes.idTypeDemande NOT IN (24)")
    List<Demandes> findDemandesCIEPSAll();

    //Autres requêtes affichage des demandes
    List<Demandes> findDemandesByTypesDemandes(TypesDemandes type);
    List<Demandes> findDemandesByEtatsDemandes(EtatsDemandes etat);

    @Query("select d from Demandes d where d.notice.pays NOT IN('FR', 'GF', 'GP', 'MQ', 'NC', 'PF', 'PM', 'RE', 'SM', 'WF', 'YT') and d.etatsDemandes.idEtatDemande in (33, 26) and d.typesDemandes.idTypeDemande != 24")
    List<Demandes> findDemandesCIEPSForMailing();

    @Query("select d from Demandes d left join fetch d.taggues where d.notice.ppn = :ppn")
    List<Demandes> findDemandesByPPN(@Param("ppn") String ppn);

    @Query("select d from Demandes d left join fetch d.taggues where d.issn = :issn and d.notice.ppn != :ppn")
    List<Demandes> findDemandesByISSN(@Param("issn") String issn, @Param("ppn") String ppn);

    @Query(value = "SELECT SEQ_DEMANDES.nextval FROM dual", nativeQuery = true)
    Integer getNextSeriesId();

}
