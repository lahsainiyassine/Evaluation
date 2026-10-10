package ma.projet.service;

import ma.projet.classes.EmployeTache;
import ma.projet.classes.Projet;
import ma.projet.classes.Tache;
import ma.projet.dao.IDao;
import ma.projet.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ProjetService implements IDao<Projet> {

    @Override
    public boolean create(Projet o) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.save(o);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public boolean update(Projet o) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.update(o);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public boolean delete(Projet o) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.delete(o);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public Projet findById(int id) {
        Session session = null;
        Projet projet = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            projet = session.get(Projet.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return projet;
    }

    @Override
    public List<Projet> findAll() {
        Session session = null;
        List<Projet> projets = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            projets = session.createQuery("FROM Projet", Projet.class).list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return projets;
    }

    // Afficher la liste des tâches planifiées pour un projet
    public void afficherTachesPlanifiees(Projet p) {
        Session session = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            List<Tache> taches = session.createQuery(
                    "FROM Tache t WHERE t.projet.id = :projId", Tache.class)
                    .setParameter("projId", p.getId())
                    .list();

            System.out.println("Tâches planifiées pour le projet : " + p.getNom());
            System.out.printf("%-5s %-20s %-15s %-15s %-10s%n", "Num", "Nom", "Date Début", "Date Fin", "Prix (DH)");
            for (Tache t : taches) {
                String dDebut = t.getDateDebut() != null ? sdf.format(t.getDateDebut()) : "-";
                String dFin = t.getDateFin() != null ? sdf.format(t.getDateFin()) : "-";
                System.out.printf("%-5d %-20s %-15s %-15s %-10.2f%n",
                        t.getId(), t.getNom(), dDebut, dFin, t.getPrix());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
    }

    // Afficher la liste des tâches réalisées avec les dates réelles
    public void afficherTachesRealiseesAvecDatesReelles(Projet p) {
        Session session = null;
        SimpleDateFormat sdfHeader = new SimpleDateFormat("d MMMM yyyy", Locale.FRENCH);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            List<EmployeTache> list = session.createQuery(
                    "FROM EmployeTache et WHERE et.tache.projet.id = :projId", EmployeTache.class)
                    .setParameter("projId", p.getId())
                    .list();

            String dateDebutStr = p.getDateDebut() != null ? sdfHeader.format(p.getDateDebut()) : "-";
            System.out.printf("Projet : %-6d Nom : %-20s Date début : %s%n", p.getId(), p.getNom(), dateDebutStr);
            System.out.println("Liste des tâches:");
            System.out.printf("%-4s %-15s %-20s %-15s%n", "Num", "Nom", "Date Début Réelle", "Date Fin Réelle");
            for (EmployeTache et : list) {
                String dDebutR = et.getDateDebutReelle() != null ? sdf.format(et.getDateDebutReelle()) : "-";
                String dFinR = et.getDateFinReelle() != null ? sdf.format(et.getDateFinReelle()) : "-";
                System.out.printf("%-4d %-15s %-20s %-15s%n",
                        et.getTache().getId(),
                        et.getTache().getNom(),
                        dDebutR,
                        dFinR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
    }
}
