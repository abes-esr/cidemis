package fr.abes.cidemis.webstats;

import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.FileWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

@Slf4j
public abstract class Export<T> {
    protected JdbcTemplate jdbcTemplate;

    protected Date dateStat;

    public Export(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected abstract void lineToCSV(CSVWriter writer, T dto) throws SQLException;

    protected abstract List<T> getQuery();


    public void generate(String destination, Date date) throws ExportException {
        log.info(destination + " [" + date + "]: Génération");
        try (CSVWriter writer = new CSVWriter(new FileWriter(destination), ';', CSVWriter.NO_QUOTE_CHARACTER)) {
            this.dateStat = date;
            List<T> query = this.getQuery();
            if (!query.isEmpty()) {
                ListIterator<T> liste = query.listIterator();
                while (liste.hasNext()) {
                    this.lineToCSV(writer, liste.next());
                }
            }
        } catch (Exception e) {
            log.error(destination + " [" + date + "]: Error", e);
            throw new ExportException(e);
        }
        log.info(destination + " [" + date + "]: Done");
    }

}
