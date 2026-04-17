package pocketbudget.infra.persistence.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;
import pocketbudget.domain.budget.Budget;
import pocketbudget.domain.budget.BudgetId;
import pocketbudget.domain.budget.BudgetRepository;
import pocketbudget.infra.persistence.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class HibernateBudgetRepository implements BudgetRepository {

    @Override
    public void save(Budget budget) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(budget);
            tx.commit();
        }
    }

    @Override
    public Optional<Budget> findById(BudgetId budgetId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Budget.class, budgetId));
        }
    }

    @Override
    public List<Budget> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Budget", Budget.class).list();
        }
    }

    @Override
    public List<Budget> findAllByUserId(String userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Budget WHERE userId = :userId", Budget.class)
                .setParameter("userId", userId)
                .list();
        }
    }

    @Override
    public List<Budget> findByMonthAndYear(int month, int year) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Budget WHERE month = :month AND year = :year", Budget.class)
                .setParameter("month", month)
                .setParameter("year", year)
                .list();
        }
    }

    @Override
    public List<Budget> findByMonthAndYearAndUserId(int month, int year, String userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Budget WHERE month = :month AND year = :year AND userId = :userId", Budget.class)
                .setParameter("month", month)
                .setParameter("year", year)
                .setParameter("userId", userId)
                .list();
        }
    }

    @Override
    public void delete(BudgetId budgetId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Budget budget = session.get(Budget.class, budgetId);
            if (budget != null) {
                session.remove(budget);
            }
            tx.commit();
        }
    }

    @Override
    public boolean exists(BudgetId budgetId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Budget.class, budgetId) != null;
        }
    }
}
