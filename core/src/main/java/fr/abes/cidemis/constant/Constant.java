package fr.abes.cidemis.constant;

import fr.abes.cidemis.model.CodePays;
import fr.abes.cidemis.model.cidemis.DefaultTaggues;
import fr.abes.cidemis.model.cidemis.ZoneCorrection;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;

@Getter
public class Constant implements Serializable {
	private static final long serialVersionUID = -1467113392579110521L;

	public static final String ENCODE = "UTF-8";

	/**
	 * *******************************************************************************
	 * DEFINITION DES PROPRIETES
	 *******************************************************************************/

	public static final String PROPERTIES_URL_SITE = "URL_SITE";

	public static String PATH_MAIL_MENSUEL = "";
	public static boolean SKIP_MAIL_FLAG = true;
	public static String PROPERTIES_PSI_LIEN_PERENNE;
	public static String PROPERTIES_PSI_LIEN_PERENNE_ISSN;


	/**
	 * *******************************************************************************
	 * DEFINITION DES AUTRES DONNEES
	 *******************************************************************************/
	public static final String ADMIN_ISSN = "351IS001";
	
	public static final int TYPE_DEMANDE_NUMEROTATION = 22;
	public static final int TYPE_DEMANDE_CORRECTION = 23;
	public static final int TYPE_DEMANDE_CREATION = 24;

	public static final int ETAT_EN_ATTENTE_VALIDATION_CATALOGUEUR = 22;
	public static final int ETAT_EN_ATTENTE_VALIDATION_RESPONSABLE_CR = 23;
	public static final int ETAT_VALIDEE_PAR_CATALOGUEUR = 24;
	public static final int ETAT_VALIDEE_PAR_RESPONSABLE_CR = 25;
	public static final int ETAT_VALIDEE_PAR_RESPONSABLE_CR_VERS_INTERNATIONAL = 26;
	public static final int ETAT_EN_ATTENTE_PRECISION_CATALOGUEUR = 27;
	public static final int ETAT_EN_ATTENTE_PRECISION_RESPONSABLE_CR = 28;
	public static final int ETAT_VERS_INTERNATIONAL = 33;
	public static final int ETAT_PRECISION_PAR_CATALOGUEUR = 34;
	public static final int ETAT_PRECISION_PAR_RESPONSABLE_CR = 35;
	public static final int ETAT_TRAITEMENT_TERMINE_REFUSEE = 36;
	public static final int ETAT_TRAITEMENT_TERMINE_ACCEPTEE = 37;
	public static final int ETAT_TRAITEMENT_REJETEE_PAR_CR = 38;
	public static final int ETAT_ARCHIVEE = 39;

	public static final int ROLE_CATALOGUEUR = 1;
	public static final int ROLE_CORCAT = 2;
	public static final int ROLE_ABES = 3;
	public static final int ROLE_ISSN = 4;
	public static final int ROLE_CIEPS = 5;

	public static final String DEFAULT_COLOR = "DEE7F8";

	public static final String CODE_PAYS_SEPARATEUR = "\\t";
	/**
	 * *******************************************************************************
	 * ALLOUER DYNAMIQUEMENT
	 *******************************************************************************/
	public static final Map<String, String> LISTE_TYPE_PUBLICATION_SHORT = new HashMap<>();
	public static final Map<String, String> LISTE_TYPE_DOCUMENT = new HashMap<>();
	public static final Map<String, String> LISTE_TYPE_PUBLICATION = new HashMap<>();
	public static final Map<String, List<String>> LISTE_FILTRE = new HashMap<>();
	public static final List<DefaultTaggues> LISTE_DEFAULT_TAGGUES = new ArrayList<>();
	public static final List<ZoneCorrection> LISTE_ZONE_CORRECTION = new ArrayList<>();

	public static final List<String> CODE_PAYS_FR = new ArrayList<>();
	public static final Map<String, Integer> PROFILS = new HashMap<>();
	public static final Map<String, String> COLONNES = new HashMap<>();

