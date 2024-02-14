package fr.abes.cidemis.controller;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.exception.DaoException;
import fr.abes.cidemis.model.cidemis.Connexion;
import fr.abes.cidemis.model.cidemis.Options;
import fr.abes.cidemis.web.ParamHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class OptionsColonnesServlet extends AbstractServlet {

    @RequestMapping(value = "/optionscolonnes")
    public void optionsColonnes(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        Connexion connexion = (Connexion)session.getAttribute("connexion");
        ParamHelper param = new ParamHelper(request);
        
        Options colOption;
        List<Options> optionslist = new ArrayList();

        List<Options> optionslistuser = null;
        try {
            optionslistuser = getService().getOptions().findOptionsColonnesByCbsUsers(connexion.getUser());
        } catch (DaoException e) {
            e.printStackTrace();
            log.error("ERREUR: " + e.getTierOfException() + " : " + e.getTypeOfException() + " : TABLE " + e.getTableOfException());
        }

        // Récupère les options envoyées par la page HTML
        for (Map.Entry<String, String> col : Constant.getColonnes().entrySet()) {
            colOption = new Options();
            colOption.setLibOption(col.getKey());
            colOption.setValue(param.getParameter(col.getKey()));
            optionslist.add(colOption);
        }
        
        // On met à jour la liste des options de l'utilisateur avec celle de la page HTML
        for (Options optionuser : optionslistuser){
            for (Options option : optionslist){
                if(optionuser.getLibOption().equals(option.getLibOption())){
                    optionuser.setValue(option.getValue());
                    getService().getOptions().save(optionuser);
                }
            }
        }
   
        response.setContentType("text/xml;charset=" + Constant.ENCODE);
        response.getWriter().println("<option>OK</option>");
    }

    @Override
    public String getServletInfo() {
        return "Gestion de la préférences d'affichage des colonnes de l'utilisateur";
    }

}
