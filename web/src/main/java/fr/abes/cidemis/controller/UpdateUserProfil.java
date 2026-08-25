package fr.abes.cidemis.controller;

import java.io.IOException;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.model.cidemis.CbsUsers;
import fr.abes.cidemis.model.cidemis.Connexion;
import fr.abes.cidemis.service.IUsersService;
import fr.abes.cidemis.web.ParamHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "UpdateUserProfil", urlPatterns = {"/update-user-profil"})
public class UpdateUserProfil extends AbstractServlet {
    private final ParamHelper param;
    private final IUsersService users;

    public UpdateUserProfil(ParamHelper param, IUsersService users) {
        this.param = param;
        this.users = users;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        Connexion connexion = (Connexion)session.getAttribute("connexion");
        param.setRequest(request);
        
        // On vérifie que l'on est bien connecté avec le login de l'admin issn
        if (connexion.getUser().getUserKey().equals(Constant.ADMIN_ISSN)){
            Integer userNum = Integer.valueOf(param.getParameter("user"));
            Integer profilNum = Integer.valueOf(param.getParameter("profil"));
            
            CbsUsers user = this.users.findCbsUsers(userNum);
            this.users.updateProfil(user, profilNum);
        }
    }
}
