package fr.abes.cidemis.dao.cidemis;

import fr.abes.cidemis.model.cidemis.NoticeBiblio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Calendar;

public interface IToolsDao extends JpaRepository<NoticeBiblio, String> {
    NoticeBiblio findFirstByDateEtatBeforeOrderByDateEtatDesc(Calendar date);
}
