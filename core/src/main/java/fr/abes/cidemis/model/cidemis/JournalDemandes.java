package fr.abes.cidemis.model.cidemis;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "JOURNAL_DEMANDES")
@NoArgsConstructor
@Getter @Setter
public class JournalDemandes implements Serializable {
	private static final long serialVersionUID = -6691816830803418210L;
	@Id
	@Column(name = "ID_JOURNALDEMANDE")
	private Integer idJournalDemande;

	@ManyToOne
	@JoinColumn(name = "ID_ETATDEMANDE")
	private EtatsDemandes etatsdemandes;
	@ManyToOne
	@JoinColumn(name = "ID_DEMANDE")
	private Demandes demandes;
	@ManyToOne
	@JoinColumn(name = "USER_NUM")
	private CbsUsers cbsUsers;
	@Column(name = "DATE_ENTREE")
	private Date dateEntree;

	public JournalDemandes(Integer id) { this.idJournalDemande = id; }
 }