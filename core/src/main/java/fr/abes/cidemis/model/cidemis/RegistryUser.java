package fr.abes.cidemis.model.cidemis;

import lombok.Data;

import java.io.Serializable;

@Data
public class RegistryUser implements Serializable {
	private static final long serialVersionUID = 5231147346271152095L;


	private Integer userNum;
	private String userKey;
	private String userGroup;
	private String library;
	private String shortName;
	private Boolean loginAllowed;
	private String iln;

	public String getLibrary() {
		return library.trim();
	}

 }