package fr.abes.cidemis.service.impl;

import fr.abes.cidemis.dao.cidemis.CidemisDaoProvider;
import fr.abes.cidemis.model.cidemis.CidemisNotices;
import fr.abes.cidemis.service.IToolsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

@Service
@Slf4j
public class ToolsService implements IToolsService {
    /** Durée de validité du cache en millisecondes (la table NOTICESBIBIO est très volumineuse). */
    private static final long CACHE_TTL_MS = 60_000L;

    @Autowired
    private CidemisDaoProvider dao;

    private volatile String lastDateSynchronizedCache;
    private volatile long lastDateSynchronizedCacheTime;

    @Override
    public String getLastDateSynchronized() {
        long now = System.currentTimeMillis();
        String cached = this.lastDateSynchronizedCache;
        if (cached != null && (now - this.lastDateSynchronizedCacheTime) < CACHE_TTL_MS) {
            return cached;
        }
        String pattern = "dd/MM/yyyy HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Calendar maxDateEtat = this.dao.getToolsDao().findMaxDateEtatBefore(new GregorianCalendar());
        String result = format.format(maxDateEtat.getTime());
        this.lastDateSynchronizedCache = result;
        this.lastDateSynchronizedCacheTime = now;
        return result;
    }

    @Override
    public CidemisNotices findCidemisNotice(String ppn) {
        CidemisNotices cidemisNotice = this.dao.getCidemisTableDao().findCidemisNotice(ppn);

        if (cidemisNotice == null) {
            log.debug("La fonction 'findCidemis_Notice' n'a retournée aucun résultat. ppn:" + ppn);
            return null;
        }
        return cidemisNotice;
    }
}
