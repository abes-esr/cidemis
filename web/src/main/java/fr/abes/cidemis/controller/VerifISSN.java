package fr.abes.cidemis.controller;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Controller
public class VerifISSN extends AbstractServlet {
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
			json.put("exist", !getService().getDemande().findDemandesByISSN(issn, ppn).isEmpty());
		}
    	catch (JSONException e) {
			log.error( "VerifISSN Error", e);
			throw new ServletException();
		}
        
        out.print(json);
        out.flush();
    }

    @Override
    public String getServletInfo() {
        return "Check if ISSN is already use";
    }
}
