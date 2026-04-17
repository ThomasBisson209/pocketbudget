package pocketbudget.infra.persistence;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Configuration config = new Configuration().configure();

            String dbUrl = System.getenv("DB_URL");
            if (dbUrl != null && !dbUrl.isBlank()) {
                config.setProperty("hibernate.connection.url", dbUrl);
                if (dbUrl.startsWith("jdbc:postgresql")) {
                    config.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
                    config.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
                }
            }

            sessionFactory = config.buildSessionFactory();
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
