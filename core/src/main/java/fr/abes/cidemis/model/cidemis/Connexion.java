package fr.abes.cidemis.model.cidemis;

import java.io.Serializable;

public class Connexion implements Serializable {
	private static final long serialVersionUID = -3202902594966138473L;
	
	private CbsUsers user;
    private RegistryUser registryUser;

    public Connexion(CbsUsers user,RegistryUser registryUser) {
        this.user = user;
        this.registryUser = registryUser;
    }
    
    public CbsUsers getUser() {
        return user;
    }

    public void setUser(CbsUsers user) {
        this.user = user;
    }

    public RegistryUser getRegistryuser() {
        return registryUser;
    }

    public void setRegistryuser(RegistryUser registryUser) {
        this.registryUser = registryUser;
    }
}
