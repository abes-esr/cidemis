package fr.abes.cidemis.model.cidemis;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "TAGGUES")
@NoArgsConstructor
@Getter @Setter
@SequenceGenerator(name = "SEQ_TAGGUES", sequenceName = "SEQ_TAGGUES", initialValue = 1, allocationSize = 1)
public class Taggues implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID_TAGGUE")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TAGGUES")
	private Integer idTaggue;
	@OneToOne
	@JoinColumn(name = "ID_DEMANDE")
	private Demandes demande;
	@Column(name = "LIBELLE_TAGGUE")
	private String libelleTaggue;
	@Column(name = "COULEUR_TAGGUE")
	private String couleurTaggue;

}
