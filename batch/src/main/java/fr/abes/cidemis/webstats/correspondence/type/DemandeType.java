package fr.abes.cidemis.webstats.correspondence.type;

import com.opencsv.CSVWriter;
import fr.abes.cidemis.webstats.Export;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.List;

public class DemandeType extends Export<DemandeTypeDto> {
    public DemandeType(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected void lineToCSV(CSVWriter writer, DemandeTypeDto dto) throws SQLException {
        writer.writeNext(new String[]{Integer.toString(dto.getIdTypeDemande()), dto.getLibelleTypeDemande()});
    }

    @Override
    protected List<DemandeTypeDto> getQuery() {
        String query = "SELECT ID_TYPEDEMANDE, LIBELLE_TYPEDEMANDE FROM TYPESDEMANDES";
        return jdbcTemplate.query(query, new DemandeTypeMapper());
    }
}
