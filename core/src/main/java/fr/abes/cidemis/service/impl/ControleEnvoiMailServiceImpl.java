package fr.abes.cidemis.service.impl;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.model.cidemis.CbsUsers;
import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.model.cidemis.Roles;
import fr.abes.cidemis.service.CidemisManageService;
import fr.abes.cidemis.service.IControleEnvoiMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ControleEnvoiMailServiceImpl implements IControleEnvoiMailService {
    @Autowired
    CidemisManageService service;

    @Override
    public List<Roles> whichRoleOfUserToSendEmail(CbsUsers user, Demandes demande) {
        List<Roles> roles = new ArrayList<>();
        switch (user.getRoles().getIdRole()){
            case Constant.ROLE_CATALOGUEUR:
            if(this.demandeIsItOneOfTheFollowingStatus(demande.getEtatsDemandes().getIdEtatDemande(),
                    Constant.ETAT_EN_ATTENTE_PRECISION_RESPONSABLE_CR,
                    Constant.ETAT_PRECISION_PAR_CATALOGUEUR
                    )) {
                roles.add(service.getUsers().findRoles(Constant.ROLE_CORCAT));
            }
            return roles;

            case Constant.ROLE_CORCAT:
            if(this.demandeIsItOneOfTheFollowingStatus(demande.getEtatsDemandes().getIdEtatDemande(),
                    Constant.ETAT_PRECISION_PAR_RESPONSABLE_CR,
                    Constant.ETAT_TRAITEMENT_REJETEE_PAR_CR,
                    Constant.ETAT_EN_ATTENTE_PRECISION_CATALOGUEUR
            )){
                roles.add(service.getUsers().findRoles(Constant.ROLE_CATALOGUEUR));
            }
            return roles;

            case Constant.ROLE_ISSN:
            case Constant.ROLE_CIEPS:
                if(this.demandeIsItOneOfTheFollowingStatus(demande.getEtatsDemandes().getIdEtatDemande(),
                        Constant.ETAT_TRAITEMENT_TERMINE_REFUSEE
                )){
                    roles.add(service.getUsers().findRoles(Constant.ROLE_CORCAT));
                    roles.add(service.getUsers().findRoles(Constant.ROLE_CATALOGUEUR));

                }
                if(this.demandeIsItOneOfTheFollowingStatus(demande.getEtatsDemandes().getIdEtatDemande(),
                        Constant.ETAT_EN_ATTENTE_PRECISION_RESPONSABLE_CR
                )){
                    roles.add(service.getUsers().findRoles(Constant.ROLE_CORCAT));
                }
            return roles;
            default: return roles;
        }
    }

    private Boolean demandeIsItOneOfTheFollowingStatus(Integer demandeStatus, Integer... expectedStatus){
        for(final int numberStatus : expectedStatus){
            if(demandeStatus.equals(numberStatus)){
                return true;
            }
        }
        return false;
    }
}
