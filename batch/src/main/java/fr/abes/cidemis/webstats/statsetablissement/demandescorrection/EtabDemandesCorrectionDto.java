package fr.abes.cidemis.webstats.statsetablissement.demandescorrection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class EtabDemandesCorrectionDto {
    String ilnRattache;
    String rcrDemandeur;
    String zones;
    Integer idEtatDemande;
    Integer count;
}
