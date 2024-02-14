package fr.abes.cidemis.webstats.statsetablissement.demandesissnstate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class EtabDemandesISSNStateDto {
    private String ilnRattache;
    private String rcrDemandeur;
    private Integer idTypeDemande;
    private String status;
    private Integer count;
}
