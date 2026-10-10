package ma.projet.test;

import ma.projet.classes.Employe;
import ma.projet.classes.EmployeTache;
import ma.projet.classes.Projet;
import ma.projet.classes.Tache;
import ma.projet.service.EmployeService;
import ma.projet.service.EmployeTacheService;
import ma.projet.service.ProjetService;
import ma.projet.service.TacheService;
import ma.projet.util.HibernateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestProjet {

    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        EmployeService employeService = new EmployeService();
        ProjetService projetService = new ProjetService();
        TacheService tacheService = new TacheService();
        EmployeTacheService employeTacheService = new EmployeTacheService();

        try {
            // 1. Création des employés
            Employe emp1 = new Employe("Alami", "Ahmed", "0611223344");
            Employe emp2 = new Employe("Benali", "Karim", "0655667788");
            employeService.create(emp1);
            employeService.create(emp2);

            // 2. Création d'un projet avec chef de projet emp1
            Date dateDebutProj = sdf.parse("14/01/2013");
            Date dateFinProj = sdf.parse("31/12/2013");
            Projet proj = new Projet("Gestion de stock", dateDebutProj, dateFinProj, emp1);
            projetService.create(proj);

            // 3. Création des tâches pour ce projet
            Tache t1 = new Tache("Analyse", sdf.parse("01/02/2013"), sdf.parse("25/02/2013"), 1200.0, proj);
            Tache t2 = new Tache("Conception", sdf.parse("01/03/2013"), sdf.parse("20/03/2013"), 1500.0, proj);
            Tache t3 = new Tache("Développement", sdf.parse("01/04/2013"), sdf.parse("30/04/2013"), 3500.0, proj);
            Tache t4 = new Tache("Test", sdf.parse("01/05/2013"), sdf.parse("15/05/2013"), 800.0, proj);

            tacheService.create(t1);
            tacheService.create(t2);
            tacheService.create(t3);
            tacheService.create(t4);

            // 4. Assignation des tâches aux employés avec dates réelles
            EmployeTache et1 = new EmployeTache(sdf.parse("10/02/2013"), sdf.parse("20/02/2013"), emp2, t1);
            EmployeTache et2 = new EmployeTache(sdf.parse("10/03/2013"), sdf.parse("15/03/2013"), emp2, t2);
            EmployeTache et3 = new EmployeTache(sdf.parse("10/04/2013"), sdf.parse("25/04/2013"), emp2, t3);

            employeTacheService.create(et1);
            employeTacheService.create(et2);
            employeTacheService.create(et3);

            System.out.println("================================================================================");
            System.out.println("1. Tâches réalisées avec les dates réelles (format attendu de l'exercice) :");
            System.out.println("================================================================================");
            projetService.afficherTachesRealiseesAvecDatesReelles(proj);

            System.out.println("\n================================================================================");
            System.out.println("2. Tâches planifiées pour le projet :");
            System.out.println("================================================================================");
            projetService.afficherTachesPlanifiees(proj);

            System.out.println("\n================================================================================");
            System.out.println("3. Projets gérés par l'employé (Chef de projet) :");
            System.out.println("================================================================================");
            employeService.afficherProjetsGeres(emp1);

            System.out.println("\n================================================================================");
            System.out.println("4. Tâches réalisées par un employé :");
            System.out.println("================================================================================");
            employeService.afficherTachesRealisees(emp2);

            System.out.println("\n================================================================================");
            System.out.println("5. Tâches dont le prix > 1000 DH (requête nommée) :");
            System.out.println("================================================================================");
            tacheService.afficherTachesPrixSuperieur1000();

            System.out.println("\n================================================================================");
            System.out.println("6. Tâches réalisées entre deux dates (01/01/2013 et 31/03/2013) :");
            System.out.println("================================================================================");
            tacheService.afficherTachesRealiseesEntreDates(sdf.parse("01/01/2013"), sdf.parse("31/03/2013"));

        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.closeSessionFactory();
        }
    }
}
