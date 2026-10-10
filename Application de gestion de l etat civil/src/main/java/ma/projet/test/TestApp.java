package ma.projet.test;

import ma.projet.beans.Femme;
import ma.projet.beans.Homme;
import ma.projet.beans.Mariage;
import ma.projet.service.FemmeService;
import ma.projet.service.HommeService;
import ma.projet.service.MariageService;
import ma.projet.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestApp {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private static Date parseDate(String dateStr) {
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void cleanDatabase() {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.createQuery("delete from Mariage").executeUpdate();
            session.createQuery("delete from Femme").executeUpdate();
            session.createQuery("delete from Homme").executeUpdate();
            session.createQuery("delete from Personne").executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
        } finally {
            if (session != null) session.close();
        }
    }

    public static void main(String[] args) {
        // Nettoyage préalable pour que le programme soit réexécutable à tout moment
        cleanDatabase();

        HommeService hommeService = new HommeService();
        FemmeService femmeService = new FemmeService();
        MariageService mariageService = new MariageService();

        System.out.println("================================================================================");
        System.out.println("                  INITIALISATION ET INSERTION DES DONNÉES                       ");
        System.out.println("================================================================================");

        // 1. Création de 10 femmes
        Femme f1 = new Femme("RAMI", "Salima", "0611223344", "Casablanca, Maarif", parseDate("10/05/1970"));
        Femme f2 = new Femme("ALI", "Amal", "0622334455", "Rabat, Agdal", parseDate("15/08/1972"));
        Femme f3 = new Femme("ALAOUI", "Wafa", "0633445566", "Fès, Medina", parseDate("20/01/1975"));
        Femme f4 = new Femme("ALAMI", "Karima", "0644556677", "Marrakech, Guéliz", parseDate("12/03/1968"));
        Femme f5 = new Femme("ZAHRA", "Fatima", "0655667788", "Tanger, Malabata", parseDate("01/01/1960")); // La plus âgée
        Femme f6 = new Femme("BENNANI", "Najat", "0666778899", "Meknès, Hamria", parseDate("14/07/1980"));
        Femme f7 = new Femme("TAZI", "Samira", "0677889900", "Kenitra, Centre", parseDate("22/09/1982"));
        Femme f8 = new Femme("CHRAIBI", "Houda", "0688990011", "Agadir, Talborjt", parseDate("05/11/1985"));
        Femme f9 = new Femme("FASSI", "Meriem", "0699001122", "Oujda, Al Qods", parseDate("18/02/1988"));
        Femme f10 = new Femme("IDRISSI", "Souad", "0610203040", "Tétouan, Wilaya", parseDate("30/04/1992"));

        femmeService.create(f1);
        femmeService.create(f2);
        femmeService.create(f3);
        femmeService.create(f4);
        femmeService.create(f5);
        femmeService.create(f6);
        femmeService.create(f7);
        femmeService.create(f8);
        femmeService.create(f9);
        femmeService.create(f10);

        // 2. Création de 5 hommes
        Homme h1 = new Homme("SAFI", "Said", "0601020304", "Casablanca, Anfa", parseDate("05/04/1965"));
        Homme h2 = new Homme("ALAMI", "Ahmed", "0602030405", "Rabat, Hassan", parseDate("12/06/1970"));
        Homme h3 = new Homme("CHERKAOUI", "Hassan", "0603040506", "Marrakech, Hivernage", parseDate("25/11/1975"));
        Homme h4 = new Homme("TAHIRI", "Omar", "0604050607", "Fès, Atlas", parseDate("08/09/1980"));
        Homme h5 = new Homme("BENJELLOUN", "Youssef", "0605060708", "Tanger, Marshan", parseDate("19/12/1982"));

        hommeService.create(h1);
        hommeService.create(h2);
        hommeService.create(h3);
        hommeService.create(h4);
        hommeService.create(h5);

        // 3. Création des mariages
        // Pour SAFI Said (h1) : correspond à l'exemple d'affichage
        // - Mariage échoué avec KARIMA ALAMI : 03/09/1989 -> 03/09/1990, 0 enfant
        mariageService.create(new Mariage(parseDate("03/09/1989"), parseDate("03/09/1990"), 0, h1, f4));
        // - Mariages en cours :
        // 1. SALIMA RAMI : Début 03/09/1990, 4 enfants
        mariageService.create(new Mariage(parseDate("03/09/1990"), null, 4, h1, f1));
        // 2. AMAL ALI : Début 03/09/1995, 2 enfants
        mariageService.create(new Mariage(parseDate("03/09/1995"), null, 2, h1, f2));
        // 3. WAFA ALAOUI : Début 04/11/2000, 3 enfants
        mariageService.create(new Mariage(parseDate("04/11/2000"), null, 3, h1, f3));
        // 4. NAJAT BENNANI : Début 15/06/2005, 1 enfant (4ème épouse entre 1990 et 2010 !)
        mariageService.create(new Mariage(parseDate("15/06/2005"), null, 1, h1, f6));

        // Autres mariages pour permettre de tester tous les cas :
        // - KARIMA ALAMI (f4) remariée avec ALAMI Ahmed (h2) -> 2ème mariage pour Karima
        mariageService.create(new Mariage(parseDate("10/05/1992"), null, 2, h2, f4));
        // - SALIMA RAMI (f1) avait un premier mariage avant avec CHERKAOUI Hassan (h3) -> 2ème mariage pour Salima
        mariageService.create(new Mariage(parseDate("01/01/1987"), parseDate("01/01/1989"), 1, h3, f1));
        // - Autres mariages
        mariageService.create(new Mariage(parseDate("20/03/2006"), null, 2, h3, f8));
        mariageService.create(new Mariage(parseDate("14/02/2012"), null, 3, h4, f9));
        mariageService.create(new Mariage(parseDate("10/10/2015"), null, 1, h5, f10));

        System.out.println("Données insérées avec succès : 10 femmes, 5 hommes et leurs mariages.");

        System.out.println("\n================================================================================");
        System.out.println("1. AFFICHAGE DE LA LISTE DES FEMMES");
        System.out.println("================================================================================");
        femmeService.afficherFemmes();

        System.out.println("\n================================================================================");
        System.out.println("2. AFFICHAGE DE LA FEMME LA PLUS ÂGÉE");
        System.out.println("================================================================================");
        femmeService.afficherFemmePlusAgee();

        System.out.println("\n================================================================================");
        System.out.println("3. AFFICHAGE DES ÉPOUSES D'UN HOMME DONNÉ ENTRE DEUX DATES");
        System.out.println("================================================================================");
        Date dDebut = parseDate("01/01/1990");
        Date dFin = parseDate("31/12/2000");
        hommeService.afficherEpousesEntreDates(h1, dDebut, dFin);

        System.out.println("\n================================================================================");
        System.out.println("4. NOMBRE D'ENFANTS D'UNE FEMME ENTRE DEUX DATES (REQUÊTE NATIVE NOMMÉE)");
        System.out.println("================================================================================");
        Date date1 = parseDate("01/01/1985");
        Date date2 = parseDate("31/12/1995");
        femmeService.afficherNombreEnfantsEntreDates(f1, date1, date2);

        System.out.println("\n================================================================================");
        System.out.println("5. FEMMES MARIÉES DEUX FOIS OU PLUS (REQUÊTE NOMMÉE)");
        System.out.println("================================================================================");
        femmeService.afficherFemmesMarieesAuMoinsDeuxFois();

        System.out.println("\n================================================================================");
        System.out.println("6. HOMMES MARIÉS À QUATRE FEMMES ENTRE DEUX DATES (CRITERIA API)");
        System.out.println("================================================================================");
        Date critDebut = parseDate("01/01/1990");
        Date critFin = parseDate("31/12/2010");
        hommeService.afficherHommesMariesQuatreFemmes(critDebut, critFin);

        System.out.println("\n================================================================================");
        System.out.println("7. MARIAGES D'UN HOMME AVEC TOUS LES DÉTAILS (EXEMPLE ATTENDU)");
        System.out.println("================================================================================");
        hommeService.afficherMariages(h1);

        System.out.println("================================================================================");

        HibernateUtil.shutdown();
    }
}