	public static final Map<String, CodePays> CODE_PAYS_SORTED = new TreeMap<>();
	public static final Map<String, String> LISTE_LIBELLEPAYS_CODEPAYS = new TreeMap<>(
			String::compareTo);
	public static final Map<String, CodePays> LISTE_CENTRE_ISSN = new TreeMap<>();
	public static final Integer PROFIL_PAS_PROFIL = 0;
	public static final Integer PROFIL_INDETERMINE = 1;
	public static final Integer PROFIL_COLLECTION = 2;
	public static final Integer PROFIL_PERIODIQUE_ELECTRONIQUE = 3;
	public static final Integer PROFIL_PERIODIQUE_IMPRIME = 4;
	public static final Integer PROFIL_PERIODIQUE_IMPRIME_VIVANT = 5;
	public static final Integer PROFIL_PERIODIQUE_IMPRIME_MORT = 6;
	public static final Integer PROFIL_TOUTES_DEMANDES = 7;
	public static final String PROFIL_PAS_PROFIL_TXT = "";
	public static final String PROFIL_TOUTES_DEMANDES_TXT = "toutes-demandes";
	public static final String PROFIL_INDETERMINE_TXT = "indetermine";
	public static final String PROFIL_COLLECTION_TXT = "collection";
	public static final String PROFIL_PERIODIQUE_ELECTRONIQUE_TXT = "periodique-electronique";
	public static final String PROFIL_PERIODIQUE_IMPRIME_TXT = "periodique-imprime";
	public static final String PROFIL_PERIODIQUE_IMPRIME_VIVANT_TXT = "periodique-imprime-vivant";
	public static final String PROFIL_PERIODIQUE_IMPRIME_MORT_TXT = "periodique-imprime-mort";
	public static final String COL_DATE = "col_date";
	public static final String COL_DATE_MODIF = "col_date_modif";
	public static final String COL_DEMANDE_TYPE = "col_demande_type";
	public static final String COL_DEMANDE_NUM = "col_demande_num";
	public static final String COL_PPN = "col_ppn";
	public static final String COL_TITRE = "col_titre";
	public static final String COL_ETAT = "col_etat";
	public static final String COL_ILN = "col_iln";
	public static final String COL_ISSN = "col_issn";
	public static final String COL_FRBNF = "col_frbnf";
	public static final String COL_PUBLICATION_TYPE = "col_publication_type";
	public static final String COL_SUPPORT_TYPE = "col_support_type";
	public static final String COL_PUBLICATION_PAYS = "col_publication_pays";
	public static final String COL_STATUT_DE_VIE = "col_statut_de_vie";
	public static final String COL_PUBLICATION_DATE = "col_publication_date";
	public static final String COL_PIECES_JUSTIF = "col_pieces_justif";
	public static final String COL_RCR = "col_rcr";
	public static final String COL_TAGGUE = "col_taggue";
	public static final String COL_ACTION = "col_action";
	public static final String COLTYPE_INPUT = "input-text";
	public static final String COLTYPE_SELECT = "select";
	public static final String COLTYPE_CHECKBOX = "checkbox";
	public static final String COLTYPE_NONE = "none";

