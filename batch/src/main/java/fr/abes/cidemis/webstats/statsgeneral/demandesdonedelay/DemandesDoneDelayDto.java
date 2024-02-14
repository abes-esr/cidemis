package fr.abes.cidemis.webstats.statsgeneral.demandesdonedelay;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class DemandesDoneDelayDto {
    String pays;
    String slice;
    Integer count;
}
