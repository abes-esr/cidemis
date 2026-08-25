package fr.abes.cidemis.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.exception.DaoException;
import fr.abes.cidemis.model.cidemis.Connexion;
import fr.abes.cidemis.model.cidemis.Options;
import fr.abes.cidemis.service.IOptionsService;
import fr.abes.cidemis.web.ParamHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class OptionsColonnesServlet extends AbstractServlet {
    private final ParamHelper param;
    private final IOptionsService options;

    public OptionsColonnesServlet(ParamHelper param, IOptionsService options) {
        this.param = param;
        this.options = options;
    }

    @RequestMapping(value = "/optionscolonnes")
    public void optionsColonnes(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        Connexion connexion = (Connexion)session.getAttribute("connexion");
        param.setRequest(request);
        
        Options colOption;
        List<Options> optionslist = new ArrayList<>();

        List<Options> optionslistuser = null;
        try {
            optionslistuser = this.options.findOptionsColonnesByCbsUsers(connexion.getUser());
        } catch (DaoException e) {
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
                    this.options.save(optionuser);
                }
            }
        }
   
        response.setContentType("text/xml;charset=" + Constant.ENCODE);
        response.getWriter().println("<option>OK</option>");
    }
}
