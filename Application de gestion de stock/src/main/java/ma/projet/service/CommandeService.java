package ma.projet.service;

import ma.projet.classes.Commande;
import ma.projet.classes.LigneCommandeProduit;
import ma.projet.dao.IDao;
import ma.projet.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommandeService implements IDao<Commande> {

    @Override
    public boolean create(Commande o) {
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
    public boolean update(Commande o) {
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
    public boolean delete(Commande o) {
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
    public Commande findById(int id) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.get(Commande.class, id);
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public List<Commande> findAll() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.createQuery("from Commande", Commande.class).list();
        } finally {
            if (session != null) session.close();
        }
    }

    public void afficherProduitsCommandes(Commande commande) {
        if (commande == null) {
            System.out.println("Commande inexistante.");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH);
        String dateStr = commande.getDate() != null ? sdf.format(commande.getDate()) : "N/A";
        // Capitalize first letter of month if desirable
        String[] parts = dateStr.split(" ");
        if (parts.length == 3) {
            dateStr = parts[0] + " " + parts[1].substring(0, 1).toUpperCase() + parts[1].substring(1) + " " + parts[2];
        }

        System.out.println("Commande : " + commande.getId() + "\t Date : " + dateStr);
        System.out.println("Liste des produits :");
        System.out.printf("%-12s %-10s %-10s\n", "Référence", "Prix", "Quantité");

        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            List<LigneCommandeProduit> lignes = session.createQuery(
                    "FROM LigneCommandeProduit l WHERE l.commande.id = :cmdId", LigneCommandeProduit.class)
                    .setParameter("cmdId", commande.getId())
                    .getResultList();

            for (LigneCommandeProduit lcp : lignes) {
                String ref = lcp.getProduit() != null ? lcp.getProduit().getReference() : "-";
                float prix = lcp.getProduit() != null ? lcp.getProduit().getPrix() : 0;
                String prixStr = (prix == (long) prix ? String.format("%d", (long) prix) : String.format("%.2f", prix)) + " DH";
                System.out.printf("%-12s %-10s %-10d\n", ref, prixStr, lcp.getQuantite());
            }
        } finally {
            if (session != null) session.close();
        }
    }

    public void afficherProduitsCommandes(int commandeId) {
        Commande c = findById(commandeId);
        afficherProduitsCommandes(c);
    }
}
