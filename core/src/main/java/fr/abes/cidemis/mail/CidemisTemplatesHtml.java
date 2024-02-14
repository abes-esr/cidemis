package fr.abes.cidemis.mail;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.model.cidemis.Commentaires;
import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.model.cidemis.PiecesJustificatives;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Description("Classe contenant uniquement les templates html")
@Slf4j
public class CidemisTemplatesHtml {
    /**
     * Retourne le sujet du mail
     *
     * @param demande demande
     * @return texte
     */
    public static String getCidemisMailCiepsSubject(Demandes demande) {
        String sujet = (demande.getTypesDemandes().getIdTypeDemande().equals(Constant.TYPE_DEMANDE_NUMEROTATION) ? "Assignment Request" : "Correction request") +
                "/ABES/SUDOC " +
                demande.getCentreISSN() +
                "/" +
                Constant.getCodePaysSorted().get(demande.getNotice().getPaysCIEPS()).getIso31661a3() +
                " PPN " +
                demande.getNotice().getPpn() +
                " " +
                demande.getTitre();
        return sujet;
    }

    /**
     * Retoune le destinaitaire du mail
     *
     * @param demande
     * @return
     */
    public static String getCidemisMailCiepsReceiver(Demandes demande) {
        return Constant.getCodePaysSorted().get(demande.getNotice().getPaysCIEPS()).getEmail();
    }

    /**
     * Debut du corps du mail d'une demande de numérotation
     *
     * @param demande demande
     * @return texte
     */
    public static String getCidemisMailCiepsDemandeNumerotationTextBegin(Demandes demande) {
        return
                "Dear Madam/Sir,"
                        + "Please find below for your consideration an assignment request for the resource "
                        + demande.getTitre()
                        + "CIDEMIS reference: #"
                        + demande.getIdDemande()
                        + "<br>"
                        + "<br>"
                        + "<br>"
                        + "<center><b>PLEASE DO NOT RESPOND TO THIS EMAIL</b></center>"
                        + "<br>"
                        + "<br>";
    }

    /**
     * Debut du corps du mail d'une demande de correction
     *
     * @param demande demande
     * @return texte
     */
    public static String getCidemisMailCiepsDemandeCorrectionTextBegin(Demandes demande) {
        return
                "Dear Madam/Sir,"
                        + "<br>"
                        + "<br>"
                        + "Please find below for your consideration a correction request for the ISSN record "
                        + demande.getNotice().getIssn()
                        + " "
                        + demande.getTitre()
                        + "<br>"
                        + "CIDEMIS reference: #"
                        + demande.getIdDemande()
                        + "Field to be added or to be modified : "
                        + demande.getZones()
                        + "<br>"
                        + "<br>"
                        + "<br>"
                        + "<center><b>PLEASE DO NOT RESPOND TO THIS EMAIL</b></center>"
                        + "<br>"
                        + "<br>";
    }

    /**
     * Corps du mail d'une demande pour les pièces jointes
     *
     * @param demande demande
     * @return texte
     */
    public static String getCidemisMailCiepsDemandeTextAttachements(Demandes demande, String urlDiffusion) {
        StringBuilder msg = new StringBuilder();
        /*Ajout de cette zone au corps du mail si la demande contient des pièces justificatives*/
        if (!demande.getPiecesJustificativeslist().isEmpty()) {
            msg.append(
                    "Kindly click on the link(s) below to see the attachment(s) to the request:"
                            + "<br>"
                            + "<ul>"
            );
            for (PiecesJustificatives piece : demande.getPiecesJustificativeslist()) {
                msg.append(
                        "<li>"
                                + "<a href='" + urlDiffusion + piece.getUrlfichier() + "'>" + piece.getPublicname() + "</a>"
                                + "</li>");
            }
            msg.append(
                    "</ul>"
            );
        }
        return msg.toString();
    }

