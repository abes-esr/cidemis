package fr.abes.cidemis.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.exception.DaoException;
import fr.abes.cidemis.localisation.LocalProvider;
import fr.abes.cidemis.model.cidemis.Commentaires;
import fr.abes.cidemis.model.cidemis.Connexion;
import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.model.cidemis.Options;
import fr.abes.cidemis.service.ICommentairesService;
import fr.abes.cidemis.service.IDemandesService;
import fr.abes.cidemis.service.IOptionsService;
import fr.abes.cidemis.web.ParamHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class ExportDemandesServlet extends AbstractServlet {
    private static final String NL = "\n";
    private final ParamHelper param;
    private final IDemandesService demandes;
    private final ICommentairesService commentaires;
    private final IOptionsService options;

    public ExportDemandesServlet(ParamHelper param, ICommentairesService commentaires, IDemandesService demandes, IOptionsService options) {
        this.param = param;
        this.demandes = demandes;
        this.commentaires = commentaires;
        this.options = options;
    }

    @Override
    protected boolean checkSession() { return true; }

    @RequestMapping(value = "/exportdemande")
    public void exportDemande(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        param.setRequest(request);

        // Récupère toutes les demandes de l'utilisateur
        List<Demandes> demandesUtilisateur = this.demandes.findDemandesByCbsUsers(((Connexion)session.getAttribute("connexion")).getUser(), true, true);

        if (param.getParameter("id")!=null) {
            String[] ids = param.getParameter("id").split(",");
            List<Demandes> demandesExportees = new ArrayList<>();
            
            for(Demandes d : demandesUtilisateur)
                if (Arrays.asList(ids).contains(d.getIdDemande().toString()))
                    demandesExportees.add(d);
            
            if (!demandesExportees.isEmpty()){
                Boolean allColumns = "true".equals(param.getParameter("allcolumns"));
                Boolean includeComments = "true".equals(param.getParameter("includecomments"));

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
        List<Options> colonnes = this.options.findOptionsColonnesByCbsUsers(connexion.getUser());
        
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
            csv.append(";").append(lang.getMsgNoHtmlEntities("col_comments"));
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
                    case Constant.COL_DATE -> csv.append(c).append(demande.getDateDemandeFormatee().replaceAll("/;/", ","));
                    case Constant.COL_DATE_MODIF -> csv.append(c).append(demande.getDateModifFormatee().replaceAll("/;/", ","));
                    case Constant.COL_DEMANDE_TYPE -> csv.append(c).append(demande.getTypesDemandes().getLibelleTypeDemande());
                    case Constant.COL_DEMANDE_NUM -> csv.append(c).append(demande.getIdDemande());
                    case Constant.COL_PPN -> csv.append(c).append("=\"").append((demande.getNotice() != null) ? demande.getNotice().getPpn() : "").append("\"");
                    case Constant.COL_TITRE -> csv.append(c + "\"" + ((demande.getTitre() != null) ? demande.getTitre().replaceAll("/;/", ",").replaceAll("\"","\"\"") + "\"" : ""));
                    case Constant.COL_ETAT -> csv.append(c).append(demande.getEtatsDemandes().getLibelleEtatDemande());
                    case Constant.COL_ILN -> csv.append(c).append(demande.getCr());
                    case Constant.COL_ISSN -> csv.append(c).append("=\"").append((demande.getNotice() != null) ? ((demande.getNotice().getIssn() != null) ? demande.getNotice().getIssn() : "") : "").append("\"");
                    case Constant.COL_FRBNF -> csv.append(c).append("=\"").append((demande.getNotice() != null) ? ((demande.getNotice().getFrbnf() != null) ? demande.getNotice().getFrbnf() : "")  : "").append("\"");
                    case Constant.COL_PUBLICATION_TYPE -> csv.append(c).append((demande.getNotice() != null) ? (demande.getNotice().getTypeRessource()) : "");
                    case Constant.COL_SUPPORT_TYPE -> csv.append(c).append((demande.getNotice() != null) ? (demande.getNotice().getTypeDocumentLibelle()) : "");
                    case Constant.COL_PUBLICATION_PAYS -> csv.append(c).append((demande.getNotice() != null) ? (demande.getNotice().getPays()) : "");
                    case Constant.COL_STATUT_DE_VIE -> csv.append(c).append((demande.getNotice() != null) ? ((demande.getNotice().getStatutdevie() ? "Mort" : "Vivant")) : "");
                    case Constant.COL_PUBLICATION_DATE -> csv.append(c).append((demande.getNotice() != null) ? (demande.getNotice().getDatePublication()) : "");
                    case Constant.COL_RCR -> csv.append(c).append(demande.getRcrDemandeur());
                    case Constant.COL_TAGGUE -> csv.append(c).append((demande.getTaggues() != null) ? demande.getTaggues().getLibelleTaggue() : "");
                    default -> csv.append("");
                }
                
                c = ";";
            }
        }
        
        if (includeComments)
            csv.append(";").append(getCommentairesCSV(demande));
        
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
        
        for (Commentaires comment:this.commentaires.findCommentairesByDemandes(demande)){
        	commentairecsv.append( 
        			"Le " + comment.getDateCommentaireFormatee() + " par " + comment.getCbsUsers().getShortName() + " :" + ExportDemandesServlet.NL
                    + comment.getLibCommentaire().replaceAll("\"","\"\"")
                    + ExportDemandesServlet.NL + ExportDemandesServlet.NL + "--------------------------------" + ExportDemandesServlet.NL + ExportDemandesServlet.NL);
        }
        
        commentairecsv.append("\"");     
        return commentairecsv;
    }
}
