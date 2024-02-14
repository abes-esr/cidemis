package fr.abes.cidemis.controller;

import fr.abes.cidemis.model.cidemis.Connexion;
import fr.abes.cidemis.web.ParamHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class UpdateEmailServlet extends AbstractServlet {
	@RequestMapping(value = "updateemail")
    public void updateEmail(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        ParamHelper param = new ParamHelper(request);
        String email = param.getParameter("email");
        
        Connexion connexion = (Connexion)session.getAttribute("connexion");
        connexion.getUser().setUserEmail(email);
        
        getService().getUsers().save(connexion.getUser());
        session.setAttribute("connexion", connexion);
    }

    @Override
    public String getServletInfo() {
        return "Permet à un utilisateur de modifier son adresse mail";
    }

}
