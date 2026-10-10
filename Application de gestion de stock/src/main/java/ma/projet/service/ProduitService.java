package ma.projet.service;

import ma.projet.classes.Categorie;
import ma.projet.classes.Commande;
import ma.projet.classes.LigneCommandeProduit;
import ma.projet.classes.Produit;
import ma.projet.dao.IDao;
import ma.projet.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProduitService implements IDao<Produit> {

    @Override
    public boolean create(Produit o) {
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
    public boolean update(Produit o) {
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
    public boolean delete(Produit o) {
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
    public Produit findById(int id) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.get(Produit.class, id);
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public List<Produit> findAll() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.createQuery("from Produit", Produit.class).list();
        } finally {
            if (session != null) session.close();
        }
    }

    /**
     * Recherche la liste des produits appartenant à une catégorie donnée.
     */
    public List<Produit> findByCategorie(Categorie categorie) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.createNamedQuery("Produit.findByCategorie", Produit.class)
                    .setParameter("categorie", categorie)
                    .getResultList();
        } finally {
            if (session != null) session.close();
        }
    }

    /**
     * Affiche la liste des produits par catégorie.
     */
    public void afficherProduitsParCategorie(Categorie categorie) {
        if (categorie == null) {
            System.out.println("Catégorie null.");
            return;
        }
        List<Produit> produits = findByCategorie(categorie);
        System.out.println("Catégorie : " + categorie.getLibelle() + " (" + categorie.getCode() + ")");
        System.out.println("Liste des produits :");
        System.out.printf("%-12s %-12s\n", "Référence", "Prix");
        for (Produit p : produits) {
            String prixStr = formatPrix(p.getPrix());
            System.out.printf("%-12s %-12s\n", p.getReference(), prixStr);
        }
        System.out.println();
    }

    /**
     * Recherche les produits commandés entre deux dates.
     */
    public List<Produit> findProduitsCommandesEntreDates(Date dateDebut, Date dateFin) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            String hql = "SELECT DISTINCT l.produit FROM LigneCommandeProduit l " +
                         "WHERE l.commande.date BETWEEN :dateDebut AND :dateFin";
            return session.createQuery(hql, Produit.class)
                    .setParameter("dateDebut", dateDebut)
                    .setParameter("dateFin", dateFin)
                    .getResultList();
        } finally {
            if (session != null) session.close();
        }
    }

    /**
     * Affiche les produits commandés entre deux dates.
     */
    public void afficherProduitsCommandesEntreDates(Date dateDebut, Date dateFin) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("Produits commandés entre le " + sdf.format(dateDebut) + " et le " + sdf.format(dateFin) + " :");
        List<Produit> produits = findProduitsCommandesEntreDates(dateDebut, dateFin);
        if (produits.isEmpty()) {
            System.out.println("Aucun produit commandé durant cette période.");
        } else {
            System.out.printf("%-12s %-12s\n", "Référence", "Prix");
            for (Produit p : produits) {
                System.out.printf("%-12s %-12s\n", p.getReference(), formatPrix(p.getPrix()));
            }
        }
        System.out.println();
    }

    /**
     * Affiche les produits commandés dans une commande donnée.
     * Exemple de format attendu :
     * Commande : 4     Date : 14 Mars 2013
     * Liste des produits :
     * Référence   Prix    Quantité
     * ES12        120 DH  7
     * ZR85        100 DH  14
     * EE85        200 DH  5
     */
    public void afficherProduitsCommandes(Commande commande) {
        if (commande == null) {
            System.out.println("Commande inexistante.");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH);
        String dateStr = commande.getDate() != null ? sdf.format(commande.getDate()) : "N/A";
        // Mettre en majuscule l'initiale du mois
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
                System.out.printf("%-12s %-10s %-10d\n", ref, formatPrix(prix), lcp.getQuantite());
            }
        } finally {
            if (session != null) session.close();
        }
        System.out.println();
    }

    public void afficherProduitsCommandes(int commandeId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Commande c = session.get(Commande.class, commandeId);
            afficherProduitsCommandes(c);
        } finally {
            if (session != null) session.close();
        }
    }

    public void afficherProduitsParCommande(Commande commande) {
        afficherProduitsCommandes(commande);
    }

    public void afficherProduitsParCommande(int commandeId) {
        afficherProduitsCommandes(commandeId);
    }

    /**
     * Recherche les produits dont le prix est supérieur à 100 DH en utilisant une requête nommée.
     */
    public List<Produit> findPrixSuperieurA100() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.createNamedQuery("Produit.findByPrixSuperieurA100", Produit.class)
                    .getResultList();
        } finally {
            if (session != null) session.close();
        }
    }

    /**
     * Affiche la liste des produits dont le prix est supérieur à 100 DH en utilisant une requête nommée.
     */
    public void afficherProduitsPrixSuperieurA100() {
        System.out.println("Liste des produits dont le prix est supérieur à 100 DH (Requête nommée) :");
        List<Produit> produits = findPrixSuperieurA100();
        System.out.printf("%-12s %-12s\n", "Référence", "Prix");
        for (Produit p : produits) {
            System.out.printf("%-12s %-12s\n", p.getReference(), formatPrix(p.getPrix()));
        }
        System.out.println();
    }

    /**
     * Recherche les produits dont le prix est supérieur à une valeur donnée via requête nommée paramétrée.
     */
    public List<Produit> findPrixSuperieurA(float minPrix) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.createNamedQuery("Produit.findByPrixSuperieurA", Produit.class)
                    .setParameter("prix", minPrix)
                    .getResultList();
        } finally {
            if (session != null) session.close();
        }
    }

    private String formatPrix(float prix) {
        if (prix == (long) prix) {
            return String.format("%d DH", (long) prix);
        } else {
            return String.format("%.2f DH", prix);
        }
    }
}
