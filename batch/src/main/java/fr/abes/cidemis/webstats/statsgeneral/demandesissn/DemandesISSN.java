package fr.abes.cidemis.webstats.statsgeneral.demandesissn;

import com.opencsv.CSVWriter;
import fr.abes.cidemis.webstats.Export;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.List;

public class DemandesISSN extends Export<DemandesISSNDto> {
    public DemandesISSN(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected void lineToCSV(CSVWriter writer, DemandesISSNDto dto) throws SQLException {
        writer.writeNext(new String[]{
                dto.getIdTypeDemande(),
                dto.getStatus(),
                dto.getCount()
        });
    }

    @Override
    protected List<DemandesISSNDto> getQuery() {
        String query = "(" +
                "SELECT ID_TYPEDEMANDE, 'DONE' AS STATUS, COUNT(*) AS COUNT FROM DEMANDES " +
                "WHERE ID_ETATDEMANDE IN (36, 37, 38) " +
                "AND DATE_DEMANDE < ? " +
                "GROUP BY ID_TYPEDEMANDE" +
                ") " +
                "UNION " +
                "(" +
                "SELECT ID_TYPEDEMANDE, 'ACTUAL' AS STATUS, COUNT(*) AS COUNT FROM DEMANDES " +
                "WHERE ID_ETATDEMANDE NOT IN (36, 37, 38) " +
                "AND DATE_DEMANDE < ? " +
                "GROUP BY ID_TYPEDEMANDE" +
                ")";
        return jdbcTemplate.query(query, new Object[] {dateStat, dateStat}, new DemandesISSNMapper());
    }

}
