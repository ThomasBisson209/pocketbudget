package pocketbudget.domain.budget;

import jakarta.persistence.*;
import pocketbudget.domain.budget.exceptions.BudgetLimitExceededException;

@Entity
@Table(name = "budgets")
public class Budget {
    @EmbeddedId
    private BudgetId budgetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetCategory category;

    @Column(nullable = false)
    private double monthlyLimit;

    @Column(nullable = false)
    private double currentSpent;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private int year;

    protected Budget() {}

    public Budget(BudgetId budgetId, BudgetCategory category, double monthlyLimit, int month, int year) {
        if (monthlyLimit <= 0) {
            throw new IllegalArgumentException("Monthly limit must be positive");
        }
        this.budgetId = budgetId;
        this.category = category;
        this.monthlyLimit = monthlyLimit;
        this.currentSpent = 0;
        this.month = month;
        this.year = year;
    }

    public void addExpense(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Expense amount must be positive");
        }
        if (currentSpent + amount > monthlyLimit) {
            throw new BudgetLimitExceededException(category, monthlyLimit, currentSpent + amount);
        }
        this.currentSpent += amount;
    }

    public double getRemainingAmount() {
        return monthlyLimit - currentSpent;
    }

    public boolean isOverBudget() {
        return currentSpent > monthlyLimit;
    }

    public BudgetId getBudgetId() {
        return budgetId;
    }

    public BudgetCategory getCategory() {
        return category;
    }

    public double getMonthlyLimit() {
        return monthlyLimit;
    }

    public double getCurrentSpent() {
        return currentSpent;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}
