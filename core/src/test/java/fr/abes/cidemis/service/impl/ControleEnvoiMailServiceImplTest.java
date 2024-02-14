package fr.abes.cidemis.service.impl;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.model.cidemis.CbsUsers;
import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.model.cidemis.EtatsDemandes;
import fr.abes.cidemis.model.cidemis.Roles;
import fr.abes.cidemis.service.CidemisManageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ControleEnvoiMailServiceImplTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CidemisManageService service;
    @InjectMocks
    private ControleEnvoiMailServiceImpl controleEnvoiMailService;

    CbsUsers cbsUsers;
    Roles roles;
    Demandes demande;
    EtatsDemandes etatsDemandes;

    @BeforeEach
    void init(){
        MockitoAnnotations.initMocks(this);

        Roles roleCatalogueur = new Roles();
        roleCatalogueur.setIdRole(Constant.ROLE_CATALOGUEUR);
        when(service.getUsers().findRoles(Constant.ROLE_CATALOGUEUR)).thenReturn(roleCatalogueur);

        Roles roleRespCr = new Roles();
        roleRespCr.setIdRole(Constant.ROLE_RESPONSABLE_CR);
        when(service.getUsers().findRoles(Constant.ROLE_RESPONSABLE_CR)).thenReturn(roleRespCr);

        Roles roleIssn = new Roles();
        roleIssn.setIdRole(Constant.ROLE_ISSN);
        when(service.getUsers().findRoles(Constant.ROLE_ISSN)).thenReturn(roleIssn);

        Roles roleCieps = new Roles();
        roleIssn.setIdRole(Constant.ROLE_CIEPS);
        when(service.getUsers().findRoles(Constant.ROLE_CIEPS)).thenReturn(roleCieps);

        this.roles = new Roles();
        this.cbsUsers = new CbsUsers(1);
        this.demande = new Demandes(1);
        this.etatsDemandes = new EtatsDemandes();
    }

    @Test
    void whichUserToSendEmailTestCatalogueur() {
        this.roles.setIdRole(Constant.ROLE_CATALOGUEUR);
        this.cbsUsers.setRoles(roles);
        this.etatsDemandes.setIdEtatDemande(Constant.ETAT_EN_ATTENTE_PRECISION_RESPONSABLE_CR);
        demande.setEtatsDemandes(etatsDemandes);
        assertThat(controleEnvoiMailService.whichRoleOfUserToSendEmail(this.cbsUsers, this.demande).get(0).getIdRole()).isEqualTo(Constant.ROLE_RESPONSABLE_CR);

        this.etatsDemandes.setIdEtatDemande(Constant.ETAT_PRECISION_PAR_CATALOGUEUR);
        demande.setEtatsDemandes(etatsDemandes);
        assertThat(controleEnvoiMailService.whichRoleOfUserToSendEmail(this.cbsUsers, this.demande).get(0).getIdRole()).isEqualTo(Constant.ROLE_RESPONSABLE_CR);

    }

    @Test
    void whichUserToSendEmailTestResponsableCR() {
        this.roles.setIdRole(Constant.ROLE_RESPONSABLE_CR);
        this.cbsUsers.setRoles(roles);
        this.etatsDemandes.setIdEtatDemande(Constant.ETAT_PRECISION_PAR_RESPONSABLE_CR);
        demande.setEtatsDemandes(etatsDemandes);

        assertThat(controleEnvoiMailService.whichRoleOfUserToSendEmail(this.cbsUsers, this.demande).get(0).getIdRole()).isEqualTo(Constant.ROLE_CATALOGUEUR);

        this.etatsDemandes.setIdEtatDemande(Constant.ETAT_EN_ATTENTE_PRECISION_CATALOGUEUR);
        demande.setEtatsDemandes(etatsDemandes);
        assertThat(controleEnvoiMailService.whichRoleOfUserToSendEmail(this.cbsUsers, this.demande).get(0).getIdRole()).isEqualTo(Constant.ROLE_CATALOGUEUR);

        this.etatsDemandes.setIdEtatDemande(Constant.ETAT_TRAITEMENT_REJETEE_PAR_CR);
        demande.setEtatsDemandes(etatsDemandes);
        assertThat(controleEnvoiMailService.whichRoleOfUserToSendEmail(this.cbsUsers, this.demande).get(0).getIdRole()).isEqualTo(Constant.ROLE_CATALOGUEUR);

    }

    @Test
    void whichUserToSendEmailTestCiepsSendToCrAndCatalogueur() {
        this.roles.setIdRole(Constant.ROLE_CIEPS);
        this.cbsUsers.setRoles(roles);
        this.etatsDemandes.setIdEtatDemande(Constant.ETAT_TRAITEMENT_TERMINE_REFUSEE);
        demande.setEtatsDemandes(etatsDemandes);

        assertThat(controleEnvoiMailService.whichRoleOfUserToSendEmail(this.cbsUsers, this.demande).get(0).getIdRole()).isEqualTo(Constant.ROLE_RESPONSABLE_CR);
        assertThat(controleEnvoiMailService.whichRoleOfUserToSendEmail(this.cbsUsers, this.demande).get(1).getIdRole()).isEqualTo(Constant.ROLE_CATALOGUEUR);
    }

    @Test
    void whichUserToSendEmailTestISSNSendToCr() {
        this.roles.setIdRole(Constant.ROLE_ISSN);
        this.cbsUsers.setRoles(roles);
        this.etatsDemandes.setIdEtatDemande(Constant.ETAT_EN_ATTENTE_PRECISION_RESPONSABLE_CR);
        demande.setEtatsDemandes(etatsDemandes);

        assertThat(controleEnvoiMailService.whichRoleOfUserToSendEmail(this.cbsUsers, this.demande).get(0).getIdRole()).isEqualTo(Constant.ROLE_RESPONSABLE_CR);
    }
}