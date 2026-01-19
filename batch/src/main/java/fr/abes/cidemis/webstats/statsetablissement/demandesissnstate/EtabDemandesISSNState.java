package fr.abes.cidemis.webstats.statsetablissement.demandesissnstate;

import com.opencsv.CSVWriter;
import fr.abes.cidemis.webstats.Export;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.List;

public class EtabDemandesISSNState extends Export<EtabDemandesISSNStateDto> {
    public EtabDemandesISSNState(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected void lineToCSV(CSVWriter writer, EtabDemandesISSNStateDto dto) throws SQLException {
        writer.writeNext(new String[]{
           dto.getIln(),
           dto.getRcrDemandeur(),
           dto.getIdTypeDemande().toString(),
           dto.getStatus(),
           dto.getCount().toString()
        });
    }

    @Override
    protected List<EtabDemandesISSNStateDto> getQuery() {
        String query = "(" +
                "SELECT " +
                "u.ILN, " +
                "d.RCR_DEMANDEUR, " +
                "d.ID_TYPEDEMANDE, " +
                "'DONE' AS STATUS, " +
                "COUNT(*) AS COUNT " +
                "FROM DEMANDES d, CBS_USERS u " +
                "WHERE " +
                "d.ID_ETATDEMANDE IN (36, 37, 38) " +
                "AND u.USER_NUM = d.USER_NUM " +
                "AND d.DATE_DEMANDE < ? " +
                "GROUP BY " +
                "d.RCR_DEMANDEUR, d.ID_TYPEDEMANDE, u.ILN " +
                ") " +
                "UNION " +
                "( " +
                "SELECT " +
                "u.ILN, " +
                "d.RCR_DEMANDEUR, " +
                "d.ID_TYPEDEMANDE, " +
                "'ACTUAL' AS STATUS, " +
                "COUNT(*) AS COUNT " +
                "FROM DEMANDES d, CBS_USERS u " +
                "WHERE " +
                "d.ID_ETATDEMANDE NOT IN (36, 37, 38) " +
                "AND u.USER_NUM = d.USER_NUM " +
                "AND d.DATE_DEMANDE < ? " +
                "GROUP BY " +
                "d.RCR_DEMANDEUR, d.ID_TYPEDEMANDE, u.ILN " +
                ")";
        return jdbcTemplate.query(query, new Object[] {dateStat, dateStat}, new EtabDemandesISSNStateMapper());
    }
}
