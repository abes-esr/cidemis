package fr.abes.cidemis.model.cidemis;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.web.HtmlEntities;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "COMMENTAIRES")
@NoArgsConstructor
@Getter @Setter
@SequenceGenerator(name = "SEQ_COMMENTAIRES", sequenceName = "SEQ_COMMENTAIRES", initialValue = 1, allocationSize = 1)
public class Commentaires implements Serializable {
	private static final long serialVersionUID = -2688665797944255979L;
	@Id
	@Column(name = "ID_COMMENTAIRE")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_COMMENTAIRES")
	private Integer idCommentaire;
	@ManyToOne
	@JoinColumn(name = "ID_DEMANDE")
	private Demandes demande;
	@ManyToOne
	@JoinColumn(name = "USER_NUM")
	private CbsUsers cbsUsers;
	@Column(name = "LIB_COMMENTAIRE")
	private String libCommentaire;
	@Column(name = "DATE_CREATION")
    private Date dateCommentaire;
	@Column(name = "VISIBLE_ISSN")
    private Boolean visibleISSN;
	
	/**
	 * Retourne si $user peut modifier cette demande
	 * @param user
	 * @return
	 */
	public Boolean canUpdate(CbsUsers user) {
		Boolean updatable = false;
		
		if (user.getUserKey().compareTo(this.getCbsUsers().getUserKey()) == 0) {
			if (user.getRoles().getIdRole().compareTo(Constant.ROLE_CATALOGUEUR) == 0 && (
					this.getDemande().getEtatsDemandes().getIdEtatDemande().intValue() == Constant.ETAT_EN_ATTENTE_VALIDATION_CATALOGUEUR
					|| this.getDemande().getEtatsDemandes().getIdEtatDemande().intValue() == Constant.ETAT_EN_ATTENTE_PRECISION_CATALOGUEUR
					)) {
				updatable = true;
			}
			if (user.getRoles().getIdRole().compareTo(Constant.ROLE_RESPONSABLE_CR) == 0 && (
					this.getDemande().getEtatsDemandes().getIdEtatDemande().intValue() == Constant.ETAT_EN_ATTENTE_VALIDATION_RESPONSABLE_CR
					|| this.getDemande().getEtatsDemandes().getIdEtatDemande().intValue() == Constant.ETAT_EN_ATTENTE_PRECISION_RESPONSABLE_CR
					)) {
				updatable = true;
			}
		}
		
		return updatable;
	}

	public String getDateCommentaireFormatee() {
		if (dateCommentaire == null)
			return "";
		SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");

		return dateformat.format( dateCommentaire );
	}

	public String getLibCommentaireHTML(){
		return HtmlEntities.htmlentities(getLibCommentaire()).replaceAll("\r\n", "<br/>");
	}


}