package fr.abes.cidemis.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.model.cidemis.PiecesJustificatives;
import fr.abes.cidemis.service.IDemandesService;
import fr.abes.cidemis.service.IPiecesJustificativesService;
import fr.abes.cidemis.web.MyDispatcher;
import fr.abes.cidemis.web.ParamHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class PieceJointeServlet extends AbstractServlet {
    private final ParamHelper param;
    private final IDemandesService demandes;
    private final IPiecesJustificativesService piecesJustificatives;

    public PieceJointeServlet(ParamHelper param, IPiecesJustificativesService piecesJustificatives, IDemandesService demandes) {
        this.param = param;
        this.demandes = demandes;
        this.piecesJustificatives = piecesJustificatives;
    }

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
        Demandes demande = this.demandes.findDemande(Integer.valueOf(demandenum));
        List<PiecesJustificatives> piecesJustificativesList = this.piecesJustificatives.findPiecesJustificativesByDemandes(demande);

        request.setAttribute("demande", demande);
        request.setAttribute("piecesJustificatives", piecesJustificativesList);
        return MyDispatcher.PIECEJOINTE;
    }
}
