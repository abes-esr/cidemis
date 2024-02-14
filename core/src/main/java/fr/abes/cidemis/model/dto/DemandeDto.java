package fr.abes.cidemis.model.dto;

import fr.abes.cidemis.components.Fichier;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DemandeDto {
    private Integer idDemande;
    private String ppn;
    private Integer typesDemandes;
    private String commentaireTxt;
    private String taggueTxt;
    private Boolean commentaireVisibleIssn;
    private Integer lastIdCommentaire;
    private String action;
    private String zones;
    private Integer idProfil;
    private String issn;
    private String titre;
    private String numPpn;
    private String rcrDemandeur;
    private String codePays;
    private Integer fileCount;
    private String[] filesToDelete;
    private List<Fichier> fichiers;
}
