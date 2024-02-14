package fr.abes.cidemis.localisation;

import fr.abes.cidemis.web.HtmlEntities;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Fournit les fonctions nécessaires pour retrouver la chaine localisée voulue
 * @author Olivier Combe
 */
@Slf4j
public class LocalProvider {
	private static final String RESSOURCE_FILE = "MessageResources";
    private Locale locale;

    /**
     * Constructeur pour définir la langue par défaut du LocalProvider instancié
     * @param locale
     */
    public LocalProvider(Locale locale) {
        this.locale = locale;
    }

    /**
     * Permet de changer la langue par défaut de la machine virtuelle
     * @param locale
     */
    public static void changeDefaultLocale(Locale locale) {
        Locale.setDefault(locale);
    }

    protected static ClassLoader getCurrentClassLoader(Object defaultObject) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        if (loader == null) {
            loader = defaultObject.getClass().getClassLoader();
        }

        return loader;
    }

    /**
     * Permet de retrouver une chaîne localisée
     * @param bundleName, le chemin et préfixe des fichiers properties contenant les textes localisés
     * @param key, la clé d'accès à la ressource localisée
     * @param params, un tableau de paramètres à injecter dans la chaîne localisée
     * @param locale, la langue choisie
     * @return
     */
    private static String getMessageResourceString(String bundleName, String key, Object[] params, Locale locale) {
        String text = null;

        try {
            ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale, getCurrentClassLoader(params));
            text = bundle.getString(key);
        }
        catch (MissingResourceException e) {
        	log.info("?? key " + key + " not found ??", e);
            text = "?? key " + key + " not found ??";
        }

        if (params != null) {
            MessageFormat mf = new MessageFormat(text, locale);
            text = mf.format(params, new StringBuffer(), null).toString();
        }
       
        return text;
    }

    /**
     * Récupère la chaîne localisée pour la langue par défaut (ou définie via le constructeur)
     * @param key, la clé d'accès à la ressource localisée
     * @return
     */
    public String getMsg(String key) {
        String text = getMessageResourceString(LocalProvider.RESSOURCE_FILE, key, null, locale);
        return HtmlEntities.htmlentities(text);
    }

    /**
     * Récupère la chaîne localisée pour la langue par défaut (ou définie via le constructeur)
     * @param key, la clé d'accès à la ressource localisée
     * @return
     */
    public String getMsgNoSingleQuote(String key) {
        String text = getMessageResourceString(LocalProvider.RESSOURCE_FILE, key, null, locale);
        return HtmlEntities.htmlSingleQuotes(text);
    }

    /**
     * Récupère la chaîne localisée pour la langue par défaut (ou définie via le constructeur)
     * @param key, la clé d'accès à la ressource localisée
     * @return
     */
    public String getMsgNoHtmlEntities(String key) {
        return getMessageResourceString(LocalProvider.RESSOURCE_FILE, key, null, locale);
    }

    /**
     * Récupère la chaîne localisée pour la langue par défaut (ou définie via le constructeur)
     * et injecte les paramètres dans la chaîne
     * @param key, la clé d'accès à la ressource localisée
     * @param params
     * @return
     */
    public String getMsg(String key, Object[] params) {
        return getMessageResourceString(LocalProvider.RESSOURCE_FILE, key, params, locale);
    }

    /**
     * Récupère la chaîne localisée pour la langue choisie
     * et injecte les paramètres dans la chaîne
     * @param key, la clé d'accès à la ressource localisée
     * @param params
     * @param locale
     * @return
     */
    public String getMsg(String key, Object[] params, Locale locale) {
        return getMessageResourceString(LocalProvider.RESSOURCE_FILE, key, params, locale);
    }
}