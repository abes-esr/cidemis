package fr.abes.cidemis.model;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.model.cidemis.CidemisNotices;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CidemisNoticeTest {
    @Test
    public void testStatutVie() {
        CidemisNotices notice = new CidemisNotices();
        notice.setDateFin(null);
        assertThat(notice.getStatutdevie()).isEqualTo(false);
        notice.setDateFin("01/01/20");
        assertThat(notice.getStatutdevie()).isEqualTo(true);
        notice.setDateFin("");
        assertThat(notice.getStatutdevie()).isEqualTo(false);
    }

    @Test
    public void testGetProfilIdCollection() {
        CidemisNotices notice = new CidemisNotices();
        notice.setTypeDeRessourceContinue("b");
        assertEquals(notice.getIdprofil(), Constant.PROFIL_COLLECTION.intValue());
    }

    @Test
    public void testGetProfilIdPerioElec() {
        CidemisNotices notice = new CidemisNotices();
        notice.setTypeDeRessourceContinue("a");
        notice.setTypeDocument("l");
        assertEquals(notice.getIdprofil(), Constant.PROFIL_PERIODIQUE_ELECTRONIQUE.intValue());
    }

    @Test
    public void testGetIdProfilPerioImprime() {
        CidemisNotices notices = new CidemisNotices();
        notices.setTypeDeRessourceContinue("a");
        notices.setTypeDocument("x");
        notices.setDateDebut("1830");
        assertEquals(notices.getIdprofil(), Constant.PROFIL_PERIODIQUE_IMPRIME.intValue());
    }

    @Test
    public void testGetIdProfilPerioImprimeVivant() {
        CidemisNotices notices = new CidemisNotices();
        notices.setTypeDeRessourceContinue("a");
        notices.setTypeDocument("x");
        notices.setDateDebut("1965");
        assertEquals(notices.getIdprofil(), Constant.PROFIL_PERIODIQUE_IMPRIME_VIVANT.intValue());
        notices.setDateDebut("1960");
        assertNotEquals(notices.getIdprofil(), Constant.PROFIL_PERIODIQUE_IMPRIME_VIVANT.intValue());
    }

    @Test
    public void testGetIdProfilPerioImprimeMort() {
        CidemisNotices notices = new CidemisNotices();
        notices.setTypeDeRessourceContinue("a");
        notices.setTypeDocument("x");
        notices.setDateDebut("1930");
        assertEquals(notices.getIdprofil(), Constant.PROFIL_PERIODIQUE_IMPRIME_MORT.intValue());
        notices.setDateDebut("183X");
        assertEquals(notices.getIdprofil(), Constant.PROFIL_PERIODIQUE_IMPRIME_MORT.intValue());
        notices.setDateDebut("184X");
        assertEquals(notices.getIdprofil(), Constant.PROFIL_PERIODIQUE_IMPRIME_MORT.intValue());
        notices.setDateDebut("18XX");
        assertEquals(notices.getIdprofil(), Constant.PROFIL_PERIODIQUE_IMPRIME_MORT.intValue());
        notices.setDateDebut("1965");
        notices.setDateFin("2000");
        assertEquals(notices.getIdprofil(), Constant.PROFIL_PERIODIQUE_IMPRIME_MORT.intValue());
    }

    @Test
    public void testGetIdProfilIndetermine() {
        CidemisNotices notices = new CidemisNotices();
        notices.setTypeDeRessourceContinue("a");
        notices.setTypeDocument("x");
        notices.setDateDebut("");
        assertEquals(notices.getIdprofil(), Constant.PROFIL_INDETERMINE.intValue());
    }

    @Test
    void testErreurBNFGala15252() {
        CidemisNotices notices = new CidemisNotices();
        notices.setDateDebut("201X");
        notices.setTypeDeRessourceContinue("a");
        notices.setTypeDocument("as");
        System.out.println(notices.getIdprofil());

    }
}
