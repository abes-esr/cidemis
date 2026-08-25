package fr.abes.cidemis.controller;

import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fr.abes.cidemis.service.IDemandesService;
import fr.abes.cidemis.web.ParamHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class VerifISSN extends AbstractServlet {
    private final ParamHelper param;
    private final IDemandesService demandes;

    public VerifISSN(ParamHelper param, IDemandesService demandes) {
        this.param = param;
        this.demandes = demandes;
    }

    @Override
    protected boolean checkSession() { return true; }

    @RequestMapping(value = "/verifier-issn", method = RequestMethod.POST)
    public void processRequest(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        this.catchProcessRequest(request, response);
        this.param.setRequest(request);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject json = new JSONObject();

        String issn = param.getParameter("issn");
        String ppn = param.getParameter("ppn");
        
    	try {
			json.put("exist", !this.demandes.findDemandesByISSN(issn, ppn).isEmpty());
		}
    	catch (JSONException e) {
			log.error( "VerifISSN Error", e);
			throw new ServletException();
		}
        
        out.print(json);
        out.flush();
    }
}
