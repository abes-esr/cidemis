package fr.abes.cidemis.webstats.correspondence.zone;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ZoneMapper implements RowMapper<ZoneDto> {
    @Override
    public ZoneDto mapRow(ResultSet resultSet, int i) throws SQLException {
        ZoneDto zone = new ZoneDto();
        zone.setZone(resultSet.getString("ZONE"));
        zone.setLibelle(resultSet.getString("LIBELLE"));
        return zone;
    }
}
