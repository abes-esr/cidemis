package fr.abes.cidemis.webstats.statsetablissement.demandesissnwithcriteriaactivity;

import com.opencsv.CSVWriter;
import fr.abes.cidemis.webstats.Export;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.List;

public class EtabDemandesISSNWithCriteriaActivity extends Export<EtabDemandesISSNWithCriteriaActivityDto> {
    public EtabDemandesISSNWithCriteriaActivity(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected void lineToCSV(CSVWriter writer, EtabDemandesISSNWithCriteriaActivityDto dto) throws SQLException {
        writer.writeNext(new String[]{
                dto.getIln(),
                dto.getRcrDemandeur(),
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
    protected List<EtabDemandesISSNWithCriteriaActivityDto> getQuery() {
        String query = "(" +
                "SELECT " +
                "u.ILN, " +
                "d.RCR_DEMANDEUR, " +
                "d.ID_TYPEDEMANDE, " +
                "'DONE' AS STATUS, " +
                "n.PAYS, " +
                "n.TYPE_DOCUMENT, " +
                "n.TYPE_RESSOURCE_CONTINUE, " +
                "n.DATE_FIN, " +
                "COUNT(*) AS COUNT " +
                "FROM DEMANDES d, AUTORITES.CIDEMIS_NOTICES n, CBS_USERS u " +
                "WHERE " +
                "d.ID_ETATDEMANDE IN (36, 37, 38) " +
                "AND d.PPN = n.ppn " +
                "AND u.USER_NUM = d.USER_NUM " +
                "AND d.DATE_DEMANDE BETWEEN ADD_MONTHS( ? , -1) AND ? " +
                "GROUP BY d.RCR_DEMANDEUR, d.ID_TYPEDEMANDE, n.PAYS, n.TYPE_DOCUMENT, n.TYPE_RESSOURCE_CONTINUE, n.DATE_FIN, u.ILN " +
                ")" +
                " UNION " +
                "(" +
                "SELECT " +
                "u.ILN, " +
                "d.RCR_DEMANDEUR, " +
                "d.ID_TYPEDEMANDE, " +
                "'ACTUAL' AS STATUS, " +
                "n.PAYS, " +
                "n.TYPE_DOCUMENT, " +
                "n.TYPE_RESSOURCE_CONTINUE, " +
                "n.DATE_FIN, " +
                "COUNT(*) AS COUNT " +
                "FROM DEMANDES d, AUTORITES.CIDEMIS_NOTICES n, CBS_USERS u " +
                "WHERE " +
                "d.ID_ETATDEMANDE NOT IN (36, 37, 38) " +
                "AND d.PPN = n.ppn " +
                "AND u.USER_NUM = d.USER_NUM " +
                "AND d.DATE_DEMANDE BETWEEN ADD_MONTHS( ? , -1) AND ? " +
                "GROUP BY d.RCR_DEMANDEUR, d.ID_TYPEDEMANDE, n.PAYS, n.TYPE_DOCUMENT, n.TYPE_RESSOURCE_CONTINUE, n.DATE_FIN, u.ILN " +
                ")";
        return jdbcTemplate.query(query, new Object[]{dateStat, dateStat, dateStat, dateStat}, new EtabDemandesISSNWithCriteriaActivityMapper());
    }
}
