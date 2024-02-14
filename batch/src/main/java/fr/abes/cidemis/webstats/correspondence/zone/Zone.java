package fr.abes.cidemis.webstats.correspondence.zone;

import com.opencsv.CSVWriter;
import fr.abes.cidemis.webstats.Export;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.List;

public class Zone extends Export<ZoneDto> {
    public Zone(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected void lineToCSV(CSVWriter writer, ZoneDto dto) throws SQLException {
        writer.writeNext(new String[]{
                dto.getZone(),
                dto.getLibelle()
        });

    }

    @Override
    protected List<ZoneDto> getQuery() {
        String query = "SELECT * FROM ZONE_CORRECTION";
        return jdbcTemplate.query(query, new ZoneMapper());
    }
}
