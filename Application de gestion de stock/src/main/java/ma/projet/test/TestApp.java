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
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestApp {

    /**
     * Méthode utilitaire pour obtenir une connexion SQL directe via JDBC DriverManager
     */
    public static Connection getDirectSqlConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/gestion_stock?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }

    public static void main(String[] args) {
        try {
            System.out.println("=================================================");
            System.out.println(" 0. VÉRIFICATION DE LA CONNEXION SQL");
            System.out.println("=================================================");

            // Test 1 : Connexion SQL directe via JDBC standard
            try (Connection directConn = getDirectSqlConnection()) {
                if (directConn != null && !directConn.isClosed()) {
                    System.out.println("[JDBC Direct] Connexion SQL réussie !");
                    System.out.println("  -> URL JDBC  : " + directConn.getMetaData().getURL());
                    System.out.println("  -> Base      : " + directConn.getCatalog());
                    System.out.println("  -> Utilisateur: " + directConn.getMetaData().getUserName());
                }
            } catch (SQLException e) {
                System.err.println("[JDBC Direct] Erreur de connexion SQL : " + e.getMessage());
            }

            // Test 2 : Connexion SQL via la Session Hibernate
            Session s = HibernateUtil.getSessionFactory().openSession();
            s.doWork(new Work() {
                @Override
                public void execute(Connection connection) throws SQLException {
                    if (connection != null && !connection.isClosed()) {
                        DatabaseMetaData meta = connection.getMetaData();
                        System.out.println("[Hibernate SQL] Connexion SQL active !");
                        System.out.println("  -> SGBD      : " + meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());
                        System.out.println("  -> Driver    : " + meta.getDriverName() + " " + meta.getDriverVersion());
                    }
                }
            });

            // Nettoyage préalable pour des résultats de démonstration propres et reproductibles
            Transaction tx = s.beginTransaction();
            s.createQuery("delete from LigneCommandeProduit").executeUpdate();
            s.createQuery("delete from Produit").executeUpdate();
            s.createQuery("delete from Commande").executeUpdate();
            s.createQuery("delete from Categorie").executeUpdate();
            tx.commit();
            s.close();

            // Initialisation des services
            CategorieService cs = new CategorieService();
            ProduitService ps = new ProduitService();
            CommandeService cmds = new CommandeService();
            LigneCommandeService lcs = new LigneCommandeService();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            System.out.println("\n=================================================");
            System.out.println(" 1. CRÉATION DES CATÉGORIES ET PRODUITS");
            System.out.println("=================================================");
            Categorie cInformatique = new Categorie("CAT01", "Matériel Informatique");
            Categorie cAccessoires = new Categorie("CAT02", "Accessoires");
            cs.create(cInformatique);
            cs.create(cAccessoires);

            // Produits correspondants à l'exemple du sujet
            Produit p1 = new Produit("ES12", 120.0f, cInformatique);
            Produit p2 = new Produit("ZR85", 100.0f, cInformatique);
            Produit p3 = new Produit("EE85", 200.0f, cInformatique);
            Produit p4 = new Produit("MS01", 50.0f, cAccessoires);

            ps.create(p1);
            ps.create(p2);
            ps.create(p3);
            ps.create(p4);

            System.out.println("Catégories et produits créés avec succès.\n");

            System.out.println("=================================================");
            System.out.println(" 2. CRÉATION DES COMMANDES ET LIGNES DE COMMANDE");
            System.out.println("=================================================");
            // Commande du 14 Mars 2013
            Date dateCmd = sdf.parse("2013-03-14");
            Commande cmd1 = new Commande(dateCmd);
            cmds.create(cmd1);

            // Lignes de commande pour cmd1 (exactement les données de l'exemple du sujet)
            LigneCommandeProduit l1 = new LigneCommandeProduit(cmd1, p1, 7);
            LigneCommandeProduit l2 = new LigneCommandeProduit(cmd1, p2, 14);
            LigneCommandeProduit l3 = new LigneCommandeProduit(cmd1, p3, 5);
            lcs.create(l1);
            lcs.create(l2);
            lcs.create(l3);

            // Une deuxième commande pour tester le filtre entre deux dates
            Commande cmd2 = new Commande(sdf.parse("2013-05-20"));
            cmds.create(cmd2);
            LigneCommandeProduit l4 = new LigneCommandeProduit(cmd2, p4, 2);
            lcs.create(l4);

            System.out.println("Commandes et lignes créées avec succès.\n");

            System.out.println("=================================================");
            System.out.println(" 3. TEST : PRODUITS PAR CATÉGORIE");
            System.out.println("=================================================");
            ps.afficherProduitsParCategorie(cInformatique);

            System.out.println("=================================================");
            System.out.println(" 4. TEST : PRODUITS COMMANDÉS DANS UNE COMMANDE");
            System.out.println("=================================================");
            ps.afficherProduitsCommandes(cmd1);

            System.out.println("=================================================");
            System.out.println(" 5. TEST : PRODUITS COMMANDÉS ENTRE DEUX DATES");
            System.out.println("=================================================");
            Date debut = sdf.parse("2013-01-01");
            Date fin = sdf.parse("2013-04-01");
            ps.afficherProduitsCommandesEntreDates(debut, fin);

            System.out.println("=================================================");
            System.out.println(" 6. TEST : PRODUITS DONT LE PRIX > 100 DH (REQUÊTE NOMMÉE)");
            System.out.println("=================================================");
            ps.afficherProduitsPrixSuperieurA100();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}
