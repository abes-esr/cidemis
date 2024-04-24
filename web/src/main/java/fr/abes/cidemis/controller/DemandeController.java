package fr.abes.cidemis.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.cbs.exception.CBSException;
import fr.abes.cbs.exception.ZoneException;
import fr.abes.cbs.process.ProcessCBS;
import fr.abes.cidemis.bean.DemandesListe;
import fr.abes.cidemis.components.Fichier;
import fr.abes.cidemis.components.NoticeHelper;
import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.model.cidemis.*;
import fr.abes.cidemis.model.dto.DemandeDto;
import fr.abes.cidemis.service.CidemisManageService;
import fr.abes.cidemis.web.MyDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Controller
public class DemandeController extends AbstractServlet {
    @Autowired
    private CidemisManageService service;

    @Value("${cbs.url}")
    private String cbsUrl;
    @Value("${cbs.port}")
    private String cbsPort;
    @Value("${cbs.password}")
    private String cbsPassword;

    @Value("${path.justificatifs}")
    private String path;

    @Description("Afficher une demande")
    @GetMapping(value = "/afficher-demande")
    public String afficherDemande(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String forward = this.catchProcessRequest(request, response);
        if (!forward.isEmpty()) {
            return forward;
        }
        Connexion connexion = (Connexion) session.getAttribute("connexion");
        param.setRequest(request);
        Integer numdemande = Integer.parseInt(param.getParameter("id"));
        Demandes demande = getService().getDemande().findDemande(numdemande);

        // Si on a trouvé une demande, on la met à dispo de la jsp
        if (demande != null) {
            demande.setPiecesJustificativeslist(service.getPiecesJustificatives().findPiecesJustificativesByDemandes(demande));
            demande.setCommentairesList(service.getCommentaires().findCommentairesByDemandes(demande));

            request.setAttribute("demande", demande);

            request.setAttribute("commentaires", demande.getCommentairesList());
            request.setAttribute("piecesJustificatives", demande.getPiecesJustificativeslist());

            if (getService().getDemande().canUserModifyDemande(connexion.getUser(), demande)) {
                // On récupère la liste des demandes portant sur le ppn
                if (demande.getNotice() != null) {
                    List<Demandes> demandes = getService().getDemande().findDemandesByPPN(demande.getNotice().getPpn());
                    DemandesListe demandelisteWithSamePpn = new DemandesListe();
                    demandelisteWithSamePpn.setDemandeslist(demandes, demande);
                    request.setAttribute("demandes_with_same_ppn", demandelisteWithSamePpn);
                }
                Commentaires lastCommentaire = getService().getCommentaires().findLastCommentairesByDemande(demande);
                if (lastCommentaire != null && lastCommentaire.canUpdate(connexion.getUser())) {
                    request.setAttribute("lastCommentaire", lastCommentaire);
                }
                return MyDispatcher.CREATIONDEMANDEJSP;
            } else {
                return MyDispatcher.AFFICHER_DEMANDEJSP;
            }
        } else {
            return "redirect:" + MyDispatcher.LISTE_DEMANDES;
        }
    }

    @Description("Retourne une JSP pour vérifier que l'on ne souhaite pas créer une demande qui existe déjà (nécessaire pour la servlet créer demande)")
    @PostMapping(value = "/choix-demande")
    public String choixDemande() {
        return MyDispatcher.CHOIXDEMANDEJSP;
    }

