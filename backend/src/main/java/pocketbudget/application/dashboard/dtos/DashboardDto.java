package pocketbudget.application.dashboard.dtos;

import java.util.List;

public class DashboardDto {
    public double totalBalance;
    public int totalAccounts;
    public double totalBudgeted;
    public double totalSpent;
    public double totalRemaining;
    public List<BudgetSummaryDto> budgetSummaries;
    public List<RecentTransactionDto> recentTransactions;

    public DashboardDto() {}

    public DashboardDto(double totalBalance, int totalAccounts, double totalBudgeted,
                        double totalSpent, double totalRemaining,
                        List<BudgetSummaryDto> budgetSummaries,
                        List<RecentTransactionDto> recentTransactions) {
        this.totalBalance = totalBalance;
        this.totalAccounts = totalAccounts;
        this.totalBudgeted = totalBudgeted;
        this.totalSpent = totalSpent;
        this.totalRemaining = totalRemaining;
        this.budgetSummaries = budgetSummaries;
        this.recentTransactions = recentTransactions;
    }

    public static class BudgetSummaryDto {
        public String category;
        public double limit;
        public double spent;
        public double remaining;

        public BudgetSummaryDto() {}

        public BudgetSummaryDto(String category, double limit, double spent, double remaining) {
            this.category = category;
            this.limit = limit;
            this.spent = spent;
            this.remaining = remaining;
        }
    }

    public static class RecentTransactionDto {
        public String transactionId;
        public String description;
        public double amount;
        public String type;
        public String date;
        public String accountId;
        public String budgetCategory;

        public RecentTransactionDto() {}

        public RecentTransactionDto(String transactionId, String description, double amount,
                                    String type, String date, String accountId, String budgetCategory) {
            this.transactionId = transactionId;
            this.description = description;
            this.amount = amount;
            this.type = type;
            this.date = date;
            this.accountId = accountId;
            this.budgetCategory = budgetCategory;
        }
    }
}
