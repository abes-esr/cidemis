package fr.abes.cidemis.webstats.statsetablissement.demandesissnwithcriteriaactivity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class EtabDemandesISSNWithCriteriaActivityDto {
    private String ilnRattache;
    private String rcrDemandeur;
    private Integer idTypeDemande;
    private String status;
    private String pays;
    private String typeDocument;
    private String typeRessourceContinue;
    private String dateFin;
    private Integer count;
}
