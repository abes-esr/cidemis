package fr.abes.cidemis.service.impl;

import fr.abes.cidemis.dao.cidemis.CidemisDaoProvider;
import fr.abes.cidemis.model.cidemis.CidemisNotices;
import fr.abes.cidemis.service.IToolsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

@Service
@Slf4j
public class ToolsService implements IToolsService {
    @Autowired
    private CidemisDaoProvider dao;

    @Override
    public String getLastDateSynchronized() {
        String pattern = "dd/MM/yyyy HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(this.dao.getToolsDao().findFirstByDateEtatBeforeOrderByDateEtatDesc(new GregorianCalendar()).getDateEtat().getTime());
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
