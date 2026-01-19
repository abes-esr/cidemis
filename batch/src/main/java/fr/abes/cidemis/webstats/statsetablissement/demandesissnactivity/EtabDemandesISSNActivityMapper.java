package fr.abes.cidemis.webstats.statsetablissement.demandesissnactivity;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtabDemandesISSNActivityMapper implements RowMapper<EtabDemandesISSNActivityDto> {
    @Override
    public EtabDemandesISSNActivityDto mapRow(ResultSet resultSet, int i) throws SQLException {
        EtabDemandesISSNActivityDto demande = new EtabDemandesISSNActivityDto();
        demande.setIln(resultSet.getString("ILN"));
        demande.setRcrDemandeur(resultSet.getString("RCR_DEMANDEUR"));
        demande.setIdTypeDemande(resultSet.getInt("ID_TYPEDEMANDE"));
        demande.setIdEtatDemande(resultSet.getString("ID_ETATDEMANDE"));
        demande.setCount(resultSet.getInt("COUNT"));
        return demande;
    }
}
