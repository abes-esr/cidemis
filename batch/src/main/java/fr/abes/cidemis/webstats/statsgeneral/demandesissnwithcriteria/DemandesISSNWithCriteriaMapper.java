package fr.abes.cidemis.webstats.statsgeneral.demandesissnwithcriteria;


import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DemandesISSNWithCriteriaMapper implements RowMapper<DemandesISSNWithCriteriaDto> {
    @Override
    public DemandesISSNWithCriteriaDto mapRow(ResultSet rs, int i) throws SQLException {
        DemandesISSNWithCriteriaDto demande = new DemandesISSNWithCriteriaDto();
        demande.setIdTypeDemande(rs.getInt("ID_TYPEDEMANDE"));
        demande.setStatus(rs.getString("STATUS"));
        demande.setPays(rs.getString("PAYS"));
        demande.setTypeDocument((rs.getString("TYPE_DOCUMENT") != null) ? rs.getString("TYPE_DOCUMENT").substring(0, 1) : "");
        demande.setTypeRessourceContinue(rs.getString("TYPE_RESSOURCE_CONTINUE"));
        demande.setDateFin((rs.getString("DATE_FIN") != null && !rs.getString("DATE_FIN").isEmpty()) ? "1" : "0");
        demande.setCount(rs.getInt("COUNT"));
        return demande;
    }
}
