package fr.abes.cidemis.dao.cidemis.impl;

import fr.abes.cidemis.components.CidemisNoticesMapper;
import fr.abes.cidemis.dao.cidemis.IJdbcTemplateDao;
import fr.abes.cidemis.model.cidemis.CidemisNotices;
import fr.abes.cidemis.model.dto.CidemisNoticesDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class JdbcTemplateDaoImpl implements IJdbcTemplateDao {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplateDaoImpl(@Qualifier("cidemisDataSource") DataSource cidemisDataSource, @Qualifier("cidemisJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.dataSource = cidemisDataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public CidemisNotices findCidemisNotice(String ppn) {
        String query = "SELECT * FROM TABLE(AUTORITES.CIDEMIS_NOTICE_BY_PPN(?))";
        CidemisNotices notice = new CidemisNotices();
        List<CidemisNoticesDto> liste =  jdbcTemplate.query(query, new Object[] {ppn}, new CidemisNoticesMapper());
        if (!liste.isEmpty()) {
            CidemisNoticesDto noticeBdd = liste.get(0);
            notice.setPpn(noticeBdd.getPpn());
            notice.setIssn(noticeBdd.getIssn());
            notice.setTitreA(noticeBdd.getTitreA());
            notice.setTitre(noticeBdd.getTitre());
            notice.setTitreE(noticeBdd.getTitreE());
            notice.setTitreC(noticeBdd.getTitreC());
            notice.setTitreH(noticeBdd.getTitreH());
            notice.setTitreI(noticeBdd.getTitreI());
            notice.setTitreCle(noticeBdd.getTitreCle());
            notice.setTitreCleAbrege(noticeBdd.getTitreCleAbrege());
            notice.setPays(noticeBdd.getPays());
            notice.setDateDebut(noticeBdd.getDateDebut());
            notice.setDateFin(noticeBdd.getDateFin());
            notice.setTypeDocument(noticeBdd.getTypeDocument());
            notice.setStatutNotice(noticeBdd.getStatutNotice());
            notice.setCentreISSN(noticeBdd.getCentreISSN());
            notice.setIssn035(noticeBdd.getIssn035());
            notice.setFrbnf(noticeBdd.getFrbnf());
            notice.setTypeDeRessourceContinue(noticeBdd.getTypeDeRessourceContinue());
            notice.setSourceDeCatalogageISSN(noticeBdd.getSourceDeCatalogageISSN());
            notice.setNoteIdentifiants(noticeBdd.getNoteIdentifiants());
            notice.setNoteGeneraleCatalogueur(noticeBdd.getNoteGeneraleCatalogueur());
            return notice;
        }
        return null;
    }

}
