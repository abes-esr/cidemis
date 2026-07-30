package fr.abes.cidemis.dao.cidemis;

import fr.abes.cidemis.model.cidemis.NoticeBiblio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Calendar;

public interface IToolsDao extends JpaRepository<NoticeBiblio, String> {
    /**
     * Récupère la date d'état la plus récente antérieure à la date fournie.
     * MAX() permet à Oracle d'utiliser un INDEX FULL SCAN (MIN/MAX) sur DATE_ETAT
     * au lieu d'un tri top-N sur plusieurs dizaines de millions de lignes.
     */
    @Query("select max(n.dateEtat) from NoticeBiblio n where n.dateEtat < :date")
    Calendar findMaxDateEtatBefore(@Param("date") Calendar date);
}
