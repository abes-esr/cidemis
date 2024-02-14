package fr.abes.cidemis.bean;

import java.io.Serializable;

public class Notice  implements Serializable {
	private static final long serialVersionUID = 1366555882099119684L;
	
	private String ppn;
    private String titre;
    private String pays;
    private String date;
    private String typeRessource;
    private String typeDocument;
    
    public Notice() {
    	this.ppn = "";
    	this.titre = "";
    	this.pays = "";
    	this.date = "";
    	this.typeRessource = "";
    	this.typeDocument = "";
    }   
    
    public String getPpn() {
        return ppn;
    }

    public void setPpn(String ppn) {
        this.ppn = ppn;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTypeRessource() {
        return typeRessource;
    }

    public void setTypeRessource(String typeRessource) {
        this.typeRessource = typeRessource;
    }

    public String getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(String typeDocument) {
        this.typeDocument = typeDocument;
    }
    

    
}
