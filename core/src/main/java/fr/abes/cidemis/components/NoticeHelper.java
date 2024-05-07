package fr.abes.cidemis.components;

import fr.abes.cbs.exception.CBSException;
import fr.abes.cbs.exception.ZoneException;
import fr.abes.cbs.notices.Biblio;
import fr.abes.cbs.process.ProcessCBS;
import fr.abes.cbs.utilitaire.Constants;
import fr.abes.cbs.utilitaire.Utilitaire;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Level;

import java.io.IOException;

@Slf4j
public class NoticeHelper {

    private final ProcessCBS cbs;

    private Biblio noticeBiblio;

    /**
     * Prépare la connexion au CBS
     *
     * @param cbs : objet correspondant à la connexion au cbs
     */
    public NoticeHelper(ProcessCBS cbs) {
        this.cbs = cbs;
    }

    /**
     * Retourne une notice
     *
     * @param ppn ppn de la notice à chercher
     * @return notice trouvée
     */
    public Biblio getNotice(String ppn) throws CBSException, IOException {
        try {
            cbs.search("che ppn " + ppn);
            cbs.affUnma();
            //on retourne une notice biblio au lieu d'une notice concrete car la notice peut contenir des exemplaires au format Sudoc PS non gérés par l'API
            String result = cbs.editer("1");
            return new Biblio(Utilitaire.recupEntre(result, Constants.STR_1F, Constants.STR_1E));
        } catch (CBSException | ZoneException ex) {
            log.error("Erreur de récupération de la notice" + ex);
            throw new CBSException(Level.ERROR, "Erreur lors de la récupération de la notice : " + ppn + " : " + ex.getMessage());
        }
    }


    /**
     * Enregistre la notice
     *
     */
    private void enregistrerModif() throws CBSException, IOException {
        cbs.modifierNotice("1", noticeBiblio.toString());
    }

    /**
     * Modifie une notice
     *
     * @param ppn ppn de la notice à modifier
     * @param zone zone à ajouter à la notice
     * @param souszone sous zone à ajouter à la zone
     * @param valeur valeur de la sous zone
     */
    public void modifierZoneNotice(String ppn, String zone, String souszone, String valeur) throws CBSException, IOException {
        noticeBiblio = getNotice(ppn);
        noticeBiblio.replaceSousZone(zone, souszone, valeur.replaceAll("[$]{1}", "\\$\\$"));
        enregistrerModif();
    }

    /**
     * Supprimer une zone d'une notice
     *
     * @param ppn ppn de la notice à modifier
     * @param zone zone à supprimer
     * @param souszone sous zone à supprimer
     * @param valeur valeur devant être trouvé dans la sous zone pour la supprimer
     */
    public void chercherEtSupprimerZoneNotice(String ppn, String zone, String souszone, String valeur) throws CBSException, ZoneException, IOException {
        noticeBiblio = getNotice(ppn);
        noticeBiblio.deleteZoneWithValue(zone, souszone, valeur);
        enregistrerModif();
    }

    /**
     * Ajoute une zone dans une notice
     *
     * @param ppn ppn de la notice à modifier
     * @param zone zone à ajouter
     * @param souszone sous zone à ajouter à la zone
     * @param valeur valeur à ajouter dans la sous zone
     */
    public void ajoutZoneNotice(String ppn, String zone, String souszone, String valeur) throws CBSException, ZoneException, IOException {
        noticeBiblio = getNotice(ppn);
        noticeBiblio.addZone(zone, souszone, valeur.replaceAll("[$]{1}", "\\$\\$"));
        enregistrerModif();
    }

    /**
     * On tente de supprimer une zone qui n'existe pas ... Afin de faire une fausse modif
     *
     * @param ppn ppn de la notice à vérifier
     */
    public void canModifyNotice(String ppn) throws IOException {
        try {
            noticeBiblio = getNotice(ppn);
            noticeBiblio.deleteSousZone("100000000", "$a");
            enregistrerModif();
        } catch (CBSException ex) {
            log.error("Erreur dans suppression zone dans notice : " + ex);
        }
    }
}
