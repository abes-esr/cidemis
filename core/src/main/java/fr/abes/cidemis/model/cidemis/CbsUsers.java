package fr.abes.cidemis.model.cidemis;

import fr.abes.cidemis.constant.Constant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "CBS_USERS")
@Getter @Setter
@NoArgsConstructor
public class CbsUsers implements Serializable {
	@Id
	@Column(name = "USER_NUM")
	private Integer userNum;
	@Column(name = "USER_KEY")
	private String userKey;
	@Column(name = "USER_EMAIL")
	private String userEmail;
	@Column(name = "EDIT_DATE")
	private Date editDate;
	@Column(name = "USER_GROUP")
	private String userGroup;
	@Column(name = "SHORT_NAME")
	private String shortName;
	@Column(name = "LIBRARY")
    private String library;
	@Column(name = "ILN")
    private String iln;
	@Column(name = "ID_PROFIL")
    private Integer idProfil;
	@Column(name = "ILN_RATTACHE")
	private String ilnRattache;
	@ManyToOne
	@JoinColumn(name = "ID_ROLE")
	private Roles roles;

    @OneToMany(mappedBy = "cbsUsers")
	private Set<Commentaires> commentairesList;
    @OneToMany(mappedBy = "cbsUsers")
	private List<Demandes> demandesList;
    @OneToMany(mappedBy = "cbsUsers")
	private List<JournalDemandes> journalDemandesList;
    @OneToMany(mappedBy = "cbsUsers")
	private List<PiecesJustificatives> piecesJustificativesList;
    @OneToMany(mappedBy = "cbsUsers")
	private List<Options> optionsList;

	
	public CbsUsers(Integer userNum) {
        this.userNum = userNum;
	}


    public Boolean isISSNOrCIEPS() {
    	return this.getRoles().getIdRole() == Constant.ROLE_CIEPS || this.getRoles().getIdRole() == Constant.ROLE_ISSN;
	}

    public Boolean getIsdeploye(){
        return !getUserKey().matches("(.*)CC(.*)");
    }

 }