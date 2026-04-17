package pocketbudget.infra.persistence;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.net.URI;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Configuration config = new Configuration().configure();

            // 1. Railway injects DATABASE_URL as  postgresql://user:pass@host:port/db
            String databaseUrl = System.getenv("DATABASE_URL");

            // 2. Manual override via DB_URL (JDBC format)
            String dbUrl = System.getenv("DB_URL");

            if (databaseUrl != null && !databaseUrl.isBlank()) {
                applyPostgresUrl(config, databaseUrl);
            } else if (dbUrl != null && !dbUrl.isBlank()) {
                config.setProperty("hibernate.connection.url", dbUrl);
                if (dbUrl.startsWith("jdbc:postgresql")) {
                    config.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
                    config.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
                    config.setProperty("hibernate.hbm2ddl.auto", "update");
                }
            }
            // else: use defaults from hibernate.cfg.xml (H2 in-memory for local dev)

            sessionFactory = config.buildSessionFactory();
        }
        return sessionFactory;
    }

    /**
     * Converts Railway's DATABASE_URL format to JDBC properties.
     *  postgresql://user:password@host:5432/dbname
     *  → jdbc:postgresql://host:5432/dbname
     */
    private static void applyPostgresUrl(Configuration config, String rawUrl) {
        try {
            // Ensure it starts with a scheme java.net.URI can parse
            String normalized = rawUrl.startsWith("jdbc:") ? rawUrl.substring(5) : rawUrl;
            URI uri = new URI(normalized);

            String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + uri.getPort() + uri.getPath();

            String userInfo = uri.getUserInfo();
            String username = "";
            String password = "";
            if (userInfo != null) {
                String[] parts = userInfo.split(":", 2);
                username = parts[0];
                password = parts.length > 1 ? parts[1] : "";
            }

            config.setProperty("hibernate.connection.url", jdbcUrl);
            config.setProperty("hibernate.connection.username", username);
            config.setProperty("hibernate.connection.password", password);
            config.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
            config.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            config.setProperty("hibernate.hbm2ddl.auto", "update"); // keep data between restarts
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse DATABASE_URL: " + rawUrl, e);
        }
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
