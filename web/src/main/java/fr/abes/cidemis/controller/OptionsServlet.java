package fr.abes.cidemis.controller;

import java.io.IOException;
import java.util.List;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.model.cidemis.Connexion;
import fr.abes.cidemis.model.cidemis.Options;
import fr.abes.cidemis.service.IOptionsService;
import fr.abes.cidemis.web.ParamHelper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@WebServlet("/options")
@Slf4j
public class OptionsServlet extends AbstractServlet {
    private final IOptionsService optionsService;
    private final ParamHelper param;

    public OptionsServlet(IOptionsService options, ParamHelper param) {
        this.optionsService = options;
        this.param = param;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        param.setRequest(request);
        String option = param.getParameter("option");
        String value = param.getParameter("value");
        Options userOption = null;
        Connexion connexion = (Connexion)session.getAttribute("connexion");
        List<Options> options = this.optionsService.findOptionsByCbsUsers(connexion.getUser());
        
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

        this.optionsService.save(userOption);
        response.setContentType("text/xml;charset=" + Constant.ENCODE);
        response.getWriter().println("<option>OK</option>");
    }
}
