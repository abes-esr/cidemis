package fr.abes.cidemis.dao.cidemis;

import fr.abes.cidemis.model.cidemis.CidemisNotices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICidemisNoticeDao extends JpaRepository<CidemisNotices, String> {
}
