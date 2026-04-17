package pocketbudget.infra.persistence.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;
import pocketbudget.domain.user.User;
import pocketbudget.domain.user.UserRepository;
import pocketbudget.infra.persistence.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class HibernateUserRepository implements UserRepository {

    @Override
    public void save(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(user);
            tx.commit();
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<User> results = session.createQuery(
                "FROM User WHERE username = :username", User.class)
                .setParameter("username", username)
                .list();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }
}
