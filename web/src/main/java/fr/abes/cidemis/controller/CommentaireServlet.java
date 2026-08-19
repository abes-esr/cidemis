package fr.abes.cidemis.controller;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.model.cidemis.Commentaires;
import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.web.MyDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class CommentaireServlet extends AbstractServlet {
    @RequestMapping(value = "/commentaire", method = RequestMethod.POST)
    public String commentaire(HttpServletRequest request, HttpServletResponse response) {
        String forward = this.catchProcessRequest(request, response);
        if (!forward.isEmpty()) {
            return forward;
        }
        response.setContentType("text/html;charset=" + Constant.ENCODE);

        param.setRequest(request);
        Integer demandenum = Integer.parseInt(param.getParameter("demandenum"));
        Demandes demande  = getService().getDemande().findDemande(demandenum);
        List<Commentaires> commentaires = getService().getCommentaires().findCommentairesByDemandes(demande);
        request.setAttribute("demande", demande);
        request.setAttribute("commentaires", commentaires);
        return MyDispatcher.COMMENTAIRE;
    }

    @Override
    public String getServletInfo() {
        return "Popup pour afficher les commentaires d'une demande dans la liste des demandes";
    }

}
