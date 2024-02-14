package fr.abes.cidemis.webstats.statsgeneral.demandesdonewithcriteria;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class DemandesDoneWithCriteriaDto {
    Integer idTypeDemande;
    Integer idEtatDemande;
    String pays;
    String typeDocument;
    String typeRessourceContinue;
    String dateFin;
    Integer count;
}
