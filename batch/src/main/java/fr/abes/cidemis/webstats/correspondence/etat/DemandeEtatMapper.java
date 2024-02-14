package fr.abes.cidemis.webstats.correspondence.etat;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DemandeEtatMapper implements RowMapper<DemandeEtatDto> {
    @Override
    public DemandeEtatDto mapRow(ResultSet resultSet, int i) throws SQLException {
        DemandeEtatDto etat = new DemandeEtatDto();
        etat.setIdEtatDemande(resultSet.getInt(1));
        etat.setLibelleEtatDemande(resultSet.getString(2));
        return etat;
    }
}
