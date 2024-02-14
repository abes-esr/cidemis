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
@Table(name = "ZONE_CORRECTION")
@Getter @Setter
@NoArgsConstructor
public class ZoneCorrection implements Serializable {
	private static final long serialVersionUID = 4071006460921857847L;
	@Id
	@Column(name = "ZONE")
	private String zone;
	@Column(name = "LIBELLE")
	private String libelle;


 }