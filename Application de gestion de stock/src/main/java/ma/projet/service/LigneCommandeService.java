package ma.projet.service;

import ma.projet.classes.Commande;
import ma.projet.classes.LigneCommandeProduit;
import ma.projet.classes.LigneCommandeProduitPK;
import ma.projet.dao.IDao;
import ma.projet.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class LigneCommandeService implements IDao<LigneCommandeProduit> {

    @Override
    public boolean create(LigneCommandeProduit o) {
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
    public boolean update(LigneCommandeProduit o) {
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
    public boolean delete(LigneCommandeProduit o) {
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
    public LigneCommandeProduit findById(int id) {
        // LigneCommandeProduit possède une clé primaire composite (LigneCommandeProduitPK)
        return null;
    }

    public LigneCommandeProduit findById(LigneCommandeProduitPK pk) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.get(LigneCommandeProduit.class, pk);
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public List<LigneCommandeProduit> findAll() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.createQuery("from LigneCommandeProduit", LigneCommandeProduit.class).list();
        } finally {
            if (session != null) session.close();
        }
    }

    public List<LigneCommandeProduit> findByCommande(Commande commande) {
        if (commande == null) return null;
        return findByCommande(commande.getId());
    }

    public List<LigneCommandeProduit> findByCommande(int commandeId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.createQuery(
                    "FROM LigneCommandeProduit l WHERE l.commande.id = :cmdId", LigneCommandeProduit.class)
                    .setParameter("cmdId", commandeId)
                    .getResultList();
        } finally {
            if (session != null) session.close();
        }
    }
}
