package fr.abes.cidemis.webstats.statsetablissement.demandesissnactivity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class EtabDemandesISSNActivityDto {
    private String ilnRattache;
    private String rcrDemandeur;
    private Integer idTypeDemande;
    private String idEtatDemande;
    private Integer count;
}
