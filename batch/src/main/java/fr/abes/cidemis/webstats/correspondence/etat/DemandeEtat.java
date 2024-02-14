package fr.abes.cidemis.webstats.correspondence.etat;

import com.opencsv.CSVWriter;
import fr.abes.cidemis.webstats.Export;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.List;

public class DemandeEtat extends Export<DemandeEtatDto> {
    public DemandeEtat(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected void lineToCSV(CSVWriter writer, DemandeEtatDto dto) throws SQLException {
        writer.writeNext(new String[]{Integer.toString(dto.getIdEtatDemande()), dto.getLibelleEtatDemande()});
    }

    @Override
    protected List<DemandeEtatDto> getQuery() {
        String query = "SELECT * FROM ETATSDEMANDES";
        return jdbcTemplate.query(query, new Object[] {}, new DemandeEtatMapper());
    }
}
