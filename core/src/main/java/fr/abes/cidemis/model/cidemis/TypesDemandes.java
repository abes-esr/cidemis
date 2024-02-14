package fr.abes.cidemis.model.cidemis;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "TYPESDEMANDES")
@NoArgsConstructor
@Getter @Setter
@SequenceGenerator(name = "SEQ_TYPESDEMANDES", sequenceName = "SEQ_TYPESDEMANDES", initialValue = 1, allocationSize = 1)
public class TypesDemandes implements Serializable {
	private static final long serialVersionUID = -5882480657134287667L;

	@Id
	@Column(name = "ID_TYPEDEMANDE")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TYPESDEMANDES")
	private Integer idTypeDemande;
	@Column(name = "LIBELLE_TYPEDEMANDE")
	private String libelleTypeDemande;
	@OneToMany(mappedBy = "typesDemandes")
	private List<Demandes> demandesList;


	public void clearExternalReferences() {
		demandesList = null;
	}


 }