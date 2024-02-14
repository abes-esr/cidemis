package fr.abes.cidemis.controller;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.model.cidemis.CidemisNotices;
import fr.abes.cidemis.web.MyDispatcher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ChercherNotice extends AbstractServlet {
	private static final String TYPE_DEMANDE = "type_demande";

	@Override
	protected boolean checkSession() { return true; }

	@RequestMapping(value = "/chercher-notice", method = RequestMethod.POST)
    public String processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String forward = this.catchProcessRequest(request, response);
        if (!forward.equals("")) {
            return forward;
        }
        param.setRequest(request);
        Integer typeDemande = Integer.parseInt(param.getParameter(ChercherNotice.TYPE_DEMANDE));
        String search = param.getParameter("search");
        String searchedValue = param.getParameter("searched_value");
        
        //Si création de notice
        if (typeDemande.equals(Constant.TYPE_DEMANDE_CREATION)){
            request.setAttribute(ChercherNotice.TYPE_DEMANDE, typeDemande);
            request.setAttribute("titre", searchedValue);
            request.setAttribute("notice", new CidemisNotices());
            return "forward:" + MyDispatcher.CREATION_DEMANDE;
        }
        // Si demande de numérotation ou correction
        else {
            if ((searchedValue.length()==9) &&  searchedValue.matches("(\\d{9})|((?i)\\d{8}X{1})"))
                return this.checkNotice(request, getService().getTools().findCidemisNotice(searchedValue), searchedValue, search, typeDemande);
            else
                return MyDispatcher.PPNINCORRECTJSP;
        } 
    }
	
	protected String checkNotice(HttpServletRequest request, CidemisNotices notice, String searchedValue, String search, Integer typeDemande ) {
	    if (!(notice == null || notice.getPpn().isEmpty())) {
	        // Si la notice a été supprimé
            if ("d".equals(notice.getStatutNotice())){
                request.setAttribute("ppn", searchedValue);
                return MyDispatcher.NOTICESUPPRIMEEJSP;
            }
            else {
                if (notice.getTypeDocument().matches(".(s|i|d)")){
                    request.setAttribute("notice", notice);
                    request.setAttribute(ChercherNotice.TYPE_DEMANDE, typeDemande);
                    request.setAttribute("search", search);
    
                    List<String> zonesManquantes = new ArrayList();
                    List<String> zonesPresentes = new ArrayList();
    
                    notice.checkZones(typeDemande, zonesManquantes, zonesPresentes);
    
                    request.setAttribute("zones_manquantes", zonesManquantes);
                    request.setAttribute("zones_presentes", zonesPresentes);
    
                    return MyDispatcher.CHERCHERNOTICEJSP;
                }
                else {
                   return MyDispatcher.NOTICEBIBLIOJSP;
                }
            }
	    }
        else {
            request.setAttribute("ppn", searchedValue);
            return MyDispatcher.AUCUNENOTICEJSP;
        }
	}

    @Override
    public String getServletInfo() {
        return "Permet de retourner les notices lors de la recherche d'un PPN";
    }
}
