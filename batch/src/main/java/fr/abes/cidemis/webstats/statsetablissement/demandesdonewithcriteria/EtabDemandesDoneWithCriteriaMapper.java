package fr.abes.cidemis.webstats.statsetablissement.demandesdonewithcriteria;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtabDemandesDoneWithCriteriaMapper implements RowMapper<EtabDemandesDoneWithCriteriaDto> {
    @Override
    public EtabDemandesDoneWithCriteriaDto mapRow(ResultSet resultSet, int i) throws SQLException {
        EtabDemandesDoneWithCriteriaDto demande = new EtabDemandesDoneWithCriteriaDto();
        demande.setIlnRattache(resultSet.getString("ILN_RATTACHE"));
        demande.setRcrDemandeur(resultSet.getString("RCR_DEMANDEUR"));
        demande.setIdTypeDemande(resultSet.getInt("ID_TYPEDEMANDE"));
        demande.setIdEtatDemande(resultSet.getInt("ID_ETATDEMANDE"));
        demande.setPays(resultSet.getString("PAYS"));
        demande.setTypeDocument((resultSet.getString("TYPE_DOCUMENT") != null) ? resultSet.getString("TYPE_DOCUMENT").substring(0, 1) : "");
        demande.setTypeRessourceContinue(resultSet.getString("TYPE_RESSOURCE_CONTINUE"));
        demande.setDateFin((resultSet.getString("DATE_FIN") != null && !resultSet.getString("DATE_FIN").isEmpty()) ? "1" : "0");
        demande.setCount(resultSet.getInt("COUNT"));
        return demande;
    }
}
