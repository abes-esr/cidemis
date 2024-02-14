package fr.abes.cidemis.webstats.correspondence.typesupport;

import com.opencsv.CSVWriter;
import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.webstats.Export;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DemandeTypeSupport extends Export<DemandeTypeSupportDto> {
    public DemandeTypeSupport(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected void lineToCSV(CSVWriter writer, DemandeTypeSupportDto dto) throws SQLException {
        Iterator<String> i = Constant.getListeTypeDocument().keySet().iterator();
        while (i.hasNext()) {
            String key = i.next();
            writer.writeNext(new String[]{key, Constant.getListeTypeDocument().get(key)});
        }


    }

    @Override
    protected List<DemandeTypeSupportDto> getQuery() {
        return new ArrayList<>();
    }
}
