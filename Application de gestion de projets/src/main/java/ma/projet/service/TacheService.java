package ma.projet.service;

import ma.projet.classes.EmployeTache;
import ma.projet.classes.Tache;
import ma.projet.dao.IDao;
import ma.projet.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TacheService implements IDao<Tache> {

    @Override
    public boolean create(Tache o) {
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
    public boolean update(Tache o) {
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
    public boolean delete(Tache o) {
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
    public Tache findById(int id) {
        Session session = null;
        Tache tache = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tache = session.get(Tache.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return tache;
    }

    @Override
    public List<Tache> findAll() {
        Session session = null;
        List<Tache> taches = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            taches = session.createQuery("FROM Tache", Tache.class).list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return taches;
    }

    // Afficher les tâches dont le prix est supérieur à 1000 DH (requête nommée)
    public List<Tache> getTachesPrixSuperieur1000() {
        Session session = null;
        List<Tache> taches = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            taches = session.createNamedQuery("Tache.findByPrixSuperieur1000", Tache.class).list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return taches;
    }

    public void afficherTachesPrixSuperieur1000() {
        List<Tache> taches = getTachesPrixSuperieur1000();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("Tâches dont le prix est supérieur à 1000 DH (requête nommée) :");
        System.out.printf("%-5s %-20s %-15s %-15s %-10s %-20s%n", "Num", "Nom", "Date Début", "Date Fin", "Prix (DH)", "Projet");
        if (taches != null) {
            for (Tache t : taches) {
                String dDebut = t.getDateDebut() != null ? sdf.format(t.getDateDebut()) : "-";
                String dFin = t.getDateFin() != null ? sdf.format(t.getDateFin()) : "-";
                String proj = t.getProjet() != null ? t.getProjet().getNom() : "-";
                System.out.printf("%-5d %-20s %-15s %-15s %-10.2f %-20s%n",
                        t.getId(), t.getNom(), dDebut, dFin, t.getPrix(), proj);
            }
        }
    }

    // Afficher les tâches réalisées entre deux dates
    public List<EmployeTache> getTachesRealiseesEntreDates(Date d1, Date d2) {
        Session session = null;
        List<EmployeTache> list = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            list = session.createQuery(
                    "FROM EmployeTache et WHERE et.dateDebutReelle >= :d1 AND et.dateFinReelle <= :d2",
                    EmployeTache.class)
                    .setParameter("d1", d1)
                    .setParameter("d2", d2)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return list;
    }

    public void afficherTachesRealiseesEntreDates(Date d1, Date d2) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        List<EmployeTache> list = getTachesRealiseesEntreDates(d1, d2);
        System.out.println("Tâches réalisées entre " + sdf.format(d1) + " et " + sdf.format(d2) + " :");
        System.out.printf("%-5s %-20s %-20s %-20s %-20s%n", "Num", "Nom de la tâche", "Date Début Réelle", "Date Fin Réelle", "Employé");
        if (list != null) {
            for (EmployeTache et : list) {
                String dDebut = et.getDateDebutReelle() != null ? sdf.format(et.getDateDebutReelle()) : "-";
                String dFin = et.getDateFinReelle() != null ? sdf.format(et.getDateFinReelle()) : "-";
                String emp = et.getEmploye() != null ? (et.getEmploye().getNom() + " " + et.getEmploye().getPrenom()) : "-";
                System.out.printf("%-5d %-20s %-20s %-20s %-20s%n",
                        et.getTache().getId(),
                        et.getTache().getNom(),
                        dDebut,
                        dFin,
                        emp);
            }
        }
    }
}
