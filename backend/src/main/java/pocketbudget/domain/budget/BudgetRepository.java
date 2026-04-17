package pocketbudget.domain.budget;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository {
    void save(Budget budget);
    Optional<Budget> findById(BudgetId budgetId);
    List<Budget> findAll();
    List<Budget> findByMonthAndYear(int month, int year);
    void delete(BudgetId budgetId);
    boolean exists(BudgetId budgetId);
}
