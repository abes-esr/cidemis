package fr.abes.cidemis.model.cidemis;

import fr.abes.cidemis.constant.Constant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "CIDEMIS_NOTICES", schema = "AUTORITES")
@NoArgsConstructor
@Getter @Setter
public class CidemisNotices implements Serializable {
    private static final long serialVersionUID = 303085650167540906L;
    @Id
    @Column(name = "PPN", nullable = false)
    private String ppn;
    @Column(name = "TITRE_A")
    private String titreA;
    @Column(name = "TITRE_E")
    private String titreE;
    @Column(name = "TITRE_C")
    private String titreC;
    @Column(name = "TITRE_H")
    private String titreH;
    @Column(name = "TITRE_I")
    private String titreI;
    @Transient
    private String titre;
    //530 $a
    @Column(name = "TITRE_CLE")
    private String titreCle;
    //530 $b
    @Column(name = "TITRE_ABREGE")
    private String titreCleAbrege;
    @Column(name = "PAYS")
    private String pays;
    @Column(name = "DATE_DEBUT")
    private String dateDebut;
    @Column(name = "DATE_FIN")
    private String dateFin;
    @Transient
    private String dateOuverture;
    @Column(name = "TYPE_DOCUMENT")
    private String typeDocument;
    @Column(name = "NOTICE_CENTRE_ISSN")
    private String centreISSN;
    @Column(name = "NOTICE_ISSN")
    private String issn;
    @Column(name = "ISSN035")
    private String issn035;
    @Column(name = "FRBNF")
    private String frbnf;
    @Column(name = "TYPE_RESSOURCE_CONTINUE")
    private String typeDeRessourceContinue;
    @Column(name = "SOURCE_CATALOGUAGE_ISSN")
    private String sourceDeCatalogageISSN;
    @Column(name = "NOTE_IDENTIFIANTS")
    private String noteIdentifiants; // 301$a
    @Column(name = "NOTE_GENERALE_CATALOGUEUR")
    private String noteGeneraleCatalogueur; // 830$a
    @Column(name = "STATUT_NOTICE")
    private String statutNotice;

    @Transient
    @Value("${PSI_LIEN_PERENNE}")
    private String lienPerenne;

    public CidemisNotices(String ppn) {
        this.ppn = ppn;
    }

    public String getPays() {
        if (pays == null) {
            return "";
        }
        return pays.isEmpty() ? "XX" : pays;
    }

    public String getPaysTRUE() {
        return (pays == null) ? "" : pays;
    }

    public String getPaysCIEPS() {
        return "ZZ".equals(getPays()) ? "XX" : getPays();
    }

    public void setPays(String pays) {
        // Si plusieurs codes pays, on prend le premier ..
        if (pays != null) {
            String nPays = pays.split("0")[0];
            this.pays = nPays;
        } else {
            this.pays = "XX";
        }
    }

    public String getDatePublication() {
        if (!dateDebut.isEmpty()) {
            if (!getStatutdevie())
                return dateDebut + "-...";
            else
                return dateDebut + "-" + dateFin;
        } else {
            return "";
        }
    }

    public String getTypeRessource() {
        return Constant.getListeTypePublication().get(getTypeDeRessourceContinue());
    }

    public String getTypeRessourceShort() {
        return Constant.getListeTypePublicationShort().get(getTypeDeRessourceContinue());
    }


    public String getTypeDocumentLibelle() {
        return getTypeDocument().isEmpty() ? "" : Constant.getListeTypeDocument().get(getTypeDocument().substring(0, 1));
    }


    public String getLienPerenne() {
        return lienPerenne + ppn;
    }


    public String getPayslibelle() {
        switch (pays) {
            case "":
            case "XX":
                return "Pays inconnu";
            case "ZZ":
                return "Pays multiples";
            default:
                if (pays.trim().length() > 2)
                    return "Pays multiples";
                return (Constant.getListeLibellePaysCodePays().get(pays) == null) ? "Pays inconnu" : Constant.getCodePaysSorted().get(pays).getPays();
        }
    }

    public String getTitre() {
        return (getTitrecle() == null || getTitrecle().isEmpty()) ? titre : getTitrecle();
    }

    public String getTitrecle() {
        return (titreCleAbrege == null || titreCleAbrege.isEmpty()) ? titreCle : titreCle + " " + titreCleAbrege;
    }

    /**
     * Permet de savoir si la date de fin est renseignée dans la notice
     * @return true si date de fin renseignée false sinon
     */
    public Boolean getStatutdevie() {
        return (dateFin != null) ? !dateFin.isEmpty() : false;
    }


    public int getIdprofil() {
        String regEXP = "X|x|\\.";

        //périodique
        if ("acdefghijmnz".contains(typeDeRessourceContinue)) {
            //électronique
            if (typeDocument.startsWith("l"))
                return Constant.PROFIL_PERIODIQUE_ELECTRONIQUE;

            if (!dateDebut.isEmpty()) {
                //périodique imprimé
                //si date de début > 1960 et pas de date de fin
                if (!getStatutdevie() && (Integer.parseInt(dateDebut.replaceAll(regEXP, "0")) > 1960))
                    return Constant.PROFIL_PERIODIQUE_IMPRIME_VIVANT;

                //périodique imprimé
                //si (date de début entre 1830 et 1960) ou (date de début > 1960 et date de fin renseignée)
                if ((Integer.parseInt(dateDebut.replaceAll(regEXP, "0")) > 1830 || "183X".equals(dateDebut) || "18XX".equals(dateDebut))
                    || (Integer.parseInt(dateDebut.replaceAll(regEXP, "0")) > 1960 && getStatutdevie()))
                    return Constant.PROFIL_PERIODIQUE_IMPRIME_MORT;

                //périodique imprimé
                //si date de début < 1830
                if (Integer.parseInt(dateDebut.replaceAll(regEXP, "0")) <= 1830)
                    return Constant.PROFIL_PERIODIQUE_IMPRIME;
            }
        }
        //type de ressource continue collection
        if ("b".equals(typeDeRessourceContinue))
            return Constant.PROFIL_COLLECTION;

        //tout le reste
        return Constant.PROFIL_INDETERMINE;
    }

