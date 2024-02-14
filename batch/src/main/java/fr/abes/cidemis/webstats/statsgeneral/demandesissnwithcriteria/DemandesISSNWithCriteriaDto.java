package fr.abes.cidemis.webstats.statsgeneral.demandesissnwithcriteria;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class DemandesISSNWithCriteriaDto {
    Integer idTypeDemande;
    String status;
    String pays;
    String typeDocument;
    String typeRessourceContinue;
    String dateFin;
    Integer count;
}