    /**
     * Corps du mail d'une demande pour les commentaires
     *
     * @param demande demande
     * @return text
     */
    public static String getCidemisMailCiepsDemandeTextComments(Demandes demande) {
        StringBuilder msg = new StringBuilder();
        /*Ajout de cette zone au corps du mail si la demande contient des commentaires*/
        if (!demande.getCommentairesList().isEmpty()) {
            msg.append(
                    "You will find below the existing comments concerning the request"
                            + "<br>"
            );
            for (Commentaires comment : demande.getCommentairesList()) {
                if (comment.getVisibleISSN())
                    msg.append(
                            "<p>" + comment.getDateCommentaireFormatee() + " by " + comment.getCbsUsers().getRoles().getLibRole() + " :"
                                    + "<br>"
                                    + "<i>"
                                    + comment.getLibCommentaireHTML()
                                    + "</i>"
                                    + "</p>");
            }
        }

        msg.append(
                "<br>"
                        + "<br>"
        );

        return msg.toString();
    }

    /**
     * Fin du Corps du mail pour une demande de numérotation
     *
     * @return texte
     */
    public static String getCidemisMailCiepsDemandeNumerotationTextEnd() {
        return
                "Please assign the ISSN and create the corresponding record. Please then kindly fill out the monthly Excel file by indicating"
                        + "<ul>"
                        + "<li>in the ISSN cell, the assigned ISSN</li>"
                        + "<li>in the 'comments” cell, IN CAPITAL LETTERS, the word 'COMPLETED”</li>"
                        + "<ul>"
                        + "<br>"
                        + "<br>"
                        + "In case of rejection, or if further information is needed, please indicate 'REJECTED” or 'INFORMATION NEEDED” in the ISSN cell. Please also explain your decision in the comments part in these two last cases."
                        + "<br>"
                        + "<br>"
                        + "<b>References of the record:</b>"
                        + "<br>";
    }

    /**
     * Fin du corps du mail pour une demande de correction
     *
     * @return texte
     */
    public static String getCidemisMailCiepsDemandeCorrectionTextEnd() {
        return
                "Please correct the record and fill out the monthly Excel file by indicating in the 'comments” cell, "
                        + "IN CAPITAL LETTERS, the word(s): 'COMPLETED”.";
    }

    /**
     * Ajout du contenu de la notice format étiquette
     *
     * @param demande la demande
     * @return texte
     */
    public static String getCidemisMailCiepsDemandeNoticeContentTagFormat(Demandes demande) {
        return
                "<br>"
                        + "<br>"
                        + "In case of rejection, or if further information is needed, please indicate 'REJECTED” or 'INFORMATION NEEDED” in the ISSN cell. Please also explain your decision in the comments part in these two last cases."
                        + "<br>"
                        + "<br>"
                        + "<b>References of the record:</b>"
                        + "<br>"
                        + getHTML(Constant.PROPERTIES_PSI_LIEN_PERENNE_ISSN + "?format=label&ppn=" + demande.getNotice().getPpn())
                        + "<br>"
                        + "<br>"
                        + "You will find below the links enabling you to use the record in MARC21 or UNIMARC formats (machine readable exchange formats) and to import the record in your local cataloguing system (if allowed by your system):"
                        + "<br>"
                        + "<br>"
                        + "<a href='" + Constant.PROPERTIES_PSI_LIEN_PERENNE_ISSN + "?format=marc_iso&ppn=" + demande.getNotice().getPpn() + "'>ISO 2709 Unimarc</a><br/>"
                        + "<a href='" + Constant.PROPERTIES_PSI_LIEN_PERENNE_ISSN + "?format=marc21_iso&ppn=" + demande.getNotice().getPpn() + "'>marc 21 ISO 2709</a><br/>"
                        + "You will also find below the record also available in human readable UNIMARC format:"
                        + "<br>"
                        + "<a href='" + Constant.PROPERTIES_PSI_LIEN_PERENNE_ISSN + "?format=marc_read&ppn=" + demande.getNotice().getPpn() + "'>readable Unimarc</a><br/>"
                        + "<br>"
                        + "<br>"
                        + "We thank you in advance for your contribution to the improvement of the ISSN register."
                        + "<br>"
                        + "Best regards,"
                        + "<br>"
                        + "<br>"
                        + "ABES/ISSN Coordination Team"
                        + "<br>"
                        + "<br>";
    }

