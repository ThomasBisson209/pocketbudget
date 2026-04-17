package pocketbudget.infra.persistence.inMemory;

import pocketbudget.domain.budget.Budget;
import pocketbudget.domain.budget.BudgetId;
import pocketbudget.domain.budget.BudgetRepository;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryBudgetRepository implements BudgetRepository {
    private final Map<BudgetId, Budget> store = new HashMap<>();

    @Override
    public void save(Budget budget) {
        store.put(budget.getBudgetId(), budget);
    }

    @Override
    public Optional<Budget> findById(BudgetId budgetId) {
        return Optional.ofNullable(store.get(budgetId));
    }

    @Override
    public List<Budget> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Budget> findAllByUserId(String userId) {
        return store.values().stream()
            .filter(b -> userId.equals(b.getUserId()))
            .collect(Collectors.toList());
    }

    @Override
    public List<Budget> findByMonthAndYear(int month, int year) {
        return store.values().stream()
            .filter(b -> b.getMonth() == month && b.getYear() == year)
            .collect(Collectors.toList());
    }

    @Override
    public List<Budget> findByMonthAndYearAndUserId(int month, int year, String userId) {
        return store.values().stream()
            .filter(b -> b.getMonth() == month && b.getYear() == year && userId.equals(b.getUserId()))
            .collect(Collectors.toList());
    }

    @Override
    public void delete(BudgetId budgetId) {
        store.remove(budgetId);
    }

    @Override
    public boolean exists(BudgetId budgetId) {
        return store.containsKey(budgetId);
    }
}
