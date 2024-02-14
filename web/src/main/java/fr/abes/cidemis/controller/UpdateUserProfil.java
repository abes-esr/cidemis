package fr.abes.cidemis.controller;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.model.cidemis.CbsUsers;
import fr.abes.cidemis.model.cidemis.Connexion;
import fr.abes.cidemis.web.ParamHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "UpdateUserProfil", urlPatterns = {"/update-user-profil"})
public class UpdateUserProfil extends AbstractServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        Connexion connexion = (Connexion)session.getAttribute("connexion");
        ParamHelper param = new ParamHelper(request);
        
        // On vérifie que l'on est bien connecté avec le login de l'admin issn
        if (connexion.getUser().getUserKey().equals(Constant.ADMIN_ISSN)){
            Integer userNum = Integer.parseInt(param.getParameter("user"));
            Integer profilNum = Integer.parseInt(param.getParameter("profil"));
            
            CbsUsers user = getService().getUsers().findCbsUsers(userNum);
            getService().getUsers().updateProfil(user, profilNum);
        }
    }

    @Override
    public String getServletInfo() {
        return "Mise à jour du profil d'un utilisateur (gestion uniquement par ADMIN ISSN)";
    }
}
