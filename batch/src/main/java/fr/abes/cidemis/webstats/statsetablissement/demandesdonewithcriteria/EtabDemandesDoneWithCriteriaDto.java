package fr.abes.cidemis.webstats.statsetablissement.demandesdonewithcriteria;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class EtabDemandesDoneWithCriteriaDto {
    private String iln;
    private String rcrDemandeur;
    private Integer idTypeDemande;
    private Integer idEtatDemande;
    private String pays;
    private String typeDocument;
    private String typeRessourceContinue;
    private String dateFin;
    private Integer count;
}
