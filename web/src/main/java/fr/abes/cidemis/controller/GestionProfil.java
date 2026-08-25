package fr.abes.cidemis.controller;

import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.model.cidemis.CbsUsers;
import fr.abes.cidemis.model.cidemis.Connexion;
import fr.abes.cidemis.model.cidemis.Roles;
import fr.abes.cidemis.service.IUsersService;
import fr.abes.cidemis.web.MyDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
@Controller
public class GestionProfil extends AbstractServlet {
    private final IUsersService users;

    public GestionProfil(IUsersService users) {
        this.users = users;
    }

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
            Roles roleIssn = this.users.findRoles(Constant.ROLE_ISSN);
            List<CbsUsers> usersList = this.users.findCbsUsersByRoles(roleIssn);
            CbsUsers u;
            
            // On retire l'admin de la liste
            Iterator<CbsUsers> it = usersList.iterator();
            while(it.hasNext()){
                u =  it.next();
                if (u.getUserKey().equals(Constant.ADMIN_ISSN))
                    it.remove();
            }
            
            request.setAttribute("users", usersList);
            return MyDispatcher.GESTIONPROFILJSP;
        }
        else {   
            return MyDispatcher.LISTE_DEMANDES;
        }
    }
}
