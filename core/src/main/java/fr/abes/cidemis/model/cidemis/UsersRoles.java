package fr.abes.cidemis.model.cidemis;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "USERS_ROLES")
@NoArgsConstructor
@Getter @Setter
@SequenceGenerator(name = "SEQ_USERS_ROLES", sequenceName = "SEQ_USERS_ROLES", initialValue = 1, allocationSize = 1)
public class UsersRoles implements Serializable {
	private static final long serialVersionUID = 3161694613469646679L;

	@Id
	@Column(name = "ID_USERS_ROLES")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_USERS_ROLES")
	private Integer idUsersRoles;
	@Column(name = "USER_GROUP")
	private String userGroup;
	@ManyToOne
	@JoinColumn(name = "ID_ROLE")
	private Roles idRole;

 }