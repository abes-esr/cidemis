package fr.abes.cidemis.webstats.statsgeneral.demandesdonedelay;

import com.opencsv.CSVWriter;
import fr.abes.cidemis.webstats.Export;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.List;

public class DemandesDoneDelay extends Export<DemandesDoneDelayDto> {
    public DemandesDoneDelay(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected void lineToCSV(CSVWriter writer, DemandesDoneDelayDto dto) throws SQLException {
        writer.writeNext(new String[]{
                dto.getPays(),
                dto.getSlice(),
                dto.getCount().toString()
        });
    }

    @Override
    protected List<DemandesDoneDelayDto> getQuery() {
        String query = "SELECT PAYS, SLICE, COUNT(*) AS COUNT " +
                "FROM ( " +
                "SELECT  " +
                "n.PAYS, " +
                "EXTRACT(DAY FROM (j.DATE_ENTREE - d.DATE_DEMANDE)) AS JOUR, " +
                "MONTHS_BETWEEN(j.DATE_ENTREE, d.DATE_DEMANDE) AS MOIS, " +
                "CASE  " +
                "WHEN MONTHS_BETWEEN(j.DATE_ENTREE, d.DATE_DEMANDE) < 1 THEN '1' " +
                "WHEN MONTHS_BETWEEN(j.DATE_ENTREE, d.DATE_DEMANDE) < 6 THEN '2' " +
                "WHEN MONTHS_BETWEEN(j.DATE_ENTREE, d.DATE_DEMANDE) < 12 THEN '3' " +
                "ELSE '4' " +
                "END AS SLICE " +
                "FROM DEMANDES d, JOURNAL_DEMANDES j, AUTORITES.CIDEMIS_NOTICES n " +
                "WHERE " +
                "d.ID_ETATDEMANDE IN (36, 37, 38) " +
                "AND j.ID_DEMANDE = d.ID_DEMANDE " +
                "AND j.ID_ETATDEMANDE IN (36, 37, 38) " +
                "AND d.PPN = n.ppn " +
                "AND d.DATE_DEMANDE < ? " +
                ") " +
                "GROUP BY PAYS, SLICE";
        return jdbcTemplate.query(query, new Object[] {dateStat}, new DemandesDoneDelayMapper());
    }
}
