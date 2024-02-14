package fr.abes.cidemis.webstats.statsgeneral.demandesdonewithcriteria;

import com.opencsv.CSVWriter;
import fr.abes.cidemis.webstats.Export;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.List;

public class DemandesDoneWithCriteria extends Export<DemandesDoneWithCriteriaDto> {
    public DemandesDoneWithCriteria(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected void lineToCSV(CSVWriter writer, DemandesDoneWithCriteriaDto dto) throws SQLException {
        writer.writeNext(new String[]{
                dto.getIdTypeDemande().toString(),
                dto.getIdEtatDemande().toString(),
                dto.getPays(),
                dto.getTypeDocument(),
                dto.getTypeRessourceContinue(),
                dto.getDateFin(),
                dto.getCount().toString()
        });
    }

    @Override
    protected List<DemandesDoneWithCriteriaDto> getQuery() {
        String query = "SELECT " +
                "d.ID_TYPEDEMANDE, " +
                "d.ID_ETATDEMANDE, " +
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
                "GROUP BY d.ID_TYPEDEMANDE, d.ID_ETATDEMANDE, n.PAYS, n.TYPE_DOCUMENT, n.TYPE_RESSOURCE_CONTINUE, n.DATE_FIN";
        return jdbcTemplate.query(query, new Object[] {dateStat}, new DemandesDoneWithCriteriaMapper());


    }
}
