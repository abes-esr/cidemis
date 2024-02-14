package fr.abes.cidemis.controller;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.model.cidemis.Connexion;
import fr.abes.cidemis.model.cidemis.Options;
import fr.abes.cidemis.service.CidemisManageService;
import fr.abes.cidemis.web.ParamHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/options")
@Slf4j
public class OptionsServlet extends AbstractServlet {
	private static final long serialVersionUID = 8908373273328585599L;
    @Autowired
    private CidemisManageService service;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        ParamHelper param = new ParamHelper(request);
        String option = param.getParameter("option");
        String value = param.getParameter("value");
        Options userOption = null;
        Connexion connexion = (Connexion)session.getAttribute("connexion");
        List<Options> options = service.getOptions().findOptionsByCbsUsers(connexion.getUser());
        
        for (Options o:options)
            if (o.getLibOption().equals(option))
                userOption = o;
        
        if (userOption!=null){
            userOption.setValue(value);
        }
        else{
            userOption = new Options();
            userOption.setCbsUsers(connexion.getUser());
            userOption.setLibOption(option);
            userOption.setValue(value);
        }

        service.getOptions().save(userOption);
        response.setContentType("text/xml;charset=" + Constant.ENCODE);
        response.getWriter().println("<option>OK</option>");
    }

    @Override
    public String getServletInfo() {
        return "Permet à l'utilisateur d'enregistrer ses options d'affichages";
    }

}
