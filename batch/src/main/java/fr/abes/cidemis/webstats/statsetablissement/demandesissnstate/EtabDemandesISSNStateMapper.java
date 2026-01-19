package fr.abes.cidemis.webstats.statsetablissement.demandesissnstate;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtabDemandesISSNStateMapper implements RowMapper<EtabDemandesISSNStateDto> {
    @Override
    public EtabDemandesISSNStateDto mapRow(ResultSet resultSet, int i) throws SQLException {
        EtabDemandesISSNStateDto demande = new EtabDemandesISSNStateDto();
        demande.setIln(resultSet.getString("ILN"));
        demande.setRcrDemandeur(resultSet.getString("RCR_DEMANDEUR"));
        demande.setIdTypeDemande(resultSet.getInt("ID_TYPEDEMANDE"));
        demande.setStatus(resultSet.getString("STATUS"));
        demande.setCount(resultSet.getInt("COUNT"));
        return demande;
    }
}
