package fr.abes.cidemis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.abes.cidemis.model.cidemis.Connexion;
import fr.abes.cidemis.service.IUsersService;
import fr.abes.cidemis.web.ParamHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class UpdateEmailServlet extends AbstractServlet {
    private final ParamHelper param;
    private final IUsersService users;

    public UpdateEmailServlet(ParamHelper param, IUsersService users) {
        this.param = param;
        this.users = users;
    }

	@RequestMapping(value = "updateemail")
    public void updateEmail(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        param.setRequest(request);
        String email = param.getParameter("email");
        
        Connexion connexion = (Connexion)session.getAttribute("connexion");
        connexion.getUser().setUserEmail(email);
        
        this.users.save(connexion.getUser());
        session.setAttribute("connexion", connexion);
    }
}
