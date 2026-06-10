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