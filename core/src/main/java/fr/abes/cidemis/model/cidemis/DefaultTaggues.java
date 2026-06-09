package fr.abes.cidemis.model.cidemis;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "DEFAULT_TAGGUES")
@NoArgsConstructor
@Getter @Setter
public class DefaultTaggues implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "LIBELLE_TAGGUE")
	private String libelleTaggue;
	@Column(name = "COULEUR_TAGGUE")
	private String couleurTaggue;

}
