package fr.abes.cidemis.bean;

import fr.abes.cidemis.model.cidemis.Demandes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DemandesListe implements Serializable {
	private static final long serialVersionUID = 2548620712035266538L;
	
	private ArrayList<Demandes> demandesList;
    
    public DemandesListe() {
    	this.demandesList = new ArrayList();
    }

    public List<Demandes> getDemandesList() {
        return demandesList;
    }

    public void setDemandesList(List<Demandes> demandesList) {
    	this.demandesList = new ArrayList();
    	this.demandesList.addAll(demandesList);
    }
    
    /**
     * Set Array of demandes and demande from list
     * @param demandeslist
     * @param demande
     */
    public void setDemandeslist(List<Demandes> demandeslist, Demandes demande) {
    	ArrayList<Demandes> demandeslistWithoutPPN = new ArrayList();
    	
    	for (Demandes dem : demandeslist)
    		if (demande == null || dem.getIdDemande().intValue() != demande.getIdDemande().intValue())
    			demandeslistWithoutPPN.add(dem);
    	
    	this.demandesList = demandeslistWithoutPPN;
    }

}
