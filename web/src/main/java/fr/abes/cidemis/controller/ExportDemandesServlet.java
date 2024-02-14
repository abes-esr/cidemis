package fr.abes.cidemis.controller;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.exception.DaoException;
import fr.abes.cidemis.localisation.LocalProvider;
import fr.abes.cidemis.model.cidemis.Commentaires;
import fr.abes.cidemis.model.cidemis.Connexion;
import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.model.cidemis.Options;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@Slf4j
public class ExportDemandesServlet extends AbstractServlet {

    private static final String NL = "\n";

    @Override
    protected boolean checkSession() { return true; }

    @RequestMapping(value = "/exportdemande")
    public void exportDemande(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        param.setRequest(request);

        // Récupère toutes les demandes de l'utilisateur
        List<Demandes> demandesUtilisateur = getService().getDemande().findDemandesByCbsUsers(((Connexion)session.getAttribute("connexion")).getUser(), true, true);

        if (param.getParameter("id")!=null) {
            String[] ids = param.getParameter("id").split(",");
            List<Demandes> demandesExportees = new ArrayList();
            
            for(Demandes d : demandesUtilisateur)
                if (Arrays.asList(ids).contains(d.getIdDemande().toString()))
                    demandesExportees.add(d);
            
            if (!demandesExportees.isEmpty()){
                Boolean allColumns = "true".equals(param.getParameter("allcolumns"));
                Boolean includeComments = "true".equals(param.getParameter("includecomments"));
                boolean error = false;

                StringBuilder csv = null;
                try {
                    csv = createCsvFromDemandeList(demandesExportees,request,allColumns,includeComments);
                } catch (DaoException e) {
                    request.setAttribute("tier_exception", e.getTierOfException());
                    request.setAttribute("table_exception", e.getTableOfException());
                    request.setAttribute("message_exception", e.getMessage());
                    request.setAttribute("link_redirection", "/liste-demandes");
                }

                InputStream stream = new ByteArrayInputStream(csv.toString().getBytes());
                response.setContentType("text/csv;charset=" + Constant.ENCODE);
                response.setHeader("Content-disposition", "attachment;filename=\"demandes-list.csv\"");
                IOUtils.copy(stream , response.getOutputStream());
                response.flushBuffer();
            }
        }
    }
    
    /**
     * Créer le fichier CSV à partir des options récupérés
     * @param demandes
     * @param request
     * @param allColumns
     * @param includeComments
     * @return
     */
    public StringBuilder createCsvFromDemandeList(List<Demandes> demandes, HttpServletRequest request, Boolean allColumns, Boolean includeComments) throws DaoException {
        Connexion connexion = (Connexion)request.getSession(false).getAttribute("connexion");
        List<Options> colonnes = getService().getOptions().findOptionsColonnesByCbsUsers(connexion.getUser());
        
        // \ufeff : pour permettre à Microsoft Excel de reconnaitre l'encodage UTF-8 .... =))
        StringBuilder csv = new StringBuilder("\ufeff");
        LocalProvider lang = new LocalProvider(request.getLocale());
        
        String c = "";
        for (Options col:colonnes){
            if (col.getValue().contains("visible") || allColumns){
                csv.append(c + lang.getMsgNoHtmlEntities(col.getLibOption()));
                c = ";";
            }
        }
        
        if (includeComments)
            csv.append(";" + lang.getMsgNoHtmlEntities("col_comments"));
        csv.append(ExportDemandesServlet.NL);
            
        for(Demandes d:demandes){
            csv.append(this.getCSVDemande(d, colonnes, allColumns, includeComments));
        }
        return csv;
    }
    
    /**
     * Construit pour chaque demande la ligne CSV
     * @param demande
     * @param colonnes
     * @param allColumns
     * @param includeComments
     * @return
     */
    public StringBuilder getCSVDemande(Demandes demande, List<Options> colonnes, Boolean allColumns, Boolean includeComments) {
        String c = "";
        StringBuilder csv = new StringBuilder();
        
        for (Options col : colonnes) {
            if (col.getValue().contains("visible") || allColumns) {
                switch(col.getLibOption()){
                    case Constant.COL_DATE:
                        csv.append(c + demande.getDateDemandeFormatee().replaceAll("/;/", ","));
                        break;
                    case Constant.COL_DATE_MODIF:
                        csv.append(c + demande.getDateModifFormatee().replaceAll("/;/", ","));
                        break;
                    case Constant.COL_DEMANDE_TYPE:
                        csv.append(c + demande.getTypesDemandes().getLibelleTypeDemande());
                        break;
                    case Constant.COL_DEMANDE_NUM:
                        csv.append(c + demande.getIdDemande());
                        break;
                    case Constant.COL_PPN:
                        csv.append(c + "=\"" + ((demande.getNotice() != null) ? demande.getNotice().getPpn() : "") + "\"");
                        break;
                    case Constant.COL_TITRE:
                        csv.append(c + "\"" + ((demande.getTitre() != null) ? demande.getTitre().replaceAll("/;/", ",").replaceAll("\"","\"\"") + "\"" : ""));
                        break;
                    case Constant.COL_ETAT:
                        csv.append(c + demande.getEtatsDemandes().getLibelleEtatDemande());
                        break;
                    case Constant.COL_CENTRE_REGIONAL:
                        csv.append(c + demande.getCr());
                        break;
                    case Constant.COL_ISSN:
                        csv.append(c + "=\"" + ((demande.getNotice() != null) ? ((demande.getNotice().getIssn() != null) ? demande.getNotice().getIssn() : "") : "") + "\"");
                        break;
                    case Constant.COL_FRBNF:
                        csv.append(c + "=\"" + ((demande.getNotice() != null) ? ((demande.getNotice().getFrbnf() != null) ? demande.getNotice().getFrbnf() : "")  : "") + "\"");
                        break;
                    case Constant.COL_PUBLICATION_TYPE:
                        csv.append(c + ((demande.getNotice() != null) ? (demande.getNotice().getTypeRessource()) : ""));
                        break;
                    case Constant.COL_SUPPORT_TYPE:
                        csv.append(c + ((demande.getNotice() != null) ? (demande.getNotice().getTypeDocumentLibelle()) : ""));
                        break;
                    case Constant.COL_PUBLICATION_PAYS:
                        csv.append(c + ((demande.getNotice() != null) ? (demande.getNotice().getPays()) : ""));
                        break;
                    case Constant.COL_STATUT_DE_VIE:
                        csv.append(c + ((demande.getNotice() != null) ? ((demande.getNotice().getStatutdevie() ? "Mort" : "Vivant")) : ""));
                        break;
                    case Constant.COL_PUBLICATION_DATE:
                        csv.append(c + ((demande.getNotice() != null) ? (demande.getNotice().getDatePublication()) : ""));
                        break;
                    case Constant.COL_RCR:
                        csv.append(c + demande.getRcrDemandeur());
                        break;
                    case Constant.COL_TAGGUE:
                    	csv.append(c + ((demande.getTaggues() != null) ? demande.getTaggues().getLibelleTaggue() : ""));
                    	break;
                    default:
                        csv.append("");
                }
                
                c = ";";
            }
        }
        
        if (includeComments)
            csv.append(";" + getCommentairesCSV(demande));
        
        csv.append(ExportDemandesServlet.NL);
        return csv;
    }
    
    /**
     * Retourne pour une demande ses commentaires au format CSV
     * @param demande
     * @return
     */
    public StringBuilder getCommentairesCSV(Demandes demande){
        StringBuilder commentairecsv = new StringBuilder("\"");
        
        for (Commentaires comment:getService().getCommentaires().findCommentairesByDemandes(demande)){
        	commentairecsv.append( 
        			"Le " + comment.getDateCommentaireFormatee() + " par " + comment.getCbsUsers().getShortName() + " :" + ExportDemandesServlet.NL
                    + comment.getLibCommentaire().replaceAll("\"","\"\"")
                    + ExportDemandesServlet.NL + ExportDemandesServlet.NL + "--------------------------------" + ExportDemandesServlet.NL + ExportDemandesServlet.NL);
        }
        
        commentairecsv.append("\"");     
        return commentairecsv;
    }

    @Override
    public String getServletInfo() {
        return "Création du fichier d'export des demandes (depuis le menu)";
    }

}
