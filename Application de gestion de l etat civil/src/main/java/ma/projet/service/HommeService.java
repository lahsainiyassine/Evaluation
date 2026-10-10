package ma.projet.service;

import ma.projet.beans.Femme;
import ma.projet.beans.Homme;
import ma.projet.beans.Mariage;
import ma.projet.dao.IDao;
import ma.projet.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HommeService implements IDao<Homme> {

    @Override
    public boolean create(Homme o) {
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
    public boolean update(Homme o) {
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
    public boolean delete(Homme o) {
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
    public Homme findById(int id) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.get(Homme.class, id);
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public List<Homme> findAll() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.createQuery("from Homme", Homme.class).list();
        } finally {
            if (session != null) session.close();
        }
    }

    /**
     * Recherche les épouses d’un homme entre deux dates.
     */
    public List<Femme> findEpousesEntreDates(Homme homme, Date d1, Date d2) {
        if (homme == null) return Collections.emptyList();
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            String hql = "SELECT DISTINCT m.femme FROM Mariage m WHERE m.homme = :homme AND m.dateDebut BETWEEN :d1 AND :d2";
            return session.createQuery(hql, Femme.class)
                    .setParameter("homme", homme)
                    .setParameter("d1", d1)
                    .setParameter("d2", d2)
                    .getResultList();
        } finally {
            if (session != null) session.close();
        }
    }

    /**
     * Affiche les épouses d’un homme entre deux dates.
     */
    public void afficherEpousesEntreDates(Homme homme, Date d1, Date d2) {
        if (homme == null) {
            System.out.println("Homme null.");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        List<Femme> epouses = findEpousesEntreDates(homme, d1, d2);
        System.out.println("Épouses de " + homme.getNom() + " " + homme.getPrenom() +
                " entre le " + sdf.format(d1) + " et le " + sdf.format(d2) + " :");
        if (epouses.isEmpty()) {
            System.out.println("Aucune épouse trouvée pour cette période.");
        } else {
            int i = 1;
            for (Femme f : epouses) {
                System.out.println(i++ + ". " + f.getNom() + " " + f.getPrenom() +
                        " (Née le: " + (f.getDateNaissance() != null ? sdf.format(f.getDateNaissance()) : "-") +
                        ", Tél: " + f.getTelephone() + ")");
            }
        }
    }

    /**
     * Utilise l'API Criteria pour retrouver les hommes mariés à 4 femmes entre deux dates.
     */
    public List<Homme> findHommesMariesQuatreFemmesCriteria(Date d1, Date d2) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Homme> cq = cb.createQuery(Homme.class);
            Root<Mariage> root = cq.from(Mariage.class);

            // Clause WHERE : dateDebut BETWEEN d1 AND d2
            Predicate betweenDates = cb.between(root.get("dateDebut"), d1, d2);
            cq.where(betweenDates);

            // Clause GROUP BY : groupé par homme
            cq.groupBy(root.get("homme"));

            // Clause HAVING : COUNT(DISTINCT femme) = 4
            cq.having(cb.equal(cb.countDistinct(root.get("femme")), 4L));

            // SELECT homme
            cq.select(root.get("homme"));

            return session.createQuery(cq).getResultList();
        } finally {
            if (session != null) session.close();
        }
    }

    /**
     * Utilise l'API Criteria pour afficher le nombre d'hommes mariés à quatre femmes entre deux dates.
     */
    public long countHommesMariesQuatreFemmes(Date d1, Date d2) {
        List<Homme> list = findHommesMariesQuatreFemmesCriteria(d1, d2);
        return list != null ? list.size() : 0;
    }

    /**
     * Affiche le nombre d'hommes mariés à quatre femmes entre deux dates (Criteria API).
     */
    public void afficherHommesMariesQuatreFemmes(Date d1, Date d2) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        List<Homme> hommes = findHommesMariesQuatreFemmesCriteria(d1, d2);
        int count = (hommes != null) ? hommes.size() : 0;
        System.out.println("Nombre d'hommes mariés à quatre femmes entre le " + sdf.format(d1) + " et le " + sdf.format(d2) + " : " + count);
        if (hommes != null && !hommes.isEmpty()) {
            System.out.println("Liste de ces hommes :");
            for (Homme h : hommes) {
                System.out.println("- " + h.getNom() + " " + h.getPrenom() + " (Tél: " + h.getTelephone() + ")");
            }
        }
    }

    /**
     * Affiche les mariages d’un homme donné avec les détails selon le format spécifié :
     * Nom : SAFI SAID
     * Mariages En Cours :
     * 1. Femme : SALIMA RAMI   Date Début : 03/09/1990    Nbr Enfants : 4
     * ...
     * Mariages échoués :
     * 1. Femme : KARIMA ALAMI  Date Début : 03/09/1989
     * Date Fin : 03/09/1990    Nbr Enfants : 0
     */
    public void afficherMariages(Homme homme) {
        if (homme == null) {
            System.out.println("Homme inexistant.");
            return;
        }

        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            List<Mariage> mariages = session.createQuery(
                    "FROM Mariage m JOIN FETCH m.femme WHERE m.homme = :homme ORDER BY m.dateDebut ASC", Mariage.class)
                    .setParameter("homme", homme)
                    .getResultList();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            System.out.println("Nom : " + (homme.getNom() != null ? homme.getNom().toUpperCase() : "") + " "
                    + (homme.getPrenom() != null ? homme.getPrenom().toUpperCase() : ""));

            List<Mariage> enCours = new ArrayList<>();
            List<Mariage> echoues = new ArrayList<>();

            for (Mariage m : mariages) {
                if (m.getDateFin() == null) {
                    enCours.add(m);
                } else {
                    echoues.add(m);
                }
            }

            System.out.println("Mariages En Cours :");
            if (enCours.isEmpty()) {
                System.out.println("Aucun mariage en cours.");
            } else {
                int i = 1;
                for (Mariage m : enCours) {
                    String nomFemme = (m.getFemme() != null)
                            ? (m.getFemme().getNom() + " " + m.getFemme().getPrenom()).toUpperCase()
                            : "INCONNUE";
                    String dateDebutStr = (m.getDateDebut() != null) ? sdf.format(m.getDateDebut()) : "-";
                    System.out.printf("%d. Femme : %-15s Date Début : %-12s Nbr Enfants : %d\n",
                            i++, nomFemme, dateDebutStr, m.getNbrEnfant());
                }
            }

            System.out.println("\nMariages échoués :");
            if (echoues.isEmpty()) {
                System.out.println("Aucun mariage échoué.");
            } else {
                int i = 1;
                for (Mariage m : echoues) {
                    String nomFemme = (m.getFemme() != null)
                            ? (m.getFemme().getNom() + " " + m.getFemme().getPrenom()).toUpperCase()
                            : "INCONNUE";
                    String dateDebutStr = (m.getDateDebut() != null) ? sdf.format(m.getDateDebut()) : "-";
                    String dateFinStr = (m.getDateFin() != null) ? sdf.format(m.getDateFin()) : "-";
                    System.out.printf("%d. Femme : %-15s Date Début : %s\nDate Fin : %-12s   Nbr Enfants : %d\n",
                            i++, nomFemme, dateDebutStr, dateFinStr, m.getNbrEnfant());
                }
            }
        } finally {
            if (session != null) session.close();
        }
    }

    public void afficherMariages(int hommeId) {
        Homme h = findById(hommeId);
        afficherMariages(h);
    }
}
