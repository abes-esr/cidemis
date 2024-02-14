package fr.abes.cidemis.model.cidemis;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "OPTIONS_ROLES")
@NoArgsConstructor
@Getter @Setter
@SequenceGenerator(name = "SEQ_OPTIONS_ROLES", sequenceName = "SEQ_OPTIONS_ROLES", initialValue = 1, allocationSize = 1)
public class OptionsRoles implements Serializable {
	private static final long serialVersionUID = -3474073155413559175L;

	@Id
	@Column(name = "ID_OPTIONS_ROLES")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_OPTIONS_ROLES")
	private Integer idOptionsRoles;
	@ManyToOne
	@JoinColumn(name = "ID_ROLES")
	private Roles roles;
	@Column(name = "LIB_OPTION")
	private String libOption;
	@Column(name = "VALUE")
	private String value;
	@Column(name = "TYPE")
	private String type;

 }