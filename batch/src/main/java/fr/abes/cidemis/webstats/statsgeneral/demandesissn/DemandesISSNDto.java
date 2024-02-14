package fr.abes.cidemis.webstats.statsgeneral.demandesissn;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter @Setter
public class DemandesISSNDto implements Serializable {
    private String idTypeDemande;
    private String status;
    private String count;
}
