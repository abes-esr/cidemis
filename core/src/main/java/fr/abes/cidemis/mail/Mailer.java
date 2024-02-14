package fr.abes.cidemis.mail;

import fr.abes.cidemis.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

@Slf4j
public class Mailer {
    //La chaîne formattée contenant expediteur destinataire sujet et message
    private String jsonMessageFormatted;

    //Les fichiers à envoyer en pièce jointe
    private List<File> files;

    /**
     * Construction de la chaîne json à partir
     * des différents éléments composant le message
     * expediteur, destinataire, sujet, corps...
     */
    public void constructJson(String[] subjectAndReceiverAndMessage) {
        String subjectLine = "";
        String receiverLine = "";
        String messageLine = "";
        String ccLine = "";
        String cciLine = "";
        String appLine = "";
        int elementsOfArrayNotNull = 0;
        for(int i = 0; i < subjectAndReceiverAndMessage.length; i++){
            if(subjectAndReceiverAndMessage[i] != null){
                elementsOfArrayNotNull += 1;
            }
        }

        //Si il n'y a pas plus de trois elements dans le tableau, on conditionne pour eviter une null pointer exception
        for(int i = 0; i < elementsOfArrayNotNull ; i++){
            switch (i){
                case 0 : subjectLine = this.constructJsonLineFromOnlyOneElement("subject", subjectAndReceiverAndMessage[0]);continue;
                case 1 : receiverLine = this.constructJsonLineFromMultipleElements("to", subjectAndReceiverAndMessage[1]);continue;
                case 2 : messageLine = this.constructJsonLineFromOnlyOneElement("text", subjectAndReceiverAndMessage[2]);continue;
                case 3 : ccLine = this.constructJsonLineFromMultipleElements("cc", subjectAndReceiverAndMessage[3]);continue;
                case 4 : cciLine = this.constructJsonLineFromMultipleElements("cc", subjectAndReceiverAndMessage[4]);break;
                default: return;
            }
        }

        //La ligne cc dans le json est obligatoire actuellement pour faire fonctionner le ws
        if(ccLine.equals("")){
            ccLine = "\t\"cc\": [],\n";
        }

        //La ligne cci dans le json est obligatoire actuellement pour faire fonctionner le ws
        if(cciLine.equals("")){
            cciLine = "\t\"cci\": [],\n";
        }

        //La ligne appLine dans le json est obligatoire actuellement pour faire fonctionner le ws
        appLine = "\t\"app\": \"cidemis\",\n";

        String finalJson = "{\n" +
                 appLine + receiverLine + ccLine + cciLine + subjectLine + messageLine +
                "}";
        char tab = 9;
        this.jsonMessageFormatted = finalJson.replace(",\n}", "\n}").replace(tab, ' ');
    }

    /**
     * @param mailField to | cc | .. -> le champ concerné
     * @param elementToSplit la chaîne de caractères à splitter
     * @return une ligne du json formattée
     */
    private String constructJsonLineFromMultipleElements(String mailField, String elementToSplit){
        String[] listOfElements = elementToSplit.split(";");
        StringBuilder toNormalizedString = new StringBuilder();
        toNormalizedString.append("\t\""+ mailField +"\": [");
        for(int i = 0; i < listOfElements.length-1; i++){
            toNormalizedString.append("\""+ listOfElements[i] +"\", ");
        }

        toNormalizedString.append("\"" + listOfElements[listOfElements.length-1] + "\"" + "],\n");
        return toNormalizedString.toString();
    }

    /**
     * @param mailField subject | text uniquement
     * @param element l'élement, template html ou autre à passer en paramètre
     * @return le chaîne de caractère correspondante
     */
    private String constructJsonLineFromOnlyOneElement(String mailField, String element){
        return "\t\"" + mailField + "\": \"" + element.replaceAll("\"", "'") + "\",\n";
    }

    /**
     * @return la chaîne json obtenue
     */
    public String getJsonMessageFormatted(){
        return this.jsonMessageFormatted;
    }

    /**
     * @param files liste de fichiers à ajouter en pièce jointe dans le mail d'envoi
     */
    public void setListOfFiles(List<File> files){
        this.files = files;
    }

    /**
     * @param file fichier à ajouter dans le mail d'envoi
     */
    public void setOneFileOnListOfFiles(File file){
        this.files.add(file);
    }

    /**
     * Envoi d'un mail simple sans pièce jointe
     * Le Json à envoyer à été préparé au préalable en membre de la classe
     */
    public void sendMail(String url) throws RestClientException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        HttpEntity<String> entity = new HttpEntity<>(this.jsonMessageFormatted, headers);
        restTemplate.postForObject(url + "htmlMail/", entity, String.class);
        this.jsonMessageFormatted = null;
        this.files = null;
    }

    /**
     * Envoi d'un mail avec pièces jointes
     * Le Json à envoyer à été préparé au préalable en membre de la classe
     * Les pièces jointes à envoyer ont été au préalable settées en membre de la classe
     */
    public void sendMailWithAttachments(String url) {
        HttpPost uploadFile = new HttpPost(url + "htmlMailAttachment/");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("mail", this.jsonMessageFormatted, ContentType.APPLICATION_JSON);
        /*Ajout des fichiers au mail d'envoi*/
        for(File f : this.files){
            try {
                builder.addBinaryBody(
                        "attachment",
                        new FileInputStream(f),
                        ContentType.APPLICATION_OCTET_STREAM,
                        f.getName()
                );
            } catch (FileNotFoundException e) {
                log.error(Constant.ERROR_ATTACHMENT_NOT_FOUND + e.toString());
            }
        }

        org.apache.http.HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);

        /*Envoi du mail*/
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            httpClient.execute(uploadFile);
        } catch (IOException e) {
            log.error(Constant.ERROR_ATTACHMENT_NOT_FOUND + e.toString());
        }

        /*Vide les membres de la classe pour le mail suivant*/
        this.jsonMessageFormatted = null;
        this.files = null;
    }
}
