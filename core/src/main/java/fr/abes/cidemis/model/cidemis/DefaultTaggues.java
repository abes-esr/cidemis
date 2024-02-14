package fr.abes.cidemis.model.cidemis;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
