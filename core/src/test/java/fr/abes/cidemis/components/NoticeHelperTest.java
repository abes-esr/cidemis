package fr.abes.cidemis.components;

import fr.abes.cbs.process.ProcessCBS;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Properties;

class NoticeHelperTest {
    @MockBean
    private ProcessCBS cbs;
    private NoticeHelper noticeHelper = new NoticeHelper(cbs);

    private String cbsUrl;

    private String cbsPort;

    private String cbsLogin;

    private String ppn;

    private Properties prop;

//    @BeforeEach
//    void init() throws CBSException {
//        prop = new Properties();
//        try {
//            prop.load(NoticeHelperTest.class.getResource("/application.properties").openStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        cbsUrl = prop.getProperty("CBS_URL");
//        cbsPort = prop.getProperty("CBS_PORT");
//        cbsLogin = prop.getProperty("sudoc.login");
//        ppn = prop.getProperty("ppn");
//
//        cbs = new ProcessCBS();
//        noticeHelper = new NoticeHelper(cbs);
//    }


    // On teste les trois méthodes dans un seul test pour que la notice dans le sudoc soit inchangée à la fin du test
//    @Test
//    void setNoticeHelper() throws ZoneException, CBSException {
//        noticeHelper.ajoutZoneNotice(ppn, "301", "$a", "Nouvelle Valeur");
//        assertThat(noticeHelper.getNotice().equals(Constants.STR_1F +
//                "003 232799881\r" +
//                "004 341720001:18-09-20\r" +
//                "005 341720001:18-09-20 09:18:50.000\r" +
//                "006 341720001:18-09-20\r" +
//                "008 $aObx3\r" +
//                "00A $00\r" +
//                "00U $0utf8\r" +
//                "100 0#$a2015$d2015-...\r" +
//                "101 0#$afre\r" +
//                "102 ##$aFR\r" +
//                "104 ##$au$by$cy$dba$e0$ffre\r" +
//                "110 ##$ab$by$cu$f|$gu$hu\r" +
//                "135 ##$br\r" +
//                "200 1#$a@Statistiques de l'OCDE sur la productivité$bRessource électronique\r" +
//                "210 ##$aParis$cOCDE$d2015-\r" +
//                "301 ##$aNouvelle Valeur\r" +
//                "451 ##$0197213251\r" +
//                "488 ##$0166926604\r" +
//                "530 1#$a@Statistiques de l'OCDE sur la productivité$bEd. tableaux\r" +
//                "531 ##$a@Stat. OCDE product.$bEd. tableaux\r" +
//                "608 $aSéries cartographiques$2rameau\r" +
//                "675 ##$a658.53:311.3$v8th French Abbr. Ed\r" +
//                "859 4#$uhttp://www.oecd-ilibrary.org/industry-and-services/statistiques-de-l-ocde-sur-la-productivite_2414259x"
//                + Constants.STR_1E));
//        noticeHelper.modifierZoneNotice(ppn, "301", "$a", "Fait!");
//        assertThat(noticeHelper.getNotice().equals(Constants.STR_1F +
//                "003 232799881\r" +
//                "004 341720001:18-09-20\r" +
//                "005 341720001:18-09-20 09:18:50.000\r" +
//                "006 341720001:18-09-20\r" +
//                "008 $aObx3\r" +
//                "00A $00\r" +
//                "00U $0utf8\r" +
//                "100 0#$a2015$d2015-...\r" +
//                "101 0#$afre\r" +
//                "102 ##$aFR\r" +
//                "104 ##$au$by$cy$dba$e0$ffre\r" +
//                "110 ##$ab$by$cu$f|$gu$hu\r" +
//                "135 ##$br\r" +
//                "200 1#$a@Statistiques de l'OCDE sur la productivité$bRessource électronique\r" +
//                "210 ##$aParis$cOCDE$d2015-\r" +
//                "301 ##$aFait!\r" +
//                "451 ##$0197213251\r" +
//                "488 ##$0166926604\r" +
//                "530 1#$a@Statistiques de l'OCDE sur la productivité$bEd. tableaux\r" +
//                "531 ##$a@Stat. OCDE product.$bEd. tableaux\r" +
//                "675 ##$a658.53:311.3$v8th French Abbr. Ed\r" +
//                "859 4#$uhttp://www.oecd-ilibrary.org/industry-and-services/statistiques-de-l-ocde-sur-la-productivite_2414259x"
//                + Constants.STR_1E));
//        noticeHelper.chercherEtSupprimerZoneNotice(ppn, "301", "$a", "Fait!");
//        assertThat(noticeHelper.getNotice().equals(Constants.STR_1F +
//                "003 232799881\r" +
//                "004 341720001:18-09-20\r" +
//                "005 341720001:18-09-20 09:18:50.000\r" +
//                "006 341720001:18-09-20\r" +
//                "008 $aObx3\r" +
//                "00A $00\r" +
//                "00U $0utf8\r" +
//                "100 0#$a2015$d2015-...\r" +
//                "101 0#$afre\r" +
//                "102 ##$aFR\r" +
//                "104 ##$au$by$cy$dba$e0$ffre\r" +
//                "110 ##$ab$by$cu$f|$gu$hu\r" +
//                "135 ##$br\r" +
//                "200 1#$a@Statistiques de l'OCDE sur la productivité$bRessource électronique\r" +
//                "210 ##$aParis$cOCDE$d2015-\r" +
//                "451 ##$0197213251\r" +
//                "488 ##$0166926604\r" +
//                "530 1#$a@Statistiques de l'OCDE sur la productivité$bEd. tableaux\r" +
//                "531 ##$a@Stat. OCDE product.$bEd. tableaux\r" +
//                "675 ##$a658.53:311.3$v8th French Abbr. Ed\r" +
//                "859 4#$uhttp://www.oecd-ilibrary.org/industry-and-services/statistiques-de-l-ocde-sur-la-productivite_2414259x"
//                + Constants.STR_1E));
//    }




}