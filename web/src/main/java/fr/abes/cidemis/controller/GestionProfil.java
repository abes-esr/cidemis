package fr.abes.cidemis.controller;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.model.cidemis.CbsUsers;
import fr.abes.cidemis.model.cidemis.Connexion;
import fr.abes.cidemis.model.cidemis.Roles;
import fr.abes.cidemis.web.MyDispatcher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.List;
@Controller
public class GestionProfil extends AbstractServlet {
    @Override
    protected boolean checkSession() { return true; }

	@RequestMapping(value = "/gestion-profil", method = RequestMethod.GET)
    public String gestionProfil(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String forward = this.catchProcessRequest(request, response);
        if (!forward.equals("")) {
            return forward;
        }
        Connexion connexion = (Connexion)session.getAttribute("connexion");
        
        // On vérifie que l'on est bien connecté avec le login de l'admin issn
        if (connexion.getUser().getUserKey().equals(Constant.ADMIN_ISSN)){
            // On récupère la liste des users de l'issn
            Roles roleIssn = getService().getUsers().findRoles(Constant.ROLE_ISSN);
            List<CbsUsers> users = getService().getUsers().findCbsUsersByRoles(roleIssn);
            CbsUsers u;
            
            // On retire l'admin de la liste
            Iterator<CbsUsers> it = users.iterator();
            while(it.hasNext()){
                u =  it.next();
                if (u.getUserKey().equals(Constant.ADMIN_ISSN))
                    it.remove();
            }
            
            request.setAttribute("users", users);
            return MyDispatcher.GESTIONPROFILJSP;
        }
        else {   
            return MyDispatcher.LISTE_DEMANDES;
        }
    }

    @Override
    public String getServletInfo() {
        return "JSP permettant la gestion des roles ISSN";
    }

}
