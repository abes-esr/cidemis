package fr.abes.cidemis.model;

import java.io.Serializable;

public class CodePays implements Serializable {
	private static final long serialVersionUID = 7378085441175743973L;
	
	private String pays;
	private String iso31661a3;
	private String iso31661a2;
	private String codeCentreISSN;
	private String email;

	public CodePays(){
		// Pas d'initialisation
	}
        
	public CodePays(String pays,String iso31661a3,String iso31661a2,String codeCentreISSN,String email){
	    this.pays = pays;
	    this.iso31661a3 = iso31661a3;
        this.iso31661a2 = iso31661a2;
        this.codeCentreISSN = codeCentreISSN;
        this.email = email;
	}

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getIso31661a3() {
        return iso31661a3;
    }

    public void setIso31661a3(String iso31661a3) {
        this.iso31661a3 = iso31661a3;
    }

    public String getIso31661a2() {
        return iso31661a2;
    }

    public void setIso31661a2(String iso31661a2) {
        this.iso31661a2 = iso31661a2;
    }

    public String getCodecentreissn() {
        return codeCentreISSN;
    }

    public void setCodecentreissn(String codeCentreISSN) {
        this.codeCentreISSN = codeCentreISSN;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
 }