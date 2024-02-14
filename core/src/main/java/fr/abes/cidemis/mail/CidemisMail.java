package fr.abes.cidemis.mail;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.service.CidemisManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class CidemisMail {
    private final String SUBJECT_TEST = " - Environnement de Test";
    @Autowired
    CidemisManageService service;
    //l'url du ws d'envoi des mails
    @Value("${mail.ws.url}")
    protected String url;

    //les url des testeurs
    @Value("${mail.ws.testeurs}")
    protected String testerMailAdresses;

    @Value("${mail.ws.cieps}")
    protected String ciepsMailAdress;

    @Value("${CIDEMIS_URL}")
    private String urlDiffusion;

    private final Mailer mailer;

    String[] subjectAndReceiverAndMessage = new String[5];

    public CidemisMail(){
        this.mailer = new Mailer();
    }


    /**
     * Envoi d'un mail pour le CIEPS pour 1 demande de numérotation ou correction
     *
     * @param demande entité demande
     * @return appel de la méthode sendMail
     */
    public void sendMailCIEPS(Demandes demande) throws RestClientException, JsonProcessingException {
        /*Indices : 0 -> Sujet du mail | 1 -> Destinataire du mail | 2 -> Corps du mail*/
        subjectAndReceiverAndMessage[0] = CidemisTemplatesHtml.getCidemisMailCiepsSubject(demande);
        if (Constant.getSkipMailFlag()) {
            subjectAndReceiverAndMessage[0] += SUBJECT_TEST;
            subjectAndReceiverAndMessage[1] = testerMailAdresses;
            log.info("mail : " + testerMailAdresses);
        } else {
            subjectAndReceiverAndMessage[1] = CidemisTemplatesHtml.getCidemisMailCiepsReceiver(demande);
            subjectAndReceiverAndMessage[3] = testerMailAdresses + ";" + ciepsMailAdress;
        }
        subjectAndReceiverAndMessage[2] = this.ciepsMailConstructionHtmlSubjectTemplate(demande);

        mailer.constructJson(subjectAndReceiverAndMessage);
        mailer.sendMail(url);
        this.flushArray();
    }

    /**
     * @param demande demande cieps
     * @return le template html complet qui devient le sujet du mail (le corps du mail)
     */
    private String ciepsMailConstructionHtmlSubjectTemplate(Demandes demande){
        StringBuilder message = new StringBuilder();
        if (demande.getTypesDemandes().getIdTypeDemande().equals(Constant.TYPE_DEMANDE_NUMEROTATION)) {
            message.append(CidemisTemplatesHtml.getCidemisMailCiepsDemandeNumerotationTextBegin(demande));
        } else {
            message.append(CidemisTemplatesHtml.getCidemisMailCiepsDemandeCorrectionTextBegin(demande));
        }
        message.append(CidemisTemplatesHtml.getCidemisMailCiepsDemandeTextAttachements(demande, urlDiffusion));
        message.append(CidemisTemplatesHtml.getCidemisMailCiepsDemandeTextComments(demande));
        if (demande.getTypesDemandes().getIdTypeDemande().equals(Constant.TYPE_DEMANDE_NUMEROTATION)) {
            message.append(CidemisTemplatesHtml.getCidemisMailCiepsDemandeNumerotationTextEnd());
        } else {
            message.append(CidemisTemplatesHtml.getCidemisMailCiepsDemandeCorrectionTextEnd());
        }
        message.append(CidemisTemplatesHtml.getCidemisMailCiepsDemandeNoticeContentTagFormat(demande));

        return message.toString().replaceAll("\"", "'");
    }

    /**
     * Préparation d'un mail pour le CIEPS pour 1 demande de numérotation ou correction
     *
     * @param demande entité demande
     * @return appel de la méthode sendMail
     */
    public String[] prepareMailCIEPS(Demandes demande) {
        subjectAndReceiverAndMessage[0] = CidemisTemplatesHtml.getCidemisMailCiepsSubject(demande);
        subjectAndReceiverAndMessage[1] = CidemisTemplatesHtml.getCidemisMailCiepsReceiver(demande);
        subjectAndReceiverAndMessage[2] = this.ciepsMailConstructionHtmlSubjectTemplate(demande);

        return subjectAndReceiverAndMessage;
    }

    /**
     * Mails changements de statut
     * Skipmailflag permet d'envoyer un mail différent à un destinataire ABES si env de dev/test
     */
    public void sendMailWhenStatusChange(String[] subjectAndReceiverAndMessage) throws RestClientException, JsonProcessingException {
        if (Constant.getSkipMailFlag()) {
            this.subjectAndReceiverAndMessage[0] = subjectAndReceiverAndMessage[0] + SUBJECT_TEST;
            this.subjectAndReceiverAndMessage[1] = testerMailAdresses;
            this.subjectAndReceiverAndMessage[2] = subjectAndReceiverAndMessage[2];
            log.warn("mail : " + testerMailAdresses);
        } else {
            this.subjectAndReceiverAndMessage[0] = subjectAndReceiverAndMessage[0];
            this.subjectAndReceiverAndMessage[1] = subjectAndReceiverAndMessage[1];
            this.subjectAndReceiverAndMessage[2] = subjectAndReceiverAndMessage[2];
        }

        mailer.constructJson(this.subjectAndReceiverAndMessage);
        mailer.sendMail(url);
        this.flushArray();
    }

    public void sendMailWithAttachments(String[] subjectAndReceiverAndMessage, List<File> files) {
        if (Constant.getSkipMailFlag()) {
            this.subjectAndReceiverAndMessage[0] = subjectAndReceiverAndMessage[0] + SUBJECT_TEST;
            this.subjectAndReceiverAndMessage[1] = testerMailAdresses;
        } else {
            this.subjectAndReceiverAndMessage[0] = subjectAndReceiverAndMessage[0];
            this.subjectAndReceiverAndMessage[1] = subjectAndReceiverAndMessage[1];
            this.subjectAndReceiverAndMessage[3] = testerMailAdresses + ";" + ciepsMailAdress;
        }
        this.subjectAndReceiverAndMessage[2] = subjectAndReceiverAndMessage[2];

        mailer.setListOfFiles(files);
        mailer.constructJson(this.subjectAndReceiverAndMessage);
        mailer.sendMailWithAttachments(url);
        this.flushArray();
    }


    public boolean creerDossier(File folder) {
        if (!folder.exists()) {
            try {
                if (!folder.mkdir())
                    throw new IOException("Impossible de créer le répertoire " + folder);
                else
                    return true;

            } catch (IOException ex) {
                log.error("Exception in Cidemis Mail ", ex);
                return false;
            }
        }
        return false;
    }

    /**
     * Vide le tableau destinataire sujet texte après chaque envoi de mail (sécurité)
     */
    private void flushArray(){
        for(int i = 0; i < this.subjectAndReceiverAndMessage.length - 1; i++){
            this.subjectAndReceiverAndMessage[i] = null;
        }
    }
}
