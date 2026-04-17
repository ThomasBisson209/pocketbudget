package pocketbudget.domain.budget.exceptions;

import pocketbudget.domain.budget.BudgetCategory;

public class BudgetLimitExceededException extends RuntimeException {
    public BudgetLimitExceededException(BudgetCategory category, double limit, double attempted) {
        super(String.format("Budget limit of %.2f exceeded for category %s (attempted: %.2f)", limit, category, attempted));
    }
}
