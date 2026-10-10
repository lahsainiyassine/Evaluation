package ma.projet.test;

import ma.projet.beans.Femme;
import ma.projet.beans.Homme;
import ma.projet.service.FemmeService;
import ma.projet.service.HommeService;
import ma.projet.service.MariageService;
import ma.projet.util.HibernateUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class ServiceTest {

    private static HommeService hommeService;
    private static FemmeService femmeService;
    private static MariageService mariageService;
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private static Date d(String s) {
        try {
            return sdf.parse(s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeClass
    public static void setUp() {
        hommeService = new HommeService();
        femmeService = new FemmeService();
        mariageService = new MariageService();
        TestApp.main(new String[0]);
    }

    @AfterClass
    public static void tearDown() {
        HibernateUtil.shutdown();
    }

    @Test
    public void testFindAllFemmes() {
        List<Femme> femmes = femmeService.findAll();
        assertNotNull(femmes);
        assertEquals(10, femmes.size());
    }

    @Test
    public void testFemmePlusAgee() {
        Femme plusAgee = femmeService.findFemmePlusAgee();
        assertNotNull(plusAgee);
        assertEquals("ZAHRA", plusAgee.getNom());
        assertEquals("Fatima", plusAgee.getPrenom());
    }

    @Test
    public void testEpousesEntreDates() {
        Homme h1 = hommeService.findAll().stream()
                .filter(h -> "SAFI".equals(h.getNom()))
                .findFirst().orElse(null);
        assertNotNull(h1);

        List<Femme> epouses = hommeService.findEpousesEntreDates(h1, d("01/01/1990"), d("31/12/2000"));
        assertNotNull(epouses);
        assertEquals(3, epouses.size());
    }

    @Test
    public void testCountEnfantsEntreDatesNativeQuery() {
        Femme rami = femmeService.findAll().stream()
                .filter(f -> "RAMI".equals(f.getNom()))
                .findFirst().orElse(null);
        assertNotNull(rami);

        int count = femmeService.countEnfantsEntreDates(rami, d("01/01/1985"), d("31/12/1995"));
        assertEquals(5, count);
    }

    @Test
    public void testFemmesMarieesAuMoinsDeuxFoisNamedQuery() {
        List<Femme> femmes = femmeService.findFemmesMarieesAuMoinsDeuxFois();
        assertNotNull(femmes);
        assertEquals(2, femmes.size());
    }

    @Test
    public void testHommesMariesQuatreFemmesCriteria() {
        List<Homme> hommes = hommeService.findHommesMariesQuatreFemmesCriteria(d("01/01/1990"), d("31/12/2010"));
        assertNotNull(hommes);
        assertEquals(1, hommes.size());
        assertEquals("SAFI", hommes.get(0).getNom());
    }
}
