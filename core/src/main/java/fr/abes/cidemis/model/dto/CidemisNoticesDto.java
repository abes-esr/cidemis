package fr.abes.cidemis.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CidemisNoticesDto implements Serializable {
    private String ppn;
    private String titre;
    private String titreA;
    private String titreE;
    private String titreC;
    private String titreH;
    private String titreI;
    private String titreCle;
    private String titreCleAbrege;
    private String pays;
    private String dateDebut;
    private String dateFin;
    private String typeDocument;
    private String centreISSN;
    private String issn;
    private String issn035;
    private String frbnf;
    private String typeDeRessourceContinue;
    private String sourceDeCatalogageISSN;
    private String noteIdentifiants;
    private String noteGeneraleCatalogueur;
    private String statutNotice;
}