	/** constantes pour le batch */
	public static final String SPRING_BATCH_FORCING_USAGE_JPA_TRANSACTION_MANAGER = "Forcing the use of a JPA transactionManager";
	public static final String SPRING_BATCH_ENTITY_MANAGER_FACTORY_NULL = "Unable to initialize batch configurer : entityManagerFactory must not be null";
	public static final String SPRING_BATCH_FORCING_USAGE_MAP_BASED_JOBREPOSITORY = "Forcing the use of a Map based JobRepository";
	public static final String SPRING_BATCH_INITIALIZATION_FAILED = "Ne peut pas initialiser spring batch : ";
	public static final String JOB_EXPORT_STATISTIQUES_START = "debut du job jobExportStatistiques...";
	public static final String SPRING_BATCH_JOB_EXPORT_STATISTIQUES_NAME = "exportStatistiques";
	public static final String JOB_MAILING_START = "début du job jobMailing";
	public static final String SPRING_BATCH_JOB_MAILING = "mailing";
	public static final String SPRING_BATCH_JOB_REFUS = "commentairesRefus";
	public static final String JOB_REFUS_START = "début du job d'ajout des commentaires de refus";
	public static final String ENTER_EXECUTE_FROM_SELECTDEMANDESTASKLET = "entrée dans execute de SelectDemandesTasklet";
	public static final String ENTER_EXECUTE_FROM_ENVOIMAILTASKLET = "entrée dans execute de EnvoiMailTasklet";
	public static final String ENTER_EXECUTE_FROM_SELECTDEMANDESREFUSTASKLET = "entréee dans execute SelectDemandesRefusTasklet";
	public static final String ENTER_EXECUTE_FROM_MAJNOTICES = "entree dans execute MajSudocTasklet";
	public static final String NO_DEMANDE_TO_PROCESS = "Aucune demande à traiter";
	public static final String NODEMANDE = "NODEMANDE";

	public static final String SPRING_BATCH_TOTAL_TIME_EXECUTION_MILLISECONDS = "temps total execution (ms) = ";
	public static final String SPRING_BATCH_TOTAL_TIME_EXECUTION_MINUTES = "temps total execution (minutes) = ";

	public static final String FAILED = "FAILED";
	public static final String COMPLETED = "COMPLETED";
	public static final String BLOCKED = "blocked";

	public static final String ANNEE = "annee";
	public static final String MOIS = "mois";

	public static final String ERROR_MONTH_RANGE = "Le mois doit être compris entre 1 et 12";
	public static final String ERROR_YEAR_RANGE = "L'année ne peut pas être inférieure à l'année courante";

	public static final String ERROR_ATTACHMENT_NOT_FOUND = "Le fichier à ajouter en pièce jointe pour le mail n'a pas été trouvé";

	/**RestResponseEntityExceptionHandler**/
	public static final String ERROR_CAUGHT = "error caught: ";
	public static final String ERROR_UNKNOWN_REST_CONTROLLER = "unknown error caught in RESTController, {}";
	public static final String REST_RESPONDING_WITH_STATUS = "Response REST avec statut {}";
	public static final String ACCES_INTERDIT = "Accès interdit.";

	/**
	 * *******************************************************************************
	 * METHOD GET/SET
	 *******************************************************************************/
	public static List<ZoneCorrection> getAllZoneCorrection() { return LISTE_ZONE_CORRECTION;	}

	public static List<DefaultTaggues> getAllDefaultTaggues() { return LISTE_DEFAULT_TAGGUES; 	}

	public static Map<String, CodePays> getListeCentreIssn() { return LISTE_CENTRE_ISSN; }

	public static Map<String, String> getListeTypeDocument() { return LISTE_TYPE_DOCUMENT; }

	public static Map<String, String> getListeTypePublicationShort() { return LISTE_TYPE_PUBLICATION_SHORT; }

	public static Map<String, String> getListeTypePublication() { return LISTE_TYPE_PUBLICATION; }

	public static Map<String, String> getListeLibellePaysCodePays() { return LISTE_LIBELLEPAYS_CODEPAYS; }

	public static Map<String, List<String>> getListeFiltre() { return LISTE_FILTRE; }

	public static List<String> getCodePaysFr() { return CODE_PAYS_FR; }

	public static Map<String, CodePays> getCodePaysSorted() { return CODE_PAYS_SORTED; }

	public static Map<String, String> getColonnes() { return COLONNES; }

	public static Map<String, Integer> getProfiles() { return PROFILS; }

	public static String getPathMailMensuel() { return PATH_MAIL_MENSUEL;}

	public static boolean getSkipMailFlag() { return SKIP_MAIL_FLAG; }

}