    @Description("Formulaire de création d'une demande (avec vérification qu'aucune demande sur ce PPN n'existe déjà)")
    @PostMapping(value = "/creation-demande")
    public String creationDemande(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String forward = this.catchProcessRequest(request, response);
        if (!forward.isEmpty()) {
            return forward;
        }
        Connexion connexion = (Connexion) session.getAttribute("connexion");
        param.setRequest(request);
        Integer typeDemande = Integer.parseInt(param.getParameter("type_demande"));
        String ppn = param.getParameter("ppn");
        String titre = String.valueOf(request.getAttribute("titre"));
        String action = param.getParameter("action");
        boolean ajax = !param.getParameter("ajax").isEmpty();

        String nextPage;

        // On récupère la liste des demandes portant sur le ppn
        List<Demandes> demandes = getService().getDemande().findDemandesByPPN(ppn);
        DemandesListe demandeliste = new DemandesListe();
        demandeliste.setDemandeslist(demandes, null);
        request.setAttribute("demandes_with_same_ppn", demandeliste);

        // On récupère toutes les demandes qui ne sont pas en status "refusé"
        List<Demandes> demandesPPN = new ArrayList<>();
        for (Demandes d : demandes) {
            if (d.getTypesDemandes().getIdTypeDemande().equals(typeDemande) &&
                    !d.getEtatsDemandes().getIdEtatDemande().equals(Constant.ETAT_TRAITEMENT_TERMINE_REFUSEE) &&
                    !d.getEtatsDemandes().getIdEtatDemande().equals(Constant.ETAT_TRAITEMENT_REJETEE_PAR_CR)) {
                demandesPPN.add(d);
            }
        }

        // Si on a trouvé une ou plusieurs demandes, on les ajoutes au bean DemandesListe
        if (!demandesPPN.isEmpty() && "verifier".equals(action)) {
            demandeliste = new DemandesListe();
            demandeliste.setDemandesList(demandesPPN);
            request.setAttribute("listedemandes", demandeliste);
            nextPage = MyDispatcher.CHOIX_DEMANDE;
        }
        // Si aucune demande déjà présente sur le ppn on passe sur le formulaire de création de demande
        else {
            if (typeDemande == Constant.TYPE_DEMANDE_CREATION) {
                Demandes demande = new Demandes();

                demande.setDateDemande(new Date());
                demande.setTitre(titre);
                demande.setRcrDemandeur(connexion.getUser().getLibrary());
                demande.setCommentairesList(new ArrayList<>());
                demande.setJournalDemandesList(new ArrayList<>());
                demande.setPiecesJustificativeslist(new ArrayList<>());
                demande.setCbsUsers(connexion.getUser());
                demande.setTypesDemandes(getService().getReference().findTypesdemandes(Constant.TYPE_DEMANDE_CREATION));
                demande.setEtatsDemandes(getService().getReference().findEtatsdemandes(Constant.ETAT_EN_ATTENTE_VALIDATION_CATALOGUEUR));
                service.getDemande().setCr(demande);
                request.setAttribute("demande", demande);
                request.setAttribute("type_demande", getService().getReference().findTypesdemandes(typeDemande));

                if (ajax)
                    nextPage = MyDispatcher.RESEND_FORMJSP;
                else
                    nextPage = MyDispatcher.CREATIONDEMANDEJSP;
            } else {
                CidemisNotices notice = getService().getTools().findCidemisNotice(ppn);
                if (notice == null) {
                    nextPage = MyDispatcher.AUCUNENOTICEJSP;
                } else {
                    Demandes demande = new Demandes();
                    if (connexion.getUser().getRoles().getIdRole().equals(Constant.ROLE_CATALOGUEUR))
                        demande.setEtatsDemandes(getService().getReference().findEtatsdemandes(Constant.ETAT_EN_ATTENTE_VALIDATION_CATALOGUEUR));
                    else if (connexion.getUser().getRoles().getIdRole().equals(Constant.ROLE_RESPONSABLE_CR))
                        demande.setEtatsDemandes(getService().getReference().findEtatsdemandes(Constant.ETAT_EN_ATTENTE_VALIDATION_RESPONSABLE_CR));

                    demande.setDateDemande(new Date());
                    demande.setNotice(notice);
                    demande.setTypesDemandes(getService().getReference().findTypesdemandes(typeDemande));
                    demande.setCbsUsers(connexion.getUser());
                    demande.setRcrDemandeur(connexion.getUser().getLibrary());
                    demande.setPiecesJustificativeslist(new ArrayList<>());
                    demande.setTitre(notice.getTitre());
                    service.getDemande().setCr(demande);

                    request.setAttribute("demande", demande);
                    // Si on est dans le contexte d'une requête ajax (utilisé par le système de popup), on informe le navigateur qu'il doit renvoyer le formulaire sans requête ajax
                    if (ajax)
                        nextPage = MyDispatcher.RESEND_FORMJSP;
                    else
                        nextPage = MyDispatcher.CREATIONDEMANDEJSP;
                }
            }
        }
        request.setAttribute("ppn", ppn);
        request.setAttribute("type_demande", getService().getReference().findTypesdemandes(typeDemande));
        return nextPage;
    }

