package fr.abes.cidemis.dao.cidemis;

import fr.abes.cidemis.model.cidemis.CidemisNotices;

public interface IJdbcTemplateDao {
    CidemisNotices findCidemisNotice(String ppn);

    Integer getIlnRattache(String userKey);
}
