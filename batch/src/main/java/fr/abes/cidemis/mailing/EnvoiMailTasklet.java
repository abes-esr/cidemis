package fr.abes.cidemis.mailing;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.mail.CidemisMail;
import fr.abes.cidemis.mail.CidemisTemplatesHtml;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@JobScope
public class EnvoiMailTasklet implements Tasklet, StepExecutionListener {
    @Autowired
    private CidemisMail mail;
    private Map<String, List<DemandesDto>> demandes;

    public EnvoiMailTasklet() {
        this.demandes = new HashMap<>();
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
        this.demandes = (Map<String, List<DemandesDto>>) executionContext.get("demandes"); //Récupéré de la step précédente
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return stepExecution.getExitStatus();
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws JsonProcessingException {
        log.info(Constant.ENTER_EXECUTE_FROM_ENVOIMAILTASKLET);
        for (Map.Entry<String, List<DemandesDto>> entry : demandes.entrySet()) {
            log.info("Send mail to " + entry.getKey() + " with " + entry.getValue().size() + " elements");
            constructFile(entry.getKey(), entry.getValue());
        }
        return RepeatStatus.FINISHED;
    }

    private void constructFile(String email, List<DemandesDto> demandes) throws JsonProcessingException {
        // PREPARATION
        List<File> fichiers = new ArrayList<>();
        List<DemandesDto> demandesNew = new ArrayList<>();
        List<DemandesDto> demandesOld = new ArrayList<>();

        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormatMinuteSec = new SimpleDateFormat("yyyy-MM-dd-mm-ss");
        String date = dateformat.format(new Date());
        String dateFichier = dateFormatMinuteSec.format(new Date());

        mail.creerDossier(new File(Constant.getPathMailMensuel() + email.trim()));
        mail.creerDossier(new File(Constant.getPathMailMensuel() + email.trim() + File.separator + dateFichier));

        String pathFichierNum = Constant.getPathMailMensuel() + email.trim() + File.separator
                + dateFichier + File.separator + "NUM_" + date + ".xls";
        String pathFichierCor = Constant.getPathMailMensuel() + email.trim() + File.separator
                + dateFichier + File.separator + "COR_" + date + ".xls";

        // TRIE DES DEMANDES ENTRE LES NOUVELLES ET LES ANCIENNES
        for (DemandesDto demande : demandes) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, -1);
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);

            if (demande.getDateDemande().after(c.getTime()))
                demandesNew.add(demande);
            else
                demandesOld.add(demande);
        }

        // CREATION DES TABLEAUX
        String[] emptyLine = new String[]{"", "", "", "", "", "", "", "", "", "", "", "", ""};
        String[] header = new String[]{"Cidemis Number", "Request type",
                "Field to be added or to be modified (if this is a correction request)", "Country code",
                "ISSN National Centre number", "Type of publication", "Title of the resource", "ISSN",
                "Comments (here you add a new comment to the request)", "Past comments (list of all previous comments)",
                "PPN (ABES identifier)", "Attachments to the request",
                "Links to the references of the record (ISO 2709 Unimarc, marc 21 ISO 2709, ...)"};
        List<String[]> demandesNumList = new ArrayList<>();
        List<String[]> demandesCorList = new ArrayList<>();

        demandesNumList
                .add(new String[]{"New assignments requests", "", "", "", "", "", "", "", "", "", "", "", ""});
        demandesNumList.add(header);

        demandesCorList
                .add(new String[]{"New corrections requests", "", "", "", "", "", "", "", "", "", "", "", ""});
        demandesCorList.add(header);

        for (DemandesDto demande : demandesNew)
            if (demande.getTypesDemandes().equals(Constant.TYPE_DEMANDE_NUMEROTATION))
                demandesNumList.add(this.getDemandeStringify(demande));
            else
                demandesCorList.add(this.getDemandeStringify(demande));

        demandesNumList.add(emptyLine);
        demandesNumList.add(emptyLine);
        demandesNumList
                .add(new String[]{"Outstanding requests", "", "", "", "", "", "", "", "", "", "", "", ""});
        demandesNumList.add(header);

        demandesCorList.add(emptyLine);
        demandesCorList.add(emptyLine);
        demandesCorList
                .add(new String[]{"Old corrections requests", "", "", "", "", "", "", "", "", "", "", "", ""});
        demandesCorList.add(header);

        for (DemandesDto demande : demandesOld)
            if (demande.getTypesDemandes().equals(Constant.TYPE_DEMANDE_NUMEROTATION))
                demandesNumList.add(this.getDemandeStringify(demande));
            else
                demandesCorList.add(this.getDemandeStringify(demande));

        // CREATION DES FICHIERS
        createFile(fichiers, pathFichierNum, pathFichierCor, demandesNumList, demandesCorList);

        // ENVOI DU MAIL
        sendMail(email, fichiers);
    }

    /**
     * Retourne une demande sous forme de chaîne de caractères
     *
     * @param demande une entité demande (objet demande)
     * @return une demande avec ses attributs sous forme de tableau de chaîne de caractères
     */
    private String[] getDemandeStringify(DemandesDto demande) {
        return new String[]{
                demande.getIdDemande().toString(),
                demande.getTypesDemandes().equals(Constant.TYPE_DEMANDE_NUMEROTATION) ? "NUM" : "COR",
                demande.getZones(),
                demande.getPays(),
                demande.getCentreIssn(),
                demande.getTypeRessourceContinue(),
                demande.getTitre(),
                demande.getIssn(),
                "",
                demande.getCommentaires(),
                demande.getPpn(),
                demande.getPiecesJustificatives(),
                demande.getLiensNotice()
        };
    }

    private void createFile(List<File> fichiers, String pathFichierNum, String pathFichierCor,
                            List<String[]> demandesNumList, List<String[]> demandesCorList) {
        try {
            // traitement des demandes de num
            createSheet(demandesNumList, pathFichierNum);

            // Traitement des demandes de cor
            createSheet(demandesCorList, pathFichierCor);

            // On ajouter les fichiers pour l'envoi du mail
            fichiers.add(new File(pathFichierNum));
            fichiers.add(new File(pathFichierCor));
        } catch (IOException ex) {
            log.error(null, ex);
        }
    }

    private void createSheet(List<String[]> demandesList, String pathFichier) throws IOException {
        // Les demandes de numérotations
        HSSFRow row;
        HSSFCell cell;
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("request");
        FileOutputStream fileOut;
        int i = 0;
        int j;

        for (String[] values : demandesList) {
            row = sheet.createRow(i);
            j = 0;

            for (String value : values) {
                cell = row.createCell((short) j);
                cell.setCellValue(new HSSFRichTextString(value));
                j++;
            }

            i++;
        }
        try {
            fileOut = new FileOutputStream(pathFichier);
            wb.write(fileOut);
            fileOut.close();
        } catch (FileNotFoundException ex) {
            log.error("Erreur lors de l'ouverture du fichier " + pathFichier + " : " + ex.getMessage());
        }

    }

    private void sendMail(String email, List<File> fichiers) throws JsonProcessingException {
        String[] subjectAndReceiverAndMessage = new String[3];
        subjectAndReceiverAndMessage[0] = "Cidemis - Monthly Excel Sheet";
        subjectAndReceiverAndMessage[1] = email;
        subjectAndReceiverAndMessage[2] = CidemisTemplatesHtml.getCidemisMonthlySheetTextMail();

        mail.sendMailWithAttachments(subjectAndReceiverAndMessage, fichiers);
    }
}
