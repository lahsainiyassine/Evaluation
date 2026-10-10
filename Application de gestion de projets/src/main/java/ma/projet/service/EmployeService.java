package ma.projet.service;

import ma.projet.classes.Employe;
import ma.projet.classes.EmployeTache;
import ma.projet.classes.Projet;
import ma.projet.dao.IDao;
import ma.projet.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.text.SimpleDateFormat;
import java.util.List;

public class EmployeService implements IDao<Employe> {

    @Override
    public boolean create(Employe o) {
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
    public boolean update(Employe o) {
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
    public boolean delete(Employe o) {
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
    public Employe findById(int id) {
        Session session = null;
        Employe employe = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            employe = session.get(Employe.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return employe;
    }

    @Override
    public List<Employe> findAll() {
        Session session = null;
        List<Employe> employes = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            employes = session.createQuery("FROM Employe", Employe.class).list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return employes;
    }

    // Afficher la liste des tâches réalisées par un employé
    public void afficherTachesRealisees(Employe e) {
        Session session = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            List<EmployeTache> list = session.createQuery(
                    "FROM EmployeTache et WHERE et.employe.id = :empId", EmployeTache.class)
                    .setParameter("empId", e.getId())
                    .list();

            System.out.println("Tâches réalisées par l'employé " + e.getNom() + " " + e.getPrenom() + " :");
            System.out.printf("%-5s %-20s %-20s %-20s%n", "Num", "Nom", "Date Début Réelle", "Date Fin Réelle");
            for (EmployeTache et : list) {
                String dDebut = et.getDateDebutReelle() != null ? sdf.format(et.getDateDebutReelle()) : "-";
                String dFin = et.getDateFinReelle() != null ? sdf.format(et.getDateFinReelle()) : "-";
                System.out.printf("%-5d %-20s %-20s %-20s%n",
                        et.getTache().getId(),
                        et.getTache().getNom(),
                        dDebut,
                        dFin);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
    }

    // Afficher la liste des projets gérés par un employé
    public void afficherProjetsGeres(Employe e) {
        Session session = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            List<Projet> projets = session.createQuery(
                    "FROM Projet p WHERE p.employe.id = :empId", Projet.class)
                    .setParameter("empId", e.getId())
                    .list();

            System.out.println("Projets gérés par l'employé " + e.getNom() + " " + e.getPrenom() + " :");
            System.out.printf("%-5s %-25s %-15s %-15s%n", "ID", "Nom du projet", "Date Début", "Date Fin");
            for (Projet p : projets) {
                String dDebut = p.getDateDebut() != null ? sdf.format(p.getDateDebut()) : "-";
                String dFin = p.getDateFin() != null ? sdf.format(p.getDateFin()) : "-";
                System.out.printf("%-5d %-25s %-15s %-15s%n", p.getId(), p.getNom(), dDebut, dFin);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
    }
}