    @Description("Une fois le formulaire crée, cette Servlet va vérifier que tout est ok")
    @RequestMapping(value = "/creer-demande")
    public String creerDemande(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String forward = this.catchProcessRequest(request, response);
        boolean error = false;
        if (!forward.isEmpty()) {
            return forward;
        }
        this.param.setRequest(request);
        Connexion connexion = (Connexion) session.getAttribute("connexion");
        DemandeDto demandeDto = initDemande(connexion.getUser());
        Demandes demande;
        try {
            demande = getService().getDemande().creerDemande(demandeDto, connexion.getUser(), connexion.getRegistryuser(), cbsUrl, cbsPort, cbsPassword, path);
            getService().getDemande().envoiMail(demande, connexion.getUser());
        } catch (JsonProcessingException r) {
            request.setAttribute("error_code", "ERROR_WSMAIL");
            request.setAttribute("texte_erreur", r.getMessage());
            request.setAttribute("link_redirection", "/liste-demandes");
            log.error(r.getMessage());
            error = true;
        } catch (CBSException | ZoneException | IOException ex) {
            request.setAttribute("error_code", "ERROR_CREATION");
            request.setAttribute("texte_erreur", ex.getMessage());
            error = true;
            log.error(ex.getMessage());
        }  catch(RestClientException c) {
            request.setAttribute("error_code", "ERROR_WSMAIL_INDISPONIBLE");
            request.setAttribute("code_erreur_premier_chiffre", c.getMessage().substring(0,1));
            request.setAttribute("code_erreur_http", c.getMessage().substring(0,3));
            request.setAttribute("link_redirection", "/liste-demandes");
            error = true;
        }

        //Affichage d'une popup d'erreur à l'utilisateur en cas d'erreur soulevée
        if (error) {
            return MyDispatcher.ERRORNOTICEJSP;
        }

        return "redirect:" + MyDispatcher.LISTE_DEMANDES;
    }

    private DemandeDto initDemande(CbsUsers user) {
        DemandeDto demandeDto = new DemandeDto();
        demandeDto.setPpn(this.param.getParameter("ppn"));
        demandeDto.setTypesDemandes(Integer.parseInt(this.param.getParameter("type_demande")));
        demandeDto.setCommentaireTxt(this.param.getParameter("commentaire"));
        demandeDto.setTaggueTxt(this.param.getParameter("taggue"));
        demandeDto.setCommentaireVisibleIssn(true);
        demandeDto.setLastIdCommentaire(Integer.parseInt((!this.param.getParameter("last_id_commentaire").isEmpty())
                ? this.param.getParameter("last_id_commentaire") : "0"));
        demandeDto.setIdDemande(Integer.parseInt(this.param.getParameter("id_demande")));
        demandeDto.setAction(this.param.getParameter("actiondemande"));
        demandeDto.setZones(this.param.getParameter("zones"));
        demandeDto.setIdProfil(this.param.getParameter("id_profil").isEmpty() ? null : Integer.parseInt(this.param.getParameter("id_profil")));
        demandeDto.setIssn(this.param.getParameter("num_ISSN"));
        demandeDto.setTitre(this.param.getParameter("titre"));
        demandeDto.setNumPpn(this.param.getParameter("num_ppn"));
        demandeDto.setRcrDemandeur(this.param.getParameter("num_RCR"));
        demandeDto.setCodePays(this.param.getParameter("code_pays"));
        if (!user.isISSNOrCIEPS() && this.param.getParameter("visible_issn") != null)
            demandeDto.setCommentaireVisibleIssn(this.param.getParameter("visible_issn").contains("on"));
        demandeDto.setFileCount(Integer.parseInt(this.param.getParameter("filecount")));
        demandeDto.setFilesToDelete(this.param.getParameters("filestodelete"));
        List<Fichier> listeFichiers = new ArrayList<>();
        for (int i = 0; i <= demandeDto.getFileCount(); i++) {
            String nomfichier = "file" + i;
            if (!this.param.getFile(nomfichier).isEmpty()) {
                listeFichiers.addAll(this.param.getFile(nomfichier));
            }
        }
        demandeDto.setFichiers(listeFichiers);
        return demandeDto;
    }

