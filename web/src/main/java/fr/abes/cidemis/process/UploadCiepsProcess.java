package fr.abes.cidemis.process;

import fr.abes.cbs.exception.CBSException;
import fr.abes.cbs.exception.ZoneException;
import fr.abes.cbs.process.ProcessCBS;
import fr.abes.cidemis.components.Fichier;
import fr.abes.cidemis.components.NoticeHelper;
import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.dao.cidemis.CidemisDaoProvider;
import fr.abes.cidemis.model.cidemis.*;
import fr.abes.cidemis.service.CidemisManageService;
import fr.abes.cidemis.utils.ISSN;
import fr.abes.cidemis.web.ParamHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class UploadCiepsProcess {
    private static final String ACCEPTED = "ACCEPTED";
    private static final String REJECTED = "REJECTED";
    private static final String COMPLETED = "COMPLETED";
    private static final String INFORMATION_NEEDED = "INFORMATION NEEDED";
    
    private static final String LINE_NUMBER = "Line n°";

    @Autowired
    private CidemisManageService service;
    @Autowired
    private CidemisDaoProvider dao;
    private NoticeHelper noticehelper = null;
    private Connexion connexion = null;
    private List<String> results = new ArrayList();

    @Value("${cbs.url}")
    private String cbsUrl;
    @Value("${cbs.port}")
    private String cbsPort;
    @Value("${cbs.password}")
    private String cbsPassword;

    private int linesEmpty = 0;
    private int linesValid = 0;
    private int nbLine = 0;

    /**
     * Récupère la requête et la traite avec la lecture du fichier
     * @param session
     * @param request
     */
    public void processRequest(HttpSession session, HttpServletRequest request) {
        this.connexion = (Connexion) session.getAttribute("connexion");
        ParamHelper param = new ParamHelper(request);
        List<Fichier> fichierList = param.getFile("fichier_cieps");
        Fichier fichier = fichierList.get(0);
        
        // Connexion avec le CBS
        ProcessCBS cbs = new ProcessCBS();
        try {
        	cbs.authenticate(cbsUrl, cbsPort, "M" + this.connexion.getRegistryuser().getLibrary(), cbsPassword);
        }catch (CBSException ex) {
        	log.error( "Erreur d'authentification au CBS : " + ex);
        }
        this.noticehelper = new NoticeHelper(cbs);
        this.results = new ArrayList();
        
        this.linesEmpty = 0;
        this.linesValid = 0;
        this.nbLine = 0;
        
        // Parcours du document Excel
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(fichier.getFile()));
            
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            
            // Pour chaque ligne du fichier
            for (Iterator<?> rowIt = sheet.rowIterator(); rowIt.hasNext();) {
                this.processLine(rowIt);
            }
        }
        catch (IOException e) {
            log.error( "Error UploadCieps", e);
        }
        
        // Renseignement des valeurs pour le diagnostic
        request.setAttribute("lines_count", this.nbLine);
        request.setAttribute("lines_valid", this.linesValid);
        request.setAttribute("linesEmpty", this.linesEmpty);
        request.setAttribute("results", this.results);
    }
    
    /**
     * Pour chaque ligne du fichier, la fonction regarde si elle n'est pas vide et si elle semble valide
     * @param rowIt
     */
    protected void processLine(Iterator<?> rowIt) {
        this.nbLine++;
        
        Demandes demande;
        HSSFRow row = (HSSFRow) rowIt.next();

        String demandeNum = this.getCellValue(row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
        String requestType = this.getCellValue(row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
        String concernedField = this.getCellValue(row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
        String countryCode = this.getCellValue(row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
        String centreIssn = this.getCellValue(row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
        String typePub = this.getCellValue(row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
        String titre = this.getCellValue(row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
        String status = this.getCellValue(row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
        String commentairetxt = this.getCellValue(row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
        String commentairelist = this.getCellValue(row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
        String ppn = this.getCellValue(row.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
        String attachments = this.getCellValue(row.getCell(11, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
        String links = this.getCellValue(row.getCell(12, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
        
        // Si la ligne n'est pas une ligne d'entête
        if (!this.isHeaderRow(demandeNum)) {
            // Si la ligne est vide
            if ((demandeNum + requestType + concernedField + countryCode + centreIssn + typePub + titre + status + commentairetxt + commentairelist + attachments + links).isEmpty() && "000000000".equals(ppn)) {
                this.linesEmpty++;
            }
            // Si c'est effectivement une ligne a traiter
            else if (demandeNum.matches("^[0-9]+$")) {
                demande = service.getDemande().findDemande(Integer.parseInt(demandeNum));
                demande.setCommentairesList(service.getCommentaires().findCommentairesByDemandes(demande));
                demande.setJournalDemandesList(service.getDemande().findJournalDemandesByDemandes(demande));
                try {
                    this.processDemande(demande, ppn, status, commentairetxt);
                } catch (CBSException | ZoneException e) {
                    this.addMessage(demande, "Problem in title : " + e.getMessage());
                }
            }
            // Sinon il y a une erreur sur le numéro de la demande
            else {
                this.addMessage(null, "Invalid request number. Should be digit only");
            }
        }
    }
    
    /**
     * Après que la ligne a été vérifié, cette fonction vers vérifier que tout est valide puis l'enregistrer
     * - L'utilisateur peut-il enregistrer la demande
     * - Le PPN est-il correct et corresponds t'il à celui de la demande
     * @param demande
     * @param ppn
     * @param status
     * @param commentairetxt
     */
    protected void processDemande(Demandes demande, String ppn, String status, String commentairetxt) throws ZoneException, CBSException {
        if (service.getDemande().canUserModifyDemande(this.connexion.getUser(), demande)) {
            if (demande.getNotice().getPpn().equals(ppn)) {
                if ((!status.isEmpty() || demande.getTypesDemandes().getIdTypeDemande().equals(Constant.TYPE_DEMANDE_CORRECTION)) && this.checkDemande(demande, status, commentairetxt)) {
                    this.saveDemande(demande, ppn, status, commentairetxt);
                }
                else {
                    this.addMessage(demande, "Request status missing.");
                }
            }
            else {
                this.addMessage(demande, "The provided ppn '" + ppn + "' does not match the request's ppn in our system.");
            }
        }
        else if (demande.getEtatsDemandes().getIdEtatDemande().equals(Constant.ETAT_TRAITEMENT_REJETEE_PAR_CR)  || demande.getEtatsDemandes().getIdEtatDemande().equals(Constant.ETAT_TRAITEMENT_TERMINE_ACCEPTEE) || demande.getEtatsDemandes().getIdEtatDemande().equals(Constant.ETAT_TRAITEMENT_TERMINE_REFUSEE)) {
            this.addMessage(demande, "You can't modify request number '" + demande.getIdDemande() + "'. This request is already closed.");
        }
        else {
            this.addMessage(demande, "You can't modify request number '" + demande.getIdDemande() + "'. This request is assigned to another role.");
        }
    }
    
    /**
     * Vérifie que les informations contenu dans le fichier sont logiques
     * @param demande
     * @param status
     * @param commentairetxt
     * @return
     */
    public boolean checkDemande(Demandes demande, String status, String commentairetxt) {
        Boolean success = true;
        String issn = status;
        
        if (UploadCiepsProcess.REJECTED.equals(status)) {
            if (!commentairetxt.isEmpty()) {
                demande.setEtatsDemandes(service.getReference().findEtatsdemandes(Constant.ETAT_TRAITEMENT_TERMINE_REFUSEE));
            }
            else {
                this.addMessage(demande, "Missing comment.");
                success = false;
            }
        }
        else if (UploadCiepsProcess.INFORMATION_NEEDED.equals(status)) {
            if (!commentairetxt.isEmpty()){
                demande.setEtatsDemandes(service.getReference().findEtatsdemandes(Constant.ETAT_EN_ATTENTE_PRECISION_RESPONSABLE_CR));
            }
            else {
                this.addMessage(demande, "Missing comment.");
                success = false;
            }
        }
        else {
            // Si c'est un numéro ISSN
            if (status.matches("^\\d{4}-\\d{3}[\\dxX]$") || demande.getTypesDemandes().getIdTypeDemande().equals(Constant.TYPE_DEMANDE_CORRECTION)) {
                if (!ISSN.isValidISSN(issn)) {
                    this.addMessage(demande, "Invalid ISSN number: check code not correct: '" + status + "'.");
                    success = false;
                }
                else if (commentairetxt.equals(UploadCiepsProcess.COMPLETED)) {
                    demande.setEtatsDemandes(service.getReference().findEtatsdemandes(Constant.ETAT_TRAITEMENT_TERMINE_ACCEPTEE));
                }
                else if (demande.getTypesDemandes().getIdTypeDemande().equals(Constant.TYPE_DEMANDE_CORRECTION)) {
                    this.addMessage(demande, "No action has been taken with this request as there is no keyword in neither of the ISSN nor the COMMENT columns.");
                    success = false;
                }
                else {
                    this.addMessage(demande, "An ISSN has been provided but the keyword \"COMPLETED\" is missing from the comment column.");
                    success = false;
                }
            }
            else {
                this.addMessage(demande, "Invalid request status or incorrect ISSN number format '" + status + "'.");
                success = false;
            }
        }
        
        return success;
    }
    
    public void saveDemande(Demandes demande, String ppn, String status, String commentairetxt) throws ZoneException, CBSException {
        this.linesValid++;

        String issn = status;
        Date dateNow = new Date();
        String dateNowFormat = new SimpleDateFormat("yyyy-MM-dd").format(dateNow);
        List<String> erreursNoticeZone = new ArrayList();
        String idCidemis = " (identifiant Cidemis : " + demande.getIdDemande()+")";

        // //////////////////////////////////////////////////////////////////////////////////////////////
        // MODIFICATIONS NOTICES PAR APICBS /////////////////////////////////////////////////////////////
        if (UploadCiepsProcess.REJECTED.equals(status)){
            if (demande.getTypesDemandes().getIdTypeDemande().equals(Constant.TYPE_DEMANDE_CORRECTION))
                this.deleteAndAddZone(demande, "830", "$a", idCidemis, "Demande de correction ISSN refusée pour la zone " + demande.getZones() + " le " + dateNowFormat + ". Raison refus: " + commentairetxt.replaceAll("[\n\r]", "") + idCidemis);
            else if (demande.getTypesDemandes().getIdTypeDemande().equals(Constant.TYPE_DEMANDE_NUMEROTATION))
                this.deleteAndAddZone(demande, "301", "$a", idCidemis, "Demande de numérotation refusée par ISSN le " + dateNowFormat + ". Raison refus: " + commentairetxt.replaceAll("[\n\r]", "") + idCidemis);
        }
        else if (UploadCiepsProcess.ACCEPTED.equals(commentairetxt) || UploadCiepsProcess.COMPLETED.equals(commentairetxt)){
            if (demande.getTypesDemandes().getIdTypeDemande().equals(Constant.TYPE_DEMANDE_NUMEROTATION)){
                // Si il n'y a pas de n°ISSN dans la notice, on l'ajoute
                CidemisNotices notice = service.getTools().findCidemisNotice(ppn);
                if (notice.getIssn() != null && notice.getIssn().contains(issn))
                    erreursNoticeZone.add("ISSN " + issn + " existe déjà pour la demande " + demande.getIdDemande());
                else {
                    this.addZone(demande, "011", "$a", issn);
                    demande.setIssn(issn);
                }

                this.deleteZone(demande, "301", "$a", idCidemis);
            }
            else if (demande.getTypesDemandes().getIdTypeDemande().equals(Constant.TYPE_DEMANDE_CORRECTION)){
                this.deleteZone(demande, "830", "$a", idCidemis);
            }
        }
        Commentaires commentaire = new Commentaires();
        commentaire.setCbsUsers(this.connexion.getUser());
        commentaire.setDemande(demande);
        commentaire.setDateCommentaire(dateNow);
        commentaire.setVisibleISSN(true);
        // //////////////////////////////////////////////////////////////////////////////////////////////
        // VERIFICATIONS DES ERREURS ////////////////////////////////////////////////////////////////////
        if (erreursNoticeZone.isEmpty()) {
            if (!commentairetxt.isEmpty()) {
                commentaire.setLibCommentaire(commentairetxt);
            }
        }
        else {
            StringBuilder commentaireErreur = new StringBuilder("Un problème est survenu lors de la modification de la notice, voir l'erreur renvoyée par le système ci-dessous:\r\n");
            
            for (String erreur:erreursNoticeZone) {
                commentaireErreur.append(erreur + "\r\n");
            }
            commentaireErreur.append("\r\n");
            commentaireErreur.append("Commentaire du centre international:\r\n");
            commentaireErreur.append(commentairetxt);
            commentaire.setLibCommentaire(commentaireErreur.toString());
        }
        demande.getCommentairesList().add(commentaire);
        // //////////////////////////////////////////////////////////////////////////////////////////////
        // ENREGISTREMENTS CHANGEMENTS DANS LE JOURNAL //////////////////////////////////////////////////
        JournalDemandes journal = new JournalDemandes();
        // Si il y a au moins une entrée dans le journal
        if (!demande.getJournalDemandesList().isEmpty()) {
            // Si le nouvel état de la demande est différent de l'état de la dernière entrée dans le journal alors on rajoute une entrée au journal
            if (!demande.getLastjournal().getEtatsdemandes().getIdEtatDemande().equals(demande.getEtatsDemandes().getIdEtatDemande())) {
                journal = createNewJournal(demande, dateNow);
            }
        }
        else {
            journal = createNewJournal(demande, dateNow);
        }
        demande.getJournalDemandesList().add(journal);
        
        // //////////////////////////////////////////////////////////////////////////////////////////////
        // FIN //////////////////////////////////////////////////////////////////////////////////////////
        service.getDemande().save(demande);
        this.addMessage(demande, "OK.");
    }

    private JournalDemandes createNewJournal(Demandes demande, Date dateNow) {
        JournalDemandes journal = new JournalDemandes(this.dao.getJournalDemandesDao().getNextSeriesId());
        journal.setDemandes(demande);
        journal.setCbsUsers(this.connexion.getUser());
        journal.setEtatsdemandes(demande.getEtatsDemandes());
        journal.setDateEntree(dateNow);
        return journal;
    }

    /**
     * Retourne la valeur de la cellule
     * @param cell
     * @return the value of the cell
     */
    public String getCellValue(HSSFCell cell) {
        if (cell.getCellType() == CellType.NUMERIC) {
            NumberFormat nf = new DecimalFormat("#.####");
            return nf.format(cell.getNumericCellValue());
        }
        else {
            return cell.getRichStringCellValue().getString().trim();
        }
    }
    
    /**
     * Retourne vrai si la ligne en question est une ligne d'entête
     * @param demandeNum
     * @return true if the line is header line
     */
    public boolean isHeaderRow(String demandeNum) {
        return "New assignments requests".equals(demandeNum)  || "Cidemis Number".equals(demandeNum)  || demandeNum.contains("Outstanding requests");
    }
    
    /**
     * Ajoute un message dans la ligne des logs
     * @param demande
     * @param message
     */
    public void addMessage(Demandes demande, String message) {
        this.results.add(UploadCiepsProcess.LINE_NUMBER + this.nbLine + ". Request n°" + (demande == null ? "ERROR" : demande.getIdDemande()) + ". " + message);
    }

    /**
     * Cherche une zone dans une notice et la supprime puis ajoute une zone
     * @param demande
     * @param zone
     * @param subZone
     * @param valueToDelete
     * @param valueToAdd
     * @return vrai si la suppression et l'addition ont réussies
     */
    public void deleteAndAddZone(Demandes demande, String zone, String subZone, String valueToDelete, String valueToAdd) throws ZoneException, CBSException {
        this.deleteZone(demande, zone, subZone, valueToDelete);
        this.addZone(demande, zone, subZone, valueToAdd);
    }
    
    /**
     * Cherche une zone dans une notice et la supprime
     * @param demande
     * @param zone
     * @param subZone
     * @param value
     * @returnvrai si la suppression a réussie
     */
    public void deleteZone(Demandes demande, String zone, String subZone, String value) throws ZoneException, CBSException {
        this.noticehelper.chercherEtSupprimerZoneNotice(demande.getNotice().getPpn(), zone, subZone, value);
    }

    /**
     * Ajoute une zone
     * @param demande
     * @param zone
     * @param subZone
     * @param value
     * @returnsi l'addition a réussie
     */
    public void addZone(Demandes demande, String zone, String subZone, String value) throws ZoneException, CBSException {
        this.noticehelper.ajoutZoneNotice(demande.getNotice().getPpn(), zone, subZone, value);
    }
}