    private static String getHTML(String urlToRead) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        StringBuilder result = new StringBuilder();

        try {
            System.setProperty("http.keepAlive", "false");
            url = new URL(urlToRead);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            rd.close();
        } catch (Exception e) {
            log.error("Exception in Cidemis Mail get HTML", e);
        }
        return result.toString();
    }

    /**
     * Accesseur du texte du mail pour le fichier excel mensuel
     *
     * @return le texte du corps du mail
     */
    public static String getCidemisMonthlySheetTextMail() {
        return
                "Dear Madam/Sir,"
                        + "<br>"
                        + "<br>"
                        + "Please find enclosed for your consideration the list of assignment and correction requests bound to your ISSN center."
                        + "<br>"
                        + "<br>"
                        + "This Excel sheet should be regularly filed and sent to the ISSN International Centre at the end of each month. Please use the address: bibquestions@issn.org."
                        + "<br>"
                        + "The ISSN International Centre will then load it into CIDEMIS to inform the ABES network."
                        + "<br>"
                        + "<br>"
                        + "<b>For an assignment request:</b>"
                        + "<br>"
                        + "<br>"
                        + "<b>In case of successful assignment</b>, the national ISSN centre should indicate for each record:"
                        + "<br>"
                        + "<ul>"
                        + "<li>in the ISSN cell, the assigned ISSN"
                        + "<li>in the 'comments' cell, IN CAPITAL LETTERS, the word 'COMPLETED'</li>"
                        + "</ul>"
                        + "<br>"
                        + "<b>In case of rejection</b>, or if further information is needed, the national ISSN centre should indicate for each record:"
                        + "<br>"
                        + "<ul>"
                        + "<li>in the ISSN cell, IN CAPITAL LETTERS, the word(s) 'REJECTED' or 'INFORMATION NEEDED'</li>"
                        + "<li>in the 'comments' cell, an explanation of the decision.</li>"
                        + "</ul>"
                        + "<b>For a correction request:</b>"
                        + "<br>"
                        + "<br>"
                        + "<b>In case of successful correction</b>, the national ISSN centre should indicate for each record:"
                        + "<ul>"
                        + "<li>in the 'comments' cell, IN CAPITAL LETTERS, the word 'COMPLETED'</li>"
                        + "</ul>"
                        + "<b>In case of rejection</b>, or if further information is needed, the national ISSN centre should indicate for each record:"
                        + "<ul>"
                        + "<li>in the ISSN cell, IN CAPITAL LETTERS, the word(s) 'REJECTED' or 'INFORMATION NEEDED'</li>"
                        + "<li>in the 'comments' cell, an explanation of the decision.</li>"
                        + "</ul>"
                        + "<br>"
                        + "Best Regards,"
                        + "<br>"
                        + "<br>"
                        + "ABES/ISSN Coordination Team";
    }

    public static String getCidemisMailChangementStatut(Demandes demande, String cidemisUrl) {
        return
                "Bonjour,"
                + "<br>"
                + "<br>"
                + "Nous vous informons du changement de statut de la demande Cidemis n°"
                + demande.getIdDemande() + "."
                + "<br>"
                + "<br>"
                + "Motif : " + demande.getEtatsDemandes().getLibelleEtatDemande()
                + "<br>"
                + "<br>"
                + "Vous pouvez consulter la demande directement au lien suivant : "
                + "<a href='" + cidemisUrl + "afficher-demande?id=" + demande.getIdDemande() + "' target='_blank'>Lien vers la demande</a>"
                + "<br>"
                + "<br>"
                + "Nous vous invitons à prendre connaissance des informations jointes à cette demande et, éventuellement, à apporter dans les meilleurs délais les précisions demandées.";
    }
}
