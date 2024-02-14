package fr.abes.cidemis.controller;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.model.cidemis.PiecesJustificatives;
import fr.abes.cidemis.web.MyDispatcher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class PieceJointeServlet extends AbstractServlet {
    @Override
    protected boolean checkSession() {return true; }

    @RequestMapping(value = "/piece-jointe", method = RequestMethod.POST)
    protected String processRequest(HttpServletRequest request, HttpServletResponse response) {
        String forward = this.catchProcessRequest(request, response);
        if (!forward.equals("")) {
            return forward;
        }
        response.setContentType("text/html;charset=" + Constant.ENCODE);

        param.setRequest(request);
        String demandenum = param.getParameter("demandenum");
        Demandes demande = getService().getDemande().findDemande(Integer.parseInt(demandenum));
        List<PiecesJustificatives> piecesJustificatives = getService().getPiecesJustificatives().findPiecesJustificativesByDemandes(demande);

        request.setAttribute("demande", demande);
        request.setAttribute("piecesJustificatives", piecesJustificatives);
        return MyDispatcher.PIECEJOINTE;
    }

    @Override
    public String getServletInfo() {
        return "Popup pour afficher les pièces jointes d'une demande dans la liste des demandes";
    }

}
