package fr.abes.cidemis.components;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.dao.CodePaysDao;
import fr.abes.cidemis.model.CodePays;
import fr.abes.cidemis.model.cidemis.DefaultTaggues;
import fr.abes.cidemis.model.cidemis.ZoneCorrection;
import fr.abes.cidemis.service.CidemisManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CidemisInitializer {
    private static Map<String, CodePays> codePaysListe;
    private static Date codePaysDate;
    @Value("${code.pays.url}")
    private String urlCodePays;
    @Value("${psi.lien.perenne}")
    private String lienPerenne;
    @Value("${psi.lien.perenne.issn}")
    private String lienPerenneIssn;
    @Value("${path.mail.mensuel}")
    private String pathMailMensuel;
    @Value("${mail.ws.skip}")
    private boolean skipMailFlag;
    @Autowired
    private CidemisManageService service;

    @EventListener
    public void afterPropertiesSet(ContextRefreshedEvent event)  {
        for (Map.Entry<String, CodePays> code_pays : getCodePays().entrySet()) {
            Constant.LISTE_LIBELLEPAYS_CODEPAYS.put(code_pays.getValue().getPays(), code_pays.getKey());
        }
        Constant.CODE_PAYS_SORTED.putAll(getCodePays());
        Constant.LISTE_CENTRE_ISSN.putAll(getListeCodeISSNCodePays());
        Constant.COLONNES.putAll(getColonnes());
        Constant.PROFILS.putAll(getProfils());
        Constant.CODE_PAYS_FR.addAll(getCodesPaysFr());
        Constant.LISTE_TYPE_PUBLICATION.putAll(getTypesPublication());
        Constant.LISTE_TYPE_PUBLICATION_SHORT.putAll(getTypesPublicationShort());
        Constant.LISTE_TYPE_DOCUMENT.putAll(getTypesDocuments());
        Constant.LISTE_FILTRE.putAll(getListeFiltre());
        Constant.PATH_MAIL_MENSUEL = pathMailMensuel;
        Constant.SKIP_MAIL_FLAG = skipMailFlag;
        Constant.PROPERTIES_PSI_LIEN_PERENNE = lienPerenne;
        Constant.PROPERTIES_PSI_LIEN_PERENNE_ISSN = lienPerenneIssn;
        Constant.LISTE_DEFAULT_TAGGUES.addAll(getDefaultTaggues());
        Constant.LISTE_ZONE_CORRECTION.addAll(getZonesCorrection());
    }

    private Map<String, CodePays> getListeCodeISSNCodePays() {
        Map<String, CodePays> liste = new TreeMap<>();
        for (Map.Entry<String, CodePays> code_pays : getCodePays().entrySet()) {
            liste.put(code_pays.getValue().getCodecentreissn(), code_pays.getValue());
        }
        return liste;
    }

    /**
     * Récupère la liste des correspondances CODE_PAYS -> CENTRE ISSN Cette
     * liste est mise à jour toutes les 5h
     *
     * @return
     */
    private Map<String, CodePays> getCodePays() {
        // Si on n'a pas encore téléchargé la liste, on le fait maintenant en
        // enregistrant la date
        if (codePaysDate == null) {
            codePaysListe = (new CodePaysDao()).getAllCodePays(urlCodePays);

            if (!codePaysListe.isEmpty()) {
                codePaysDate = new Date();
            }
        }
        // Si la liste a été récupérée depuis plus de 5h, alors on la met à jour
        else if ((((new Date()).getTime() - codePaysDate.getTime()) / 36000000) > 5) {
            Map<String, CodePays> listeTemp = (new CodePaysDao()).getAllCodePays(urlCodePays);

            if (!listeTemp.isEmpty()) {
                codePaysDate = new Date();
            }
        }

        return codePaysListe;
    }

    private Map<String, String> getColonnes() {
        Map<String, String> mapColonnes = new HashMap();
        mapColonnes.put(Constant.COL_DATE, Constant.COLTYPE_INPUT);
        mapColonnes.put(Constant.COL_DATE_MODIF, Constant.COLTYPE_INPUT);
        mapColonnes.put(Constant.COL_DEMANDE_TYPE, Constant.COLTYPE_SELECT);
        mapColonnes.put(Constant.COL_DEMANDE_NUM, Constant.COLTYPE_INPUT);
        mapColonnes.put(Constant.COL_PPN, Constant.COLTYPE_INPUT);
        mapColonnes.put(Constant.COL_TITRE, Constant.COLTYPE_INPUT);
        mapColonnes.put(Constant.COL_ETAT, Constant.COLTYPE_SELECT);
        mapColonnes.put(Constant.COL_CENTRE_REGIONAL, Constant.COLTYPE_INPUT);
        mapColonnes.put(Constant.COL_ISSN, Constant.COLTYPE_INPUT);
        mapColonnes.put(Constant.COL_FRBNF, Constant.COLTYPE_INPUT);
        mapColonnes.put(Constant.COL_PUBLICATION_TYPE, Constant.COLTYPE_SELECT);
        mapColonnes.put(Constant.COL_SUPPORT_TYPE, Constant.COLTYPE_SELECT);
        mapColonnes.put(Constant.COL_PUBLICATION_PAYS, Constant.COLTYPE_SELECT);
        mapColonnes.put(Constant.COL_STATUT_DE_VIE, Constant.COLTYPE_SELECT);
        mapColonnes.put(Constant.COL_PUBLICATION_DATE, Constant.COLTYPE_INPUT);
        mapColonnes.put(Constant.COL_PIECES_JUSTIF, Constant.COLTYPE_INPUT);
        mapColonnes.put(Constant.COL_RCR, Constant.COLTYPE_INPUT);
        mapColonnes.put(Constant.COL_TAGGUE, Constant.COLTYPE_INPUT);
        mapColonnes.put(Constant.COL_ACTION, Constant.COLTYPE_NONE);

        return mapColonnes;
    }

    private Map<String, List<String>> getListeFiltre() {
        Map<String, List<String>> listeFiltre = new HashMap<>();

        listeFiltre.put(Constant.COL_DEMANDE_TYPE, new ArrayList(Arrays.asList("COR", "CRE", "NUM")));
        listeFiltre.put(Constant.COL_ETAT, service.getReference().findAllEtatsdemandesLib());
        listeFiltre.put(Constant.COL_PUBLICATION_TYPE, new ArrayList(Constant.LISTE_TYPE_PUBLICATION.values()));
        listeFiltre.put(Constant.COL_SUPPORT_TYPE, new ArrayList(Constant.LISTE_TYPE_DOCUMENT.values()));
        listeFiltre.put(Constant.COL_PUBLICATION_PAYS, new ArrayList(Constant.CODE_PAYS_SORTED.keySet()));
        listeFiltre.put(Constant.COL_STATUT_DE_VIE, new ArrayList(Arrays.asList("Mort", "Vivant")));

        return listeFiltre;
    }

    private Map<String, Integer> getProfils() {
        Map<String, Integer> mapProfils = new HashMap<>();
        mapProfils.put(Constant.PROFIL_INDETERMINE_TXT, Constant.PROFIL_INDETERMINE);
        mapProfils.put(Constant.PROFIL_TOUTES_DEMANDES_TXT, Constant.PROFIL_TOUTES_DEMANDES);
        mapProfils.put(Constant.PROFIL_COLLECTION_TXT, Constant.PROFIL_COLLECTION);
        mapProfils.put(Constant.PROFIL_PERIODIQUE_ELECTRONIQUE_TXT, Constant.PROFIL_PERIODIQUE_ELECTRONIQUE);
        mapProfils.put(Constant.PROFIL_PERIODIQUE_IMPRIME_TXT, Constant.PROFIL_PERIODIQUE_IMPRIME);
        mapProfils.put(Constant.PROFIL_PERIODIQUE_IMPRIME_VIVANT_TXT, Constant.PROFIL_PERIODIQUE_IMPRIME_VIVANT);
        mapProfils.put(Constant.PROFIL_PERIODIQUE_IMPRIME_MORT_TXT, Constant.PROFIL_PERIODIQUE_IMPRIME_MORT);
        mapProfils.put(Constant.PROFIL_PAS_PROFIL_TXT, Constant.PROFIL_PAS_PROFIL);
        return mapProfils;
    }

    private static List<String> getCodesPaysFr() {
        List<String> listePays = new ArrayList<>();
        listePays.add("FR");
        listePays.add("GF");
        listePays.add("GP");
        listePays.add("MQ");
        listePays.add("NC");
        listePays.add("PF");
        listePays.add("PM");
        listePays.add("RE");
        listePays.add("SM");
        listePays.add("WF");
        listePays.add("YT");
        return listePays;
    }

    private Map<String, String> getTypesPublication() {
        Map<String, String> mapTypesPublication = new HashMap<>();
        mapTypesPublication.put("a", "Périodique");
        mapTypesPublication.put("b", "Collection de monographies");
        mapTypesPublication.put("c", "Journal");
        mapTypesPublication.put("e", "Publication à feuillets mobiles");
        mapTypesPublication.put("f", "Base de données à mise à jour");
        mapTypesPublication.put("g", "Site web à mise à jour");
        mapTypesPublication.put("z", "Type Indéterminé");
        return mapTypesPublication;
    }

    private Map<String, String> getTypesPublicationShort() {
        Map<String, String> mapTypesPublication = new HashMap<>();
        mapTypesPublication.put("a", "PER");
        mapTypesPublication.put("b", "COL");
        mapTypesPublication.put("c", "PER");
        mapTypesPublication.put("e", "PER");
        mapTypesPublication.put("f", "PER");
        mapTypesPublication.put("g", "PER");
        mapTypesPublication.put("z", "Type Indéterminé");
        return mapTypesPublication;
    }

    private Map<String, String> getTypesDocuments() {
        Map<String, String> mapTypesDocuments = new HashMap<>();
        mapTypesDocuments.put("a", "Imprimé");
        mapTypesDocuments.put("g", "Audiovisuel");
        mapTypesDocuments.put("b", "Manuscrit");
        mapTypesDocuments.put("j", "Enregistrement sonore musical");
        mapTypesDocuments.put("k", "Image fixe");
        mapTypesDocuments.put("e", "Carte");
        mapTypesDocuments.put("d", "Partition manuscrite");
        mapTypesDocuments.put("c", "Partition");
        mapTypesDocuments.put("i", "Enregistrement sonore non musical");
        mapTypesDocuments.put("l", "Electronique");
        mapTypesDocuments.put("f", "Carte manuscrite");
        mapTypesDocuments.put("r", "Objet");
        mapTypesDocuments.put("m", "Multimédia multisupport");
        return mapTypesDocuments;
    }

    private List<DefaultTaggues> getDefaultTaggues() {
        List<DefaultTaggues> listDefaultTag = new ArrayList<>();
        listDefaultTag.addAll(service.getTaggues().findAllDefaultTaggues());
        return listDefaultTag;
    }

    private List<ZoneCorrection> getZonesCorrection() {
        List<ZoneCorrection> listeZoneCorr = new ArrayList<>();
        listeZoneCorr.addAll(service.getReference().findAllZonesCorrection());
        return listeZoneCorr;
    }
}
