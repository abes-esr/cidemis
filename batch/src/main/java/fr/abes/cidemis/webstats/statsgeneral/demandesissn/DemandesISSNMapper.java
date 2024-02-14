package fr.abes.cidemis.webstats.statsgeneral.demandesissn;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DemandesISSNMapper implements RowMapper<DemandesISSNDto> {
    @Override
    public DemandesISSNDto mapRow(ResultSet resultSet, int i) throws SQLException {
        DemandesISSNDto demandes = new DemandesISSNDto();
        demandes.setIdTypeDemande(Integer.toString(resultSet.getInt("ID_TYPEDEMANDE")));
        demandes.setStatus(resultSet.getString("STATUS"));
        demandes.setCount(resultSet.getString("COUNT"));
        return demandes;
    }
}
