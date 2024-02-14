package fr.abes.cidemis.web;

public class MyDispatcher {
	public static final String ERREUR_LOGINJSP = "error_login";
	public static final String ERREUR_500JSP = "error_500";
    public static final String LISTE_DEMANDESJSP = "liste-demandes";
    public static final String LOGINJSP = "login";
    public static final String INDEXJSP = "index";
    public static final String CHERCHERNOTICEJSP = "chercher-notice";
    public static final String CHOIXDEMANDEJSP = "choix-demande";
    public static final String CREATIONDEMANDEJSP = "creation-demande";
    public static final String RESEND_FORMJSP = "resend-form";
    public static final String AFFICHER_DEMANDEJSP = "afficher-demande";
    public static final String REDIRECTJSP = "redirect";
    public static final String AUCUNENOTICEJSP = "aucune-notice";
    public static final String NOTICESUPPRIMEEJSP = "notice_supprimee";
    public static final String PPNINCORRECTJSP = "ppn-incorrect";
    public static final String ISSNEXISTANTJSP = "issn-existant";
    public static final String NOTICEBIBLIOJSP = "notice-biblio";
    public static final String GESTIONPROFILJSP = "gestion-profil";
    public static final String LOGOUTAJAX = "logout-ajax";
    public static final String PIECEJOINTE = "piece-jointe";
    public static final String COMMENTAIRE = "commentaire";
    public static final String UPLOADCIEPS = "upload-cieps";
    public static final String VERIFNOTICEDEMANDEJSP = "verifnoticedemande";
    public static final String ERRORNOTICEJSP = "error_notice";
    public static final String ERROREXCEPTION = "error_exception";

    public static final String ERREUR = "/error";
    public static final String LOGIN = "/login";
    public static final String LISTE_DEMANDES = "/liste-demandes";
    public static final String LOGOUT = "/logout";
    public static final String CHOIX_DEMANDE = "/choix-demande";
    public static final String CREATION_DEMANDE = "/creation-demande";
    public static final String AFFICHER_DEMANDE = "/afficher-demande";
    public static final String VERIFIER_PPN = "/verifier-ppn";
    public static final String VERIFIER_ISSN = "/verifier-issn";
    public static final String VERIFIER_NOTICE = "/verifier-notice";
    
    private MyDispatcher() {
    	
    }
}
