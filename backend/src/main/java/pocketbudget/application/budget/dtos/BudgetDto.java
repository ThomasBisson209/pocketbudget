package pocketbudget.application.budget.dtos;

public class BudgetDto {
    public String budgetId;
    public String category;
    public double monthlyLimit;
    public double currentSpent;
    public double remainingAmount;
    public int month;
    public int year;

    public BudgetDto() {}

    public BudgetDto(String budgetId, String category, double monthlyLimit, double currentSpent, double remainingAmount, int month, int year) {
        this.budgetId = budgetId;
        this.category = category;
        this.monthlyLimit = monthlyLimit;
        this.currentSpent = currentSpent;
        this.remainingAmount = remainingAmount;
        this.month = month;
        this.year = year;
    }
}
