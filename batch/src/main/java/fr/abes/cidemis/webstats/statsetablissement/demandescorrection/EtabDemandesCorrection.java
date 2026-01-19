package fr.abes.cidemis.webstats.statsetablissement.demandescorrection;

import com.opencsv.CSVWriter;
import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.webstats.Export;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.List;

public class EtabDemandesCorrection extends Export<EtabDemandesCorrectionDto> {
    public EtabDemandesCorrection(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected void lineToCSV(CSVWriter writer, EtabDemandesCorrectionDto dto) throws SQLException {
        writer.writeNext(new String[]{
                dto.getIln(),
                dto.getRcrDemandeur(),
                dto.getZones(),
                getStatus(dto.getIdEtatDemande()),
                dto.getCount().toString()
        });
    }

    private String getStatus(Integer idEtatDemande) {
        String statusSimple;

        switch (idEtatDemande) {
            case Constant.ETAT_TRAITEMENT_REJETEE_PAR_CR:
                statusSimple = "REJECT";
                break;
            case Constant.ETAT_TRAITEMENT_TERMINE_ACCEPTEE:
                statusSimple = "ACCEPTED";
                break;
            case Constant.ETAT_TRAITEMENT_TERMINE_REFUSEE:
                statusSimple = "REFUSED";
                break;
            default: statusSimple = "ACTUAL";
        }

        return statusSimple;

    }

    @Override
    protected List<EtabDemandesCorrectionDto> getQuery() {
        String query = "SELECT " +
                "u.ILN, " +
                "d.RCR_DEMANDEUR, " +
                "d.ZONES, " +
                "d.ID_ETATDEMANDE, " +
                "COUNT(0) AS COUNT " +
                "FROM " +
                "DEMANDES d, CBS_USERS u " +
                "WHERE d.DATE_DEMANDE < ? " +
                "AND u.USER_NUM = d.USER_NUM " +
                "AND d.ID_TYPEDEMANDE = 23" +
                "GROUP BY d.RCR_DEMANDEUR, d.ZONES, d.ID_ETATDEMANDE, u.ILN"
                ;
        return jdbcTemplate.query(query, new Object[] {dateStat}, new EtabDemandesCorrectionMapper());

    }
}
