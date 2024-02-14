package fr.abes.cidemis.webstats.statsetablissement.demandescorrection;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtabDemandesCorrectionMapper implements RowMapper<EtabDemandesCorrectionDto> {
    @Override
    public EtabDemandesCorrectionDto mapRow(ResultSet resultSet, int i) throws SQLException {
        EtabDemandesCorrectionDto demande = new EtabDemandesCorrectionDto();
        demande.setIlnRattache(resultSet.getString("ILN_RATTACHE"));
        demande.setRcrDemandeur(resultSet.getString("RCR_DEMANDEUR"));
        demande.setZones(resultSet.getString("ZONES"));
        demande.setIdEtatDemande(resultSet.getInt("ID_ETATDEMANDE"));
        demande.setCount(resultSet.getInt("COUNT"));
        return demande;
    }
}
