package fr.abes.cidemis.controller;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.exception.DaoException;
import fr.abes.cidemis.localisation.LocalProvider;
import fr.abes.cidemis.model.cidemis.Connexion;
import fr.abes.cidemis.model.cidemis.Options;
import fr.abes.cidemis.web.MyDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
public class ListeDemandesServlet extends AbstractServlet {

    protected boolean checkSession() {
        return true;
    }

	@RequestMapping(value = "/liste-demandes")
    public String listeDemandes(HttpServletRequest request, HttpServletResponse response, HttpSession session){
        String forward = this.catchProcessRequest(request, response);
        if (forward != "") {
            return forward;
        }
	    Connexion connexion = (Connexion)session.getAttribute("connexion");
        param.setRequest(request);

        /**
         * LISTE LES OPTIONS POUR LE MENU AFFICHER / MASQUER LES COLONNES
         */
        // On initialise la liste des options de colonnes
        // Cette liste est classée dans l'ordre des positions de colonnes dans la table
        List<Options> optionscolonnesListe = null;
        boolean error = false;
        try {
            optionscolonnesListe = getService().getOptions().findOptionsColonnesByCbsUsers(connexion.getUser());
        } catch (DaoException e) {
            request.setAttribute("tier_exception", e.getTierOfException());
            request.setAttribute("table_exception", e.getTableOfException());
            request.setAttribute("message_exception", e.getMessage());
            request.setAttribute("link_redirection", "/");
            error = true;
        }

        if(error){
            return MyDispatcher.ERROREXCEPTION;
        }

        // On écrira les colonnes de la table HTML dans l'ordre alphabétique et on ajoutera en attribut la position réelle qui sera envoyée à la DataTable
        List<Options> optionscolonnesListeOrdered = new ArrayList();
        LocalProvider lang = new LocalProvider(request.getLocale());
        for (Options o : optionscolonnesListe){
            o.setName(lang.getMsg(o.getLibOption()));
            o.setTypeInput(Constant.getColonnes().get(o.getLibOption()));
            optionscolonnesListeOrdered.add(new Options(o));
        }

        // On classe par ordre alphabétique
        Options optionsClass = new Options();
        Collections.sort(optionscolonnesListeOrdered, optionsClass.new OptionNameComparator());
        
        // On récupère les positions pour envoyer à l'initialisation de la DataTable
        int[] positions = new int[optionscolonnesListeOrdered.size()];
        for (int i = 0; i<optionscolonnesListeOrdered.size(); i=i+1){
           positions[i] = Collections.binarySearch(optionscolonnesListeOrdered, optionscolonnesListe.get(i),  optionsClass.new OptionNameComparator());
        }
        request.setAttribute("optionscolonnes_liste_ordered", optionscolonnesListeOrdered);
        request.setAttribute("positions", Arrays.toString(positions));
        
        if (param.getParameter("profil") != "") {
            session.setAttribute("profil", param.getParameter("profil"));
        }
        String login = "login";
        if ((Boolean) session.getAttribute(login)){
            session.setAttribute(login, false);
            request.setAttribute(login, true);
        }

        return MyDispatcher.LISTE_DEMANDESJSP;
    }

    @Override
    protected String getServletInfo() {
        return "Affiche tableau de bord";
    }
}
