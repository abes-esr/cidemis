package fr.abes.cidemis.model.cidemis;

import fr.abes.cidemis.constant.Constant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "PIECES_JUSTIFICATIVES")
@NoArgsConstructor
@Getter @Setter
public class PiecesJustificatives implements Serializable {
	private static final long serialVersionUID = 4174170531271926778L;

	@Id
	@Column(name = "ID_PIECE", nullable = false)
	private Integer idPiece;
	@ManyToOne
	@JoinColumn(name = "ID_DEMANDE")
	private Demandes demande;
	@ManyToOne
	@JoinColumn(name = "USER_NUM")
	private CbsUsers cbsUsers;
	@Column(name = "LIEN_PIECE")
	private String lienPiece;

	public PiecesJustificatives(Integer id) {
	    this.idPiece = id;
    }

    public String getPathFichier(String path){
        return path + demande.getIdDemande().toString() + "/" + idPiece + "_" + lienPiece;
    }

    public String getUrlfichier(){
        return "diffusion?id=" + idPiece;
    }

    public String getPublicname(){
        String prefix = "";
        switch(getDemande().getTypesDemandes().getIdTypeDemande()){
            case Constant.TYPE_DEMANDE_NUMEROTATION:
                prefix = "NUM";
                break;
            case Constant.TYPE_DEMANDE_CORRECTION:
                prefix = "COR";
                break;
            case Constant.TYPE_DEMANDE_CREATION:
                prefix = "CRE";
                break;
            default:
                break;
        }
        return getDemande().getIdDemande() + "_" + prefix + "_" + getLienPiece();
    }

}