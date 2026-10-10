package ma.projet.util;

import ma.projet.classes.Employe;
import ma.projet.classes.EmployeTache;
import ma.projet.classes.Projet;
import ma.projet.classes.Tache;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.io.InputStream;
import java.util.Properties;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                // Charger application.properties
                Properties properties = new Properties();
                InputStream inputStream = HibernateUtil.class.getClassLoader().getResourceAsStream("application.properties");
                if (inputStream != null) {
                    properties.load(inputStream);
                    configuration.setProperties(properties);
                } else {
                    System.err.println("Le fichier application.properties n'a pas été trouvé dans le classpath !");
                }

                // Enregistrement des classes entités
                configuration.addAnnotatedClass(Employe.class);
                configuration.addAnnotatedClass(Projet.class);
                configuration.addAnnotatedClass(Tache.class);
                configuration.addAnnotatedClass(EmployeTache.class);

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }

    public static void closeSessionFactory() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}
