package fr.abes.cidemis.model.cidemis;

import java.io.Serializable;

import fr.abes.cidemis.constant.Constant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
            case Constant.TYPE_DEMANDE_NUMEROTATION -> prefix = "NUM";
            case Constant.TYPE_DEMANDE_CORRECTION -> prefix = "COR";
            case Constant.TYPE_DEMANDE_CREATION -> prefix = "CRE";
            default -> {
                }
        }
        return getDemande().getIdDemande() + "_" + prefix + "_" + getLienPiece();
    }

}