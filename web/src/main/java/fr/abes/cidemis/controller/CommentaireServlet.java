package fr.abes.cidemis.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.model.cidemis.Commentaires;
import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.service.ICommentairesService;
import fr.abes.cidemis.service.IDemandesService;
import fr.abes.cidemis.web.MyDispatcher;
import fr.abes.cidemis.web.ParamHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class CommentaireServlet extends AbstractServlet {
    private final ParamHelper param;
    private final IDemandesService demandes;
    private final ICommentairesService commentaires;

    public CommentaireServlet(ParamHelper param, ICommentairesService commentaires, IDemandesService demandes) {
        this.param = param;
        this.demandes = demandes;
        this.commentaires = commentaires;
    }

    @RequestMapping(value = "/commentaire", method = RequestMethod.POST)
    public String commentaire(HttpServletRequest request, HttpServletResponse response) {
        String forward = this.catchProcessRequest(request, response);
        if (!forward.isEmpty()) {
            return forward;
        }
        response.setContentType("text/html;charset=" + Constant.ENCODE);

        param.setRequest(request);
        Integer demandenum = Integer.valueOf(param.getParameter("demandenum"));
        Demandes demande  = this.demandes.findDemande(demandenum);
        List<Commentaires> commentairesList = this.commentaires.findCommentairesByDemandes(demande);
        request.setAttribute("demande", demande);
        request.setAttribute("commentaires", commentairesList);
        return MyDispatcher.COMMENTAIRE;
    }
}