    @PostMapping("/SuppressionDemande")
    public String supprimerDemande(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        String forward = this.catchProcessRequest(request, response);
        if (!forward.isEmpty()) {
            return forward;
        }
        param.setRequest(request);

        Integer demandenum = Integer.parseInt(param.getParameter("demandenum"));
        Connexion connexion = (Connexion) session.getAttribute("connexion");
        Demandes demande = service.getDemande().findDemande(demandenum);

        ProcessCBS cbs = new ProcessCBS();
        try {
            cbs.authenticate(cbsUrl, cbsPort, "M" + connexion.getRegistryuser().getLibrary(), cbsPassword);
            NoticeHelper noticehelper = new NoticeHelper(cbs);

            if (service.getDemande().canUserDeleteDemande(connexion.getUser(), demande)) {
                if (demande.getTitre() != null && !demande.getTitre().equals("NOTICE SUPPRIMÉE DU SUDOC")) {
                    if (demande.getTypesDemandes().getIdTypeDemande().equals(Constant.TYPE_DEMANDE_NUMEROTATION))
                        noticehelper.chercherEtSupprimerZoneNotice(demande.getNotice().getPpn(), "301", "$a", "(identifiant Cidemis : " + demande.getIdDemande() + ")");
                    else if (demande.getTypesDemandes().getIdTypeDemande().equals(Constant.TYPE_DEMANDE_CORRECTION))
                        noticehelper.chercherEtSupprimerZoneNotice(demande.getNotice().getPpn(), "830", "$a", "(identifiant Cidemis : " + demande.getIdDemande() + ")");
                }
                service.getDemande().delete(demande);

            }
        } catch (CBSException | ZoneException ex) {
            request.setAttribute("error_code", "ERROR_SUPPRESSION");
            request.setAttribute("texte_erreur", ex.getMessage());
            return MyDispatcher.ERRORNOTICEJSP;
        }
        return null;
    }

    @Description("Archivage d'une demande")
    @PostMapping("/ArchivageDemande")
    public String archivageDemande(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String forward = this.catchProcessRequest(request, response);
        if (!forward.isEmpty()) {
            return forward;
        }
        param.setRequest(request);

        String demandenum = param.getParameter("demandenum");
        Connexion connexion = (Connexion) session.getAttribute("connexion");
        Demandes demande = service.getDemande().findDemande(Integer.parseInt(demandenum));
        demande.setJournalDemandesList(service.getDemande().findJournalDemandesByDemandes(demande));
        if (service.getDemande().canUserArchiveDemande(connexion.getUser(), demande)) {
            service.getDemande().archiverDemande(demande, connexion.getUser());
        }
        return MyDispatcher.LISTE_DEMANDES;

    }

    @Override
    protected String getServletInfo() {
        return "Controleur pour les operations portant sur les demandes";
    }
}
