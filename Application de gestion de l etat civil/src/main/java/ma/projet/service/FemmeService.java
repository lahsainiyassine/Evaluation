package ma.projet.service;

import ma.projet.beans.Femme;
import ma.projet.dao.IDao;
import ma.projet.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FemmeService implements IDao<Femme> {

    @Override
    public boolean create(Femme o) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.save(o);
            tx.commit();
            return true;
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public boolean update(Femme o) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.update(o);
            tx.commit();
            return true;
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public boolean delete(Femme o) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.delete(o);
            tx.commit();
            return true;
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public Femme findById(int id) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.get(Femme.class, id);
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public List<Femme> findAll() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.createQuery("from Femme", Femme.class).list();
        } finally {
            if (session != null) session.close();
        }
    }

    /**
     * Exécute une requête native nommée retournant le nombre d’enfants d’une femme entre deux dates.
     */
    public int countEnfantsEntreDates(Femme femme, Date d1, Date d2) {
        if (femme == null) return 0;
        return countEnfantsEntreDates(femme.getId(), d1, d2);
    }

    public int countEnfantsEntreDates(int femmeId, Date d1, Date d2) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            NativeQuery<?> query = session.getNamedNativeQuery("Femme.countEnfantsBetweenDates");
            query.setParameter("femmeId", femmeId);
            query.setParameter("dateDebut", d1);
            query.setParameter("dateFin", d2);
            Object result = query.getSingleResult();
            if (result instanceof Number) {
                return ((Number) result).intValue();
            }
            return 0;
        } finally {
            if (session != null) session.close();
        }
    }

    /**
     * Affiche le nombre d'enfants d'une femme entre deux dates.
     */
    public void afficherNombreEnfantsEntreDates(Femme femme, Date d1, Date d2) {
        if (femme == null) {
            System.out.println("Femme null.");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        int count = countEnfantsEntreDates(femme, d1, d2);
        System.out.println("Nombre d'enfants de " + femme.getNom() + " " + femme.getPrenom() +
                " entre le " + sdf.format(d1) + " et le " + sdf.format(d2) + " : " + count);
    }

    /**
     * Exécute une requête nommée retournant les femmes mariées au moins deux fois.
     */
    public List<Femme> findFemmesMarieesAuMoinsDeuxFois() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.createNamedQuery("Femme.findFemmesMarieesAuMoinsDeuxFois", Femme.class)
                    .getResultList();
        } finally {
            if (session != null) session.close();
        }
    }

    /**
     * Affiche la liste des femmes mariées deux fois ou plus.
     */
    public void afficherFemmesMarieesAuMoinsDeuxFois() {
        List<Femme> femmes = findFemmesMarieesAuMoinsDeuxFois();
        System.out.println("Femmes mariées deux fois ou plus :");
        if (femmes.isEmpty()) {
            System.out.println("Aucune femme trouvée.");
        } else {
            for (Femme f : femmes) {
                System.out.println("- " + f.getNom() + " " + f.getPrenom() + " (Tél: " + f.getTelephone() + ")");
            }
        }
    }

    /**
     * Retourne la femme la plus âgée.
     */
    public Femme findFemmePlusAgee() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.createNamedQuery("Femme.findPlusAgee", Femme.class)
                    .setMaxResults(1)
                    .uniqueResult();
        } finally {
            if (session != null) session.close();
        }
    }

    /**
     * Affiche la femme la plus âgée.
     */
    public void afficherFemmePlusAgee() {
        Femme f = findFemmePlusAgee();
        if (f != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            System.out.println("La femme la plus âgée est : " + f.getNom() + " " + f.getPrenom() +
                    " née le " + (f.getDateNaissance() != null ? sdf.format(f.getDateNaissance()) : "N/A") +
                    " (Adresse : " + f.getAdresse() + ", Tél : " + f.getTelephone() + ")");
        } else {
            System.out.println("Aucune femme enregistrée.");
        }
    }

    /**
     * Affiche la liste de toutes les femmes.
     */
    public void afficherFemmes() {
        List<Femme> femmes = findAll();
        System.out.println("Liste des femmes :");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (Femme f : femmes) {
            System.out.printf("ID: %-2d | %-12s %-12s | Née le: %-10s | Tél: %-12s | Adresse: %s\n",
                    f.getId(), f.getNom(), f.getPrenom(),
                    f.getDateNaissance() != null ? sdf.format(f.getDateNaissance()) : "-",
                    f.getTelephone(), f.getAdresse());
        }
    }
}
