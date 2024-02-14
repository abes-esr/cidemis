package fr.abes.cidemis.webstats.correspondence.typepublication;

import com.opencsv.CSVWriter;
import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.webstats.Export;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DemandeTypePublication extends Export<DemandeTypePublicationDto> {
    public DemandeTypePublication(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected void lineToCSV(CSVWriter writer, DemandeTypePublicationDto dto) {
        Iterator<String> i = Constant.getListeTypePublication().keySet().iterator();
        while (i.hasNext()) {
            String key = i.next();
            writer.writeNext(new String[]{key, Constant.getListeTypePublication().get(key)});
        }

    }

    @Override
    protected List getQuery() {
        return new ArrayList();
    }
}
