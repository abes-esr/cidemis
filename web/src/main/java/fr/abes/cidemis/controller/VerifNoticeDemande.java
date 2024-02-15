package fr.abes.cidemis.controller;

import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.web.MyDispatcher;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class VerifNoticeDemande extends AbstractServlet {
    @Autowired
    DemandeController demandeController;
    @Value("${cidemis.url}")
    private String url;

    @Override
    protected boolean checkSession() {
        return true;
    }

    @PostMapping(value = "/verifnoticedemande")
    public Object verifNoticeDemande(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        this.catchProcessRequest(request, response);
        param.setRequest(request);
        String demandeNum = param.getParameter("demande_num");
        
        if (!"-1".equals(demandeNum)) {
            Demandes demande = getService().getDemande().findDemande(Integer.parseInt(demandeNum));
            List<String> zonesManquantes = new ArrayList();
            List<String> zonesPresentes = new ArrayList();
            String errorCode;
            
            // Vérification de la demande
            if (demande!=null) {
                if (demande.getNotice().getTypeDocument().matches(".(s|i|d)")) {
                    errorCode = demande.checkZones(zonesManquantes, zonesPresentes) ? "" : "ZONE";
                }
                else {
                    errorCode = "ERROR_TYPE_DOC";
                }
            }
            else {
                errorCode = "ERROR_DEMANDE_INEXISTANTE";
            }

            // Si il y a une erreur, gestion de l'affichage
            if (!errorCode.isEmpty()){
                request.setAttribute("error_code", errorCode);
                request.setAttribute("zones_manquantes", zonesManquantes);
                request.setAttribute("zones_presentes", zonesPresentes);

                return MyDispatcher.VERIFNOTICEDEMANDEJSP;
            } 
        }
        HttpClient client = HttpClients.custom().build();
        HttpUriRequest newRequest = RequestBuilder.get()
                .setHeader(HttpHeaders.CONTENT_TYPE, "multipart/form-data")
                .setUri(url + "creer-demande")
                .build();
        client.execute(newRequest);
        return null;
        //si la vérification est ok, on envoie directement au controller de création de la demande, pour ne pas avoir à repasser par le client.
        //return demandeController.creationDemande(request, response, session);
    }
	
    @Override
    public String getServletInfo() {
        return "Vérifie qu'une notice peut avoir une demande (tous les champs présents par exemple)";
    }

}
