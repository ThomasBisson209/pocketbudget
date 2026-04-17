package pocketbudget.infra.persistence.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;
import pocketbudget.domain.transaction.TransactionRepository;
import pocketbudget.domain.transaction.TransactionId;
import pocketbudget.infra.persistence.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class HibernateTransactionRepository implements TransactionRepository {

    @Override
    public void save(pocketbudget.domain.transaction.Transaction t) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(t);
            tx.commit();
        }
    }

    @Override
    public Optional<pocketbudget.domain.transaction.Transaction> findById(TransactionId id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(pocketbudget.domain.transaction.Transaction.class, id));
        }
    }

    @Override
    public List<pocketbudget.domain.transaction.Transaction> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Transaction t ORDER BY t.date DESC",
                pocketbudget.domain.transaction.Transaction.class).list();
        }
    }

    @Override
    public List<pocketbudget.domain.transaction.Transaction> findByAccountId(String accountId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Transaction t WHERE t.accountId = :accountId ORDER BY t.date DESC",
                pocketbudget.domain.transaction.Transaction.class)
                .setParameter("accountId", accountId)
                .list();
        }
    }

    @Override
    public List<pocketbudget.domain.transaction.Transaction> findRecentN(int n) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Transaction t ORDER BY t.date DESC",
                pocketbudget.domain.transaction.Transaction.class)
                .setMaxResults(n)
                .list();
        }
    }
}
