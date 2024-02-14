package fr.abes.cidemis.model.cidemis;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "ROLES")
@NoArgsConstructor
@Getter @Setter
@SequenceGenerator(name = "SEQ_ROLES", sequenceName = "SEQ_ROLES", initialValue = 1, allocationSize = 1)
public class Roles implements Serializable {
	private static final long serialVersionUID = -36542347281543451L;
	@Id
	@Column(name = "ID_ROLE")
	@GeneratedValue(generator = "SEQ_ROLES", strategy = GenerationType.SEQUENCE)
	private Integer idRole;
	@Column(name = "LIB_ROLE")
	private String libRole;
	@OneToMany(mappedBy = "roles")
	private List<CbsUsers> cbsUsersList;


	/**
	 * @return the libRole
	 */
	public String getLibRole() {
		if (libRole == null)
			return "";
		return libRole;
	}

 }