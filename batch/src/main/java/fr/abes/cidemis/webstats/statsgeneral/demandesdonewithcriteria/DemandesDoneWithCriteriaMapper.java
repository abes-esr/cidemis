package fr.abes.cidemis.webstats.statsgeneral.demandesdonewithcriteria;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DemandesDoneWithCriteriaMapper implements RowMapper<DemandesDoneWithCriteriaDto> {
    @Override
    public DemandesDoneWithCriteriaDto mapRow(ResultSet rs, int i) throws SQLException {
        DemandesDoneWithCriteriaDto demande = new DemandesDoneWithCriteriaDto();
        demande.setIdTypeDemande(rs.getInt("ID_TYPEDEMANDE"));
        demande.setIdEtatDemande(rs.getInt("ID_ETATDEMANDE"));
        demande.setPays(rs.getString("PAYS"));
        demande.setTypeDocument((rs.getString("TYPE_DOCUMENT") != null) ? rs.getString("TYPE_DOCUMENT").substring(0, 1) : "");
        demande.setTypeRessourceContinue(rs.getString("TYPE_RESSOURCE_CONTINUE"));
        demande.setDateFin((rs.getString("DATE_FIN") != null && !rs.getString("DATE_FIN").isEmpty()) ? "1" : "0");
        demande.setCount(rs.getInt("COUNT"));
        return demande;
    }
}
