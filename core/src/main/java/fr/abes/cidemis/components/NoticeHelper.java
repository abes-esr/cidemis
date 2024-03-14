package fr.abes.cidemis.components;

import fr.abes.cbs.exception.CBSException;
import fr.abes.cbs.exception.ZoneException;
import fr.abes.cbs.notices.NoticeConcrete;
import fr.abes.cbs.process.ProcessCBS;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoticeHelper {

    private final ProcessCBS cbs;

    private NoticeConcrete noticeConcrete;

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
    private NoticeConcrete getNotice(String ppn) throws CBSException {
        try {
            cbs.search("che ppn " + ppn);
            return cbs.editerNoticeConcrete("1");
        } catch (CBSException | ZoneException ex) {
            log.error("Erreur de récupération de la notice" + ex);
            throw new CBSException("V/VERROR", "Erreur lors de la récupération de la notice : " + ppn);
        }
    }


    /**
     * Enregistre la notice
     *
     */
    private void enregistrerModif() throws CBSException {
        cbs.modifierNoticeConcrete("1", noticeConcrete);
    }

    /**
     * Modifie une notice
     *
     * @param ppn ppn de la notice à modifier
     * @param zone zone à ajouter à la notice
     * @param souszone sous zone à ajouter à la zone
     * @param valeur valeur de la sous zone
     */
    public void modifierZoneNotice(String ppn, String zone, String souszone, String valeur) throws CBSException {
        noticeConcrete = getNotice(ppn);
        noticeConcrete.getNoticeBiblio().replaceSousZone(zone, souszone, valeur.replaceAll("[$]{1}", "\\$\\$"));
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
    public void chercherEtSupprimerZoneNotice(String ppn, String zone, String souszone, String valeur) throws CBSException, ZoneException {
        noticeConcrete = getNotice(ppn);
        noticeConcrete.getNoticeBiblio().deleteZoneWithValue(zone, souszone, valeur);
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
    public void ajoutZoneNotice(String ppn, String zone, String souszone, String valeur) throws CBSException, ZoneException {
        noticeConcrete = getNotice(ppn);
        noticeConcrete.getNoticeBiblio().addZone(zone, souszone, valeur.replaceAll("[$]{1}", "\\$\\$"));
        enregistrerModif();
    }

    /**
     * On tente de supprimer une zone qui n'existe pas ... Afin de faire une fausse modif
     *
     * @param ppn ppn de la notice à vérifier
     */
    public void canModifyNotice(String ppn) {
        try {
            noticeConcrete = getNotice(ppn);
            noticeConcrete.getNoticeBiblio().deleteSousZone("100000000", "$a");
            enregistrerModif();
        } catch (CBSException ex) {
            log.error("Erreur dans suppression zone dans notice : " + ex);
        }
    }
}
