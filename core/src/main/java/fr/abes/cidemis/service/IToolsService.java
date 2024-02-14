package fr.abes.cidemis.service;

import fr.abes.cidemis.model.cidemis.CidemisNotices;

public interface IToolsService {
    String getLastDateSynchronized();

    CidemisNotices findCidemisNotice(String ppn);
}
