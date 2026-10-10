package ma.projet.test;

import ma.projet.classes.Categorie;
import ma.projet.classes.Commande;
import ma.projet.classes.LigneCommandeProduit;
import ma.projet.classes.Produit;
import ma.projet.service.CategorieService;
import ma.projet.service.CommandeService;
import ma.projet.service.LigneCommandeService;
import ma.projet.service.ProduitService;
import ma.projet.util.HibernateUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class ServiceTest {

    private static CategorieService categorieService;
    private static ProduitService produitService;
    private static CommandeService commandeService;
    private static LigneCommandeService ligneCommandeService;

    @BeforeClass
    public static void setUp() {
        categorieService = new CategorieService();
        produitService = new ProduitService();
        commandeService = new CommandeService();
        ligneCommandeService = new LigneCommandeService();
    }

    @AfterClass
    public static void tearDown() {
        HibernateUtil.shutdown();
    }

    @Test
    public void testGestionStockComplet() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // 1. Création de catégories
        Categorie catInfo = new Categorie("INFO", "Informatique");
        assertTrue(categorieService.create(catInfo));
        assertNotNull(categorieService.findById(catInfo.getId()));

        // 2. Création de produits
        Produit p1 = new Produit("ES12", 120.0f, catInfo);
        Produit p2 = new Produit("ZR85", 100.0f, catInfo);
        Produit p3 = new Produit("EE85", 200.0f, catInfo);
        assertTrue(produitService.create(p1));
        assertTrue(produitService.create(p2));
        assertTrue(produitService.create(p3));

        // 3. Test recherche par catégorie
        List<Produit> produitsCat = produitService.findByCategorie(catInfo);
        assertTrue(produitsCat.size() >= 3);

        // 4. Test création commande & lignes de commande
        Date dateCmd = sdf.parse("2013-03-14");
        Commande cmd = new Commande(dateCmd);
        assertTrue(commandeService.create(cmd));

        LigneCommandeProduit l1 = new LigneCommandeProduit(cmd, p1, 7);
        LigneCommandeProduit l2 = new LigneCommandeProduit(cmd, p2, 14);
        LigneCommandeProduit l3 = new LigneCommandeProduit(cmd, p3, 5);
        assertTrue(ligneCommandeService.create(l1));
        assertTrue(ligneCommandeService.create(l2));
        assertTrue(ligneCommandeService.create(l3));

        List<LigneCommandeProduit> lignes = ligneCommandeService.findByCommande(cmd.getId());
        assertEquals(3, lignes.size());

        // 5. Test recherche entre deux dates
        Date d1 = sdf.parse("2013-01-01");
        Date d2 = sdf.parse("2013-12-31");
        List<Produit> produitsDates = produitService.findProduitsCommandesEntreDates(d1, d2);
        assertFalse(produitsDates.isEmpty());

        // 6. Test requête nommée (prix > 100)
        List<Produit> produitsChers = produitService.findPrixSuperieurA100();
        assertFalse(produitsChers.isEmpty());
        for (Produit p : produitsChers) {
            assertTrue(p.getPrix() > 100.0f);
        }
    }
}
