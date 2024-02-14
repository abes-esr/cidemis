package fr.abes.cidemis.mail;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class MailerTest {

    private String getPatternWithoutCcAndCci(){
        return
                        "{\n" +
                        " \"app\": \"cidemis\",\n" +
                        " \"to\": [\"tcn@abes.fr\", \"chambon@abes.fr\"],\n" +
                        " \"cc\": [],\n" +
                        " \"cci\": [],\n" +
                        " \"subject\": \"Objet du mail\",\n" +
                        " \"text\": \"Exemple de contenu, avec <b>HTML</b>.\"\n" +
                        "}";
    }

    private String[] fillsubjectAndReceiverAndMessage(){
        return new String[]{
                "Objet du mail",
                "tcn@abes.fr;chambon@abes.fr",
                "Exemple de contenu, avec <b>HTML</b>."
        };
    }

    /*
    L'ordre dans le tableau doit être impérativement le suivant :
    subject (sujet du mail)
    to (destinataires du mail)
    text (corps du mail)
    cc (destinataires en copie du mail)
    cci (destinataires en copie cachés du mail)
     */

    @Test
    public void constructJson() throws JsonProcessingException {
        /*Classe à tester*/
        Mailer mailerObjectTested = new Mailer();
        /*Tableau de string à passer en paramètre pour obtenir le json*/
        String[] subjectAndReceiverAndMessage = this.fillsubjectAndReceiverAndMessage();

        /*Construction du json*/
        mailerObjectTested.constructJson(subjectAndReceiverAndMessage);
        log.info("{\n" + mailerObjectTested.getJsonMessageFormatted());
        assertEquals(mailerObjectTested.getJsonMessageFormatted(), this.getPatternWithoutCcAndCci());
    }
}