package fr.abes.cidemis.webstats.statsgeneral.demandesdonedelay;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DemandesDoneDelayMapper implements RowMapper<DemandesDoneDelayDto>{
    @Override
    public DemandesDoneDelayDto mapRow(ResultSet rs, int i) throws SQLException {
        DemandesDoneDelayDto demande = new DemandesDoneDelayDto();
        demande.setPays(rs.getString("PAYS"));
        demande.setSlice(rs.getString("SLICE"));
        demande.setCount(rs.getInt("COUNT"));
        return demande;
    }
}
