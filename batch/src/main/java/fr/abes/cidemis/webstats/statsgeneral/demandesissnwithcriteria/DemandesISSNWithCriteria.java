package fr.abes.cidemis.webstats.statsgeneral.demandesissnwithcriteria;

import com.opencsv.CSVWriter;
import fr.abes.cidemis.webstats.Export;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.List;

public class DemandesISSNWithCriteria extends Export<DemandesISSNWithCriteriaDto> {
    public DemandesISSNWithCriteria(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected void lineToCSV(CSVWriter writer, DemandesISSNWithCriteriaDto dto) throws SQLException {
            writer.writeNext(new String[]{
                    dto.getIdTypeDemande().toString(),
                    dto.getStatus(),
                    dto.getPays(),
                    dto.getTypeDocument(),
                    dto.getTypeRessourceContinue(),
                    dto.getDateFin(),
                    dto.getCount().toString()
            });
    }

    @Override
    protected List<DemandesISSNWithCriteriaDto> getQuery() {
        String query = "(" +
                "SELECT " +
                "d.ID_TYPEDEMANDE, " +
                "'DONE' AS STATUS, " +
                "n.PAYS, " +
                "n.TYPE_DOCUMENT, " +
                "n.TYPE_RESSOURCE_CONTINUE, " +
                "n.DATE_FIN, " +
                "COUNT(*) AS COUNT " +
                "FROM DEMANDES d, AUTORITES.CIDEMIS_NOTICES n " +
                "WHERE " +
                "d.ID_ETATDEMANDE IN (36, 37, 38) " +
                "AND d.PPN = n.ppn " +
                "AND d.DATE_DEMANDE < ? " +
                "GROUP BY d.ID_TYPEDEMANDE, n.PAYS, n.TYPE_DOCUMENT, n.TYPE_RESSOURCE_CONTINUE, n.DATE_FIN" +
                ") " +
                "UNION " +
                "(" +
                "SELECT " +
                "d.ID_TYPEDEMANDE, " +
                "'ACTUAL' AS STATUS, " +
                "n.PAYS, " +
                "n.TYPE_DOCUMENT, " +
                "n.TYPE_RESSOURCE_CONTINUE, " +
                "n.DATE_FIN, " +
                "COUNT(*) AS COUNT " +
                "FROM DEMANDES d, AUTORITES.CIDEMIS_NOTICES n " +
                "WHERE " +
                "d.ID_ETATDEMANDE NOT IN (36, 37, 38) " +
                "AND d.PPN = n.ppn " +
                "AND d.DATE_DEMANDE < ? " +
                "GROUP BY d.ID_TYPEDEMANDE, n.PAYS, n.TYPE_DOCUMENT, n.TYPE_RESSOURCE_CONTINUE, n.DATE_FIN" +
                ")";
        return jdbcTemplate.query(query, new Object[] {dateStat, dateStat}, new DemandesISSNWithCriteriaMapper());
    }
}
