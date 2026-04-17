package pocketbudget.domain.budget;

import org.junit.jupiter.api.Test;
import pocketbudget.domain.budget.exceptions.BudgetLimitExceededException;

import static org.junit.jupiter.api.Assertions.*;

class BudgetTest {

    @Test
    void givenValidData_whenCreatingBudget_thenBudgetIsCreated() {
        Budget budget = new Budget(BudgetId.generate(), BudgetCategory.FOOD, 300.0, 4, 2026, "user1");
        assertEquals(300.0, budget.getMonthlyLimit());
        assertEquals(0.0, budget.getCurrentSpent());
        assertEquals(300.0, budget.getRemainingAmount());
        assertFalse(budget.isOverBudget());
    }

    @Test
    void givenZeroLimit_whenCreatingBudget_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> new Budget(BudgetId.generate(), BudgetCategory.FOOD, 0, 4, 2026, "user1"));
    }

    @Test
    void givenExpenseWithinLimit_whenAddExpense_thenSpentIncreases() {
        Budget budget = new Budget(BudgetId.generate(), BudgetCategory.FOOD, 300.0, 4, 2026, "user1");
        budget.addExpense(100.0);
        assertEquals(100.0, budget.getCurrentSpent());
        assertEquals(200.0, budget.getRemainingAmount());
    }

    @Test
    void givenExpenseExceedsLimit_whenAddExpense_thenThrowsBudgetLimitExceededException() {
        Budget budget = new Budget(BudgetId.generate(), BudgetCategory.TRANSPORT, 100.0, 4, 2026, "user1");
        assertThrows(BudgetLimitExceededException.class, () -> budget.addExpense(150.0));
    }
}
