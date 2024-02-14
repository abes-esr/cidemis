package fr.abes.cidemis.components;

import fr.abes.cbs.exception.CBSException;
import fr.abes.cbs.exception.ZoneException;
import fr.abes.cbs.notices.Biblio;
import fr.abes.cbs.process.ProcessCBS;
import fr.abes.cbs.utilitaire.Constants;
import fr.abes.cbs.utilitaire.Utilitaire;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoticeHelper {

    private ProcessCBS cbs;

    @Getter
    private String notice;

    private Biblio noticeBiblio;

    /**
     * Prépare la connexion au CBS
     *
     * @param cbs
     */
    public NoticeHelper(ProcessCBS cbs) {
        this.cbs = cbs;
    }

    /**
     * Retourne une notice
     *
     * @param ppn
     * @return
     */
    private String getNotice(String ppn) {
        try {
            cbs.search("che ppn " + ppn);
            return cbs.affUnma();
        } catch (CBSException ex) {
            log.error("Erreur de récupération de la notice" + ex);
            return "";
        }
    }

    /**
     * Vérifie que l'on peut modifier un PPN
     *
     * @param ppn
     * @return
     */
    private void preparerPPN(String ppn) throws CBSException {
        this.notice = "";
        this.getNotice(ppn);
        // Tentative du passage en édition
        notice = Constants.STR_1F + Utilitaire.recupEntre(cbs.editer("1"), Constants.STR_1F, Constants.STR_1E) + Constants.STR_1E;

    }

    /**
     * Enregistre la notice
     *
     * @return
     */
    private void enregistrerModif() throws CBSException {
        cbs.modifierNotice("1", notice);
    }

    /**
     * Modifie une notice
     *
     * @param ppn
     * @param zone
     * @param souszone
     * @param valeur
     * @return
     */
    public void modifierZoneNotice(String ppn, String zone, String souszone, String valeur) throws CBSException, ZoneException {
        preparerPPN(ppn);
        noticeBiblio = new Biblio(notice);
        noticeBiblio.replaceSousZone(zone, souszone, valeur.replaceAll("[$]{1}", "\\$\\$"));
        notice = noticeBiblio.toString().substring(1, noticeBiblio.toString().length() - 1);
        enregistrerModif();
    }

    /**
     * Supprimer une zone d'une notice
     *
     * @param ppn
     * @param zone
     * @param souszone
     * @param valeur
     * @return
     */
    public void chercherEtSupprimerZoneNotice(String ppn, String zone, String souszone, String valeur) throws CBSException, ZoneException {
        preparerPPN(ppn);
        noticeBiblio = new Biblio(notice);
        noticeBiblio.deleteZoneWithValue(zone, souszone, valeur);
        notice = noticeBiblio.toString().substring(1, noticeBiblio.toString().length() - 1);
        enregistrerModif();
    }

    /**
     * Ajoute une zone dans une notice
     *
     * @param ppn
     * @param zone
     * @param souszone
     * @param valeur
     * @return
     */
    public void ajoutZoneNotice(String ppn, String zone, String souszone, String valeur) throws CBSException, ZoneException {
        preparerPPN(ppn);
        noticeBiblio = new Biblio(notice);
        noticeBiblio.addZone(zone, souszone, valeur.replaceAll("[$]{1}", "\\$\\$"));
        notice = noticeBiblio.toString().substring(1, noticeBiblio.toString().length() - 1);
        enregistrerModif();
    }

    /**
     * On tente de supprimer une zone qui n'existe pas ... Afin de faire une fausse modif
     *
     * @param ppn
     * @return Si error ne contient pas de message d'erreur, alors la notice est modifiable
     */
    public Boolean canModifyNotice(String ppn) {
        try {
            preparerPPN(ppn);
            notice = Utilitaire.suppZoneBiblio(notice, "100000000", "$a");
            enregistrerModif();
            return true;
        } catch (CBSException ex) {
            log.error("Erreur dans suppression zone dans notice : " + ex);
            return false;
        }
    }

    /**
     * Retourne la date de dernière mise à jour d'une notice
     *
     * @param ppn
     * @return
     */
    public String getDateUpdate(String ppn) {
        log.info("PPN synchronized " + ppn);
        this.notice = this.getNotice(ppn);
        int index = this.notice.indexOf("Statut");

        if (index > 20)
            return this.notice.substring(index - 18, index - 1);
        else
            return "";
    }
}
