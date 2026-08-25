package fr.abes.cidemis.dao.cidemis;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.abes.cidemis.model.cidemis.CidemisNotices;

public interface ICidemisNoticeDao extends JpaRepository<CidemisNotices, String> {
}
