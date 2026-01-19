package fr.abes.cidemis.webstats.statsetablissement.demandesdonewithcriteria;

import com.opencsv.CSVWriter;
import fr.abes.cidemis.webstats.Export;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.List;

public class EtabDemandesDoneWithCriteria extends Export<EtabDemandesDoneWithCriteriaDto> {
    public EtabDemandesDoneWithCriteria(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected void lineToCSV(CSVWriter writer, EtabDemandesDoneWithCriteriaDto dto) throws SQLException {
        writer.writeNext(new String[]{
                dto.getIln(),
                dto.getRcrDemandeur(),
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
    protected List<EtabDemandesDoneWithCriteriaDto> getQuery() {
        String query = "SELECT " + "u.ILN, " + "d.RCR_DEMANDEUR, " + "d.ID_TYPEDEMANDE, " + "d.ID_ETATDEMANDE, "
                + "n.PAYS, " + "n.TYPE_DOCUMENT, " + "n.TYPE_RESSOURCE_CONTINUE, " + "n.DATE_FIN, "
                + "COUNT(*) AS COUNT " + "FROM DEMANDES d, AUTORITES.CIDEMIS_NOTICES n, CBS_USERS u " + "WHERE "
                + "d.ID_ETATDEMANDE IN (36, 37, 38) " + "AND u.USER_NUM = d.USER_NUM " + "AND d.PPN = n.ppn "
                + "AND d.DATE_DEMANDE < ? "
                + "GROUP BY d.RCR_DEMANDEUR, d.ID_TYPEDEMANDE, d.ID_ETATDEMANDE, n.PAYS, n.TYPE_DOCUMENT, n.TYPE_RESSOURCE_CONTINUE, n.DATE_FIN, u.ILN";
        return jdbcTemplate.query(query, new Object[] {dateStat}, new EtabDemandesDoneWithCriteriaMapper());
    }
}
