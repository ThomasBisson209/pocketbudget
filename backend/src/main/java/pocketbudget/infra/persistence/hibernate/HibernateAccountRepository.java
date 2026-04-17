package pocketbudget.infra.persistence.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;
import pocketbudget.domain.account.Account;
import pocketbudget.domain.account.AccountId;
import pocketbudget.domain.account.AccountRepository;
import pocketbudget.infra.persistence.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class HibernateAccountRepository implements AccountRepository {

    @Override
    public void save(Account account) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(account);
            tx.commit();
        }
    }

    @Override
    public Optional<Account> findById(AccountId accountId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Account.class, accountId));
        }
    }

    @Override
    public List<Account> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Account", Account.class).list();
        }
    }

    @Override
    public void delete(AccountId accountId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Account account = session.get(Account.class, accountId);
            if (account != null) {
                session.remove(account);
            }
            tx.commit();
        }
    }

    @Override
    public boolean exists(AccountId accountId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Account.class, accountId) != null;
        }
    }
}
