package fr.abes.cidemis.model.cidemis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.abes.cidemis.constant.Constant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "DEMANDES")
@Getter
@Setter
@JsonIgnoreProperties({"journalDemandesList", "commentaireslist", "piecesJustificativeslist"})
public class Demandes implements Serializable {
    private static final long serialVersionUID = 4028006026608410128L;
    @Transient
    private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");

    @Id
    @Column(name = "ID_DEMANDE")
    private Integer idDemande;
    @ManyToOne
    @JoinColumn(name = "ID_TYPEDEMANDE")
    private TypesDemandes typesDemandes;
    @ManyToOne
    @JoinColumn(name = "ID_ETATDEMANDE")
    private EtatsDemandes etatsDemandes;
    @ManyToOne
    @JoinColumn(name = "USER_NUM")
    private CbsUsers cbsUsers;
    @Column(name = "DATE_DEMANDE")
    private Date dateDemande;
    @Column(name = "DATE_MODIF")
    private Date dateModif;
    @Column(name = "RCR_DEMANDEUR")
    private String rcrDemandeur;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PPN")
    @NotFound(action = NotFoundAction.IGNORE)
    private CidemisNotices notice;
    @OneToMany(mappedBy = "demande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commentaires> commentairesList;
    @OneToOne(mappedBy = "demande", cascade = CascadeType.ALL, orphanRemoval = true)
    private Taggues taggues;
    @OneToMany(mappedBy = "demandes", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JournalDemandes> journalDemandesList;
    @OneToMany(mappedBy = "demande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PiecesJustificatives> piecesJustificativeslist;
    @Column(name = "ZONES")
    private String zones;
    @Column(name = "NB_COMMENTAIRES")
    private Integer nbCommentaires;
    @Column(name = "NB_COMMENTAIRESISSN")
    private Integer nbCommentairesISSN;
    @Column(name = "NB_PIECESJUSTIFICATIVES")
    private Integer nbPiecesJustificatives;
    @ManyToOne
    @JoinColumn(name = "ID_DEMANDE_LIEE")
    @NotFound(action = NotFoundAction.IGNORE)
    private Demandes demandeLiee;
    @Column(name = "CENTRE_ISSN")
    private String centreISSN;
    @Column(name = "ID_PROFIL")
    private Integer idProfil;
    @Column(name = "ISSN")
    private String issn;
    @Column(name = "TITRE")
    private String titre;
    @Column(name = "CR")
    private String cr;
    @Column(name="ILN")
    private String iln;

    public Demandes(Integer id) {
        this.idDemande = id;
    }

    public Demandes() {
        this.idDemande = -1;
        this.commentairesList = new ArrayList();
        this.journalDemandesList = new ArrayList();
        this.piecesJustificativeslist = new ArrayList();
    }

    public Date getDateModif() {
        if (this.dateModif != null)
            return this.dateModif;
        return this.dateDemande;
    }

    public String getDateDemandeFormatee() {
        if (dateDemande == null)
            return null;
        return dateformat.format(getDateDemande());
    }

    public String getDateModifFormatee() {
        if (this.dateModif != null)
            return dateformat.format(this.dateModif);
        return dateformat.format(this.dateDemande);
    }

    /**
     * @return Retourne l'ISSN de la demande selon certains critères (si c'est
     * une demande de numérotation, alors prends l'ISSN de la demande
     * sinon de la notice)
     */
    public String getConditionnalIssn() {
        if (this.getNotice() == null) {
            return "";
        }
        if (this.getTypesDemandes().getIdTypeDemande() == Constant.TYPE_DEMANDE_NUMEROTATION)
            return (this.getIssn() == null) ? "" : this.getIssn();
        else
            return (this.getNotice().getIssn() == null) ? "" : this.getNotice().getIssn();
    }

    /**
     * Vérifie que pour cette demande, la notice est éligible à une demande dans
     * Cidemis
     *
     * @param zonesManquantes
     * @param zonesPresentes
     * @return
     */
    public boolean checkZones(List<String> zonesManquantes, List<String> zonesPresentes) {
        return this.getNotice().checkZones(this.getTypesDemandes().getIdTypeDemande(), zonesManquantes, zonesPresentes);
    }


    /**
     * Vérifie si, pour cette demande, il faut envoyer un mail au CIEPS Est-ce
     * que la demande est dans le bon état, est-ce qu'elle est adressé à la
     * france et est-ce que l'utilisateur à le droit
     *
     * @param user
     * @return true if an email must be sent to CIEPS
     */
    public boolean sendMailToCieps(CbsUsers user) {
        if (this.getEtatsDemandes().getIdEtatDemande().equals(Constant.ETAT_VERS_INTERNATIONAL)
                || this.getEtatsDemandes().getIdEtatDemande().equals(Constant.ETAT_VALIDEE_PAR_RESPONSABLE_CR_VERS_INTERNATIONAL)
                || this.getEtatsDemandes().getIdEtatDemande().equals(Constant.ETAT_PRECISION_PAR_RESPONSABLE_CR))
            return !Constant.getCodePaysFr().contains(this.getNotice().getPays())
                    && (user.getRoles().getIdRole().equals(Constant.ROLE_CORCAT)
                    || user.getRoles().getIdRole().equals(Constant.ROLE_ISSN));
        return false;
    }

    public String getCentreissnCIEPS() {
        return getCentreISSN().replaceFirst("^0", "_");
    }

    /**
     * Détermine si une demande fait partie d'une liste d'état
     *
     * @param states
     * @return true if states contains the state of demande
     */
    public boolean isStateIn(int[] states) {
        int i = 0;
        boolean find = false;

        while (i < states.length && !find) {
            if (states[i] == this.getEtatsDemandes().getIdEtatDemande())
                find = true;

            i++;
        }

        return find;
    }

    public JournalDemandes getLastjournal() {
        JournalDemandes lastJournal = null;
        if (!getJournalDemandesList().isEmpty()) {
            lastJournal = getJournalDemandesList().get(getJournalDemandesList().size() - 1);
        }
        return lastJournal;
    }

    /**
     * Renvoie une liste de toutes les pièces justificatives d'une demande et de sa demande liée
     * @return liste des pièces justificatives
     */
    public List<PiecesJustificatives> getAllpiecesjustificativeslist() {
        List<PiecesJustificatives> allPiecesJustificativesList = new ArrayList();
        allPiecesJustificativesList.addAll(getPiecesJustificativeslist());
        if (demandeLiee != null)
            allPiecesJustificativesList.addAll(demandeLiee.getAllpiecesjustificativeslist());

        return allPiecesJustificativesList;
    }

    public String getTitreFormatte() {
        if (notice != null) {
            if (notice.getTitre().isEmpty())
                return titre.trim();
            return notice.getTitre().trim();
        }
        return "";
    }


    public void setNoticeSupprimeeSudoc() {
        CidemisNotices notice = this.getNotice();
        notice.setNoticeSupprimeeSudoc();
        this.setNotice(notice);
        this.setTitre("NOTICE SUPPRIMÉE DU SUDOC");
    }
}