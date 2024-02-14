package fr.abes.cidemis.webstats.statsetablissement.demandesissnactivity;

import com.opencsv.CSVWriter;
import fr.abes.cidemis.webstats.Export;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.List;

public class EtabDemandesISSNActivity extends Export<EtabDemandesISSNActivityDto> {
    public EtabDemandesISSNActivity(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected void lineToCSV(CSVWriter writer, EtabDemandesISSNActivityDto dto) throws SQLException {
        writer.writeNext(new String[]{
                dto.getIlnRattache(),
                dto.getRcrDemandeur(),
                dto.getIdTypeDemande().toString(),
                dto.getIdEtatDemande(),
                dto.getCount().toString()
        });
    }

    @Override
    protected List<EtabDemandesISSNActivityDto> getQuery() {
        String query = "SELECT " +
                "u.ILN_RATTACHE, " +
                "d.RCR_DEMANDEUR, " +
                "d.ID_TYPEDEMANDE, " +
                "d.ID_ETATDEMANDE, " +
                "COUNT(*) AS COUNT " +
                "FROM DEMANDES d, CBS_USERS u " +
                "WHERE d.DATE_DEMANDE BETWEEN ADD_MONTHS( ? , -1) AND ? " +
                "AND u.USER_NUM = d.USER_NUM " +
                "AND d.ID_ETATDEMANDE IN (25, 26, 33, 36, 37, 38) " +
                "GROUP BY d.RCR_DEMANDEUR, d.ID_TYPEDEMANDE, d.ID_ETATDEMANDE, u.ILN_RATTACHE";
        return jdbcTemplate.query(query, new Object[] {dateStat, dateStat}, new EtabDemandesISSNActivityMapper());

    }
}
