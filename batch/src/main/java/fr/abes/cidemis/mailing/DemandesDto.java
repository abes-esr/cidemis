package fr.abes.cidemis.mailing;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class DemandesDto {

    private Integer idDemande;
    private Date dateDemande;
    private Integer typesDemandes;
    private String zones;
    private String pays;
    private String email;
    private String centreIssn;
    private String typeRessourceContinue;
    private String titre;
    private String issn;
    private String commentaires;
    private String ppn;
    private String piecesJustificatives;
    private String liensNotice;

    public DemandesDto(Integer idDemande) {
        this.idDemande = idDemande;
    }

}
