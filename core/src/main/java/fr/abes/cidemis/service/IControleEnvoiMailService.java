package fr.abes.cidemis.service;

import fr.abes.cidemis.model.cidemis.CbsUsers;
import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.model.cidemis.Roles;

import java.util.List;

public interface IControleEnvoiMailService {
    List<Roles> whichRoleOfUserToSendEmail(CbsUsers user, Demandes demande);
}