    /**
     * Initialise une notice supprimée du Sudoc (titre uniquement, autres champs vides)
     */
    public void setNoticeSupprimeeSudoc() {
        this.setTitre("NOTICE SUPPRIMÉE DU SUDOC");
        this.setTitreA("");
        this.setTitreC("");
        this.setTitreE("");
        this.setTitreH("");
        this.setTitreI("");
        this.setTitreCle("");
        this.setTitreCleAbrege("");
        this.setPays("");
        this.setDateDebut("");
        this.setDateFin("");
        this.setTypeDocument("");
        this.setCentreISSN("");
        this.setIssn("");
        this.setIssn035("");
        this.setFrbnf("");
        this.setTypeDeRessourceContinue("");
        this.setNoteGeneraleCatalogueur("");
        this.setNoteIdentifiants("");
        this.setSourceDeCatalogageISSN("");
    }


    /**
     * Vérifie que la notice est éligible à une demande dans Cidemis
     *
     * @param zonesManquantes
     * @param zonesPresentes
     * @return
     */
    public boolean checkZones(Integer idTypeDemande, List<String> zonesManquantes, List<String> zonesPresentes) {
        switch (idTypeDemande) {
            case Constant.TYPE_DEMANDE_CORRECTION:
                testZonesCorr(zonesManquantes);
                break;
            case Constant.TYPE_DEMANDE_NUMEROTATION:
                testZonesNum(zonesManquantes, zonesPresentes);
                break;
            default:
                break;
        }
        return zonesManquantes.isEmpty() && zonesPresentes.isEmpty();
    }

    private void testZonesCorr(List<String> zonesManquantes) {
        // Test des zones manquantes
        if (this.getIssn() == null || this.getIssn().isEmpty())
            zonesManquantes.add("011 : ISSN");
        if (this.getIssn035() == null || this.getIssn035().isEmpty())
            zonesManquantes.add("035 : Identifiant ISSN de la notice");
        if (this.getIssn035() != null && this.getIssn035().contains("/"))
            zonesManquantes.add("035 : Il ne peut y avoir qu'un seul identifiant de la notice dans un autre système (ISSN)");
        if (this.getPaysTRUE().isEmpty())
            zonesManquantes.add("102 : Pays de publication");
        if (this.getTypeDeRessourceContinue().isEmpty())
            zonesManquantes.add("110 : Type de ressource continue");
        if (this.getTitrecle() == null || this.getTitrecle().isEmpty())
            zonesManquantes.add("530 : Titre clé");
        if (this.getSourceDeCatalogageISSN() != null && !this.getSourceDeCatalogageISSN().contains("ISSN"))
            zonesManquantes.add("801 : Source de catalogage ISSN");
        if (this.getCentreISSN() == null || this.getCentreISSN().isEmpty())
            zonesManquantes.add("802 : Centre ISSN");
    }

    private void testZonesNum(List<String> zonesManquantes, List<String> zonesPresentes) {
        // Test des zones manquantes
        if (this.getPaysTRUE().isEmpty())
            zonesManquantes.add("102 : Pays de publication");
        if (this.getTypeDeRessourceContinue().isEmpty())
            zonesManquantes.add("110$a : Type de ressource continue");
        if ("z".equals(this.getTypeDeRessourceContinue()))
            zonesManquantes.add("Merci de préciser le type de publication (110$a)");

        // Test des zones qui ne doivent pas être présentes
        if (!(this.getIssn() == null || this.getIssn().isEmpty()))
            zonesPresentes.add("011 : ISSN");
        if (!(this.getIssn035() == null || this.getIssn035().isEmpty()))
            zonesPresentes.add("035 : Identifiant ISSN de la notice");
        if (this.getSourceDeCatalogageISSN() != null && this.getSourceDeCatalogageISSN().contains("ISSN"))
            zonesPresentes.add("801 : Source de catalogage ISSN");
        if (!(this.getCentreISSN() == null || this.getCentreISSN().isEmpty()))
            zonesPresentes.add("802 : Centre ISSN");
    }

    public void setTitre() {
        String titre = titreA != null ? titreA : "";
        if (titreE != null)
            titre += " " + titreE;
        if (titreC != null)
            titre += " " + titreC;
        if (titreH != null)
            titre += " " + titreH;
        if (titreI != null)
            titre += " " + titreI;
        this.titre = titre;
    }

    public List<String> getNoteIdentifiants() {
        if (this.noteIdentifiants != null)
            return Arrays.asList(this.noteIdentifiants.split("---note---"));
        return new ArrayList<>();
    }

    public List<String> getNoteGeneraleCatalogueur() {
        if (this.noteGeneraleCatalogueur != null)
            return Arrays.asList(this.noteGeneraleCatalogueur.split("---note---"));
        return new ArrayList<>();
    }
}
