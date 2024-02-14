package fr.abes.cidemis.webstats.correspondence.type;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DemandeTypeMapper implements RowMapper<DemandeTypeDto> {
    @Override
    public DemandeTypeDto mapRow(ResultSet rs, int i) throws SQLException {
        DemandeTypeDto type = new DemandeTypeDto();
        type.setIdTypeDemande(rs.getInt("ID_TYPEDEMANDE"));
        type.setLibelleTypeDemande(rs.getString("LIBELLE_TYPEDEMANDE"));
        return type;
    }
}
