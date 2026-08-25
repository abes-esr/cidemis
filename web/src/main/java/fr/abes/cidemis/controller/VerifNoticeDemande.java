package fr.abes.cidemis.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.service.IDemandesService;
import fr.abes.cidemis.web.MyDispatcher;
import fr.abes.cidemis.web.ParamHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class VerifNoticeDemande extends AbstractServlet {
    private final ParamHelper param;
    private final IDemandesService demandes;

    public VerifNoticeDemande(ParamHelper param, IDemandesService demandes) {
        this.param = param;
        this.demandes = demandes;
    }

    @Value("${cidemis.url}")
    private String url;

    @PostMapping(value = "/verifnoticedemande")
    public Object verifNoticeDemande(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        this.catchProcessRequest(request, response);
        param.setRequest(request);
        String demandeNum = param.getParameter("demande_num");
        
        if (!"-1".equals(demandeNum)) {
            Demandes demande = this.demandes.findDemande(Integer.valueOf(demandeNum));
            List<String> zonesManquantes = new ArrayList<>();
            List<String> zonesPresentes = new ArrayList<>();
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
}
