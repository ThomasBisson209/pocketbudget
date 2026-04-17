package pocketbudget.domain.budget.exceptions;

public class BudgetNotFoundException extends RuntimeException {
    public BudgetNotFoundException(String budgetId) {
        super("Budget not found: " + budgetId);
    }
}
