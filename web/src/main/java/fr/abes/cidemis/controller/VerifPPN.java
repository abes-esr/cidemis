package fr.abes.cidemis.controller;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.model.cidemis.CidemisNotices;
import fr.abes.cidemis.model.cidemis.Demandes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
public class VerifPPN extends AbstractServlet {
    @Override
    protected boolean checkSession() { return true; }

    @RequestMapping(value = "/verifier-ppn", method = RequestMethod.POST)
    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.catchProcessRequest(request, response);
        param.setRequest(request);
        PrintWriter out = response.getWriter();
        String ppn = param.getParameter("ppn");
        CidemisNotices notice = getService().getTools().findCidemisNotice(ppn);

        if (notice != null && !notice.getPpn().isEmpty()){
            this.checkPPN(out, notice);
        }
        else {
            out.print("AUCUNE_NOTICE");
        }
    }
	
	protected void checkPPN(PrintWriter out, CidemisNotices notice) {
	    if (notice.getCentreISSN() != null && !notice.getCentreISSN().isEmpty()) {
            out.print("ISSN_EXISTANT");
        }
        else if (!notice.getTypeDocument().matches(".(s|i|d)")) {
            out.print("MONOGRAPHIE");
        }
        else {
            // On doit maintenant vérifier qu'il n'y a pas de demande déjà en cours sur ce PPN
            List<Demandes> demandes = getService().getDemande().findDemandesByPPN(notice.getPpn());
            boolean find = false;
            
            for (Demandes d:demandes){
                if (d.getTypesDemandes().getIdTypeDemande().equals(Constant.TYPE_DEMANDE_NUMEROTATION) &&
                        !(d.getEtatsDemandes().getIdEtatDemande().equals(Constant.ETAT_TRAITEMENT_TERMINE_REFUSEE) ||
                        d.getEtatsDemandes().getIdEtatDemande().equals(Constant.ETAT_TRAITEMENT_REJETEE_PAR_CR) ||
                        d.getEtatsDemandes().getIdEtatDemande().equals(Constant.ETAT_ARCHIVEE))){
                        find = true;
                }
            }
            out.print(find ? "DEMANDE_EXISTANTE" : "OK");
        }
	}

    @Override
    public String getServletInfo() {
        return "Vérifie que le PPN saisi est ok et que je peux faire une demande dessus";
    }
}
