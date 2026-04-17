package pocketbudget.application.dashboard;

import jakarta.inject.Inject;
import pocketbudget.application.dashboard.dtos.DashboardDto;
import pocketbudget.domain.account.Account;
import pocketbudget.domain.account.AccountRepository;
import pocketbudget.domain.budget.Budget;
import pocketbudget.domain.budget.BudgetRepository;
import pocketbudget.domain.transaction.Transaction;
import pocketbudget.domain.transaction.TransactionRepository;

import java.util.List;

public class DashboardService {
    private final AccountRepository accountRepository;
    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    @Inject
    public DashboardService(AccountRepository accountRepository,
                            BudgetRepository budgetRepository,
                            TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
    }

    public DashboardDto getDashboard(int month, int year) {
        List<Account> accounts = accountRepository.findAll();
        List<Budget> budgets = budgetRepository.findByMonthAndYear(month, year);
        List<Transaction> recent = transactionRepository.findRecentN(5);

        double totalBalance = accounts.stream().mapToDouble(Account::getBalance).sum();
        double totalBudgeted = budgets.stream().mapToDouble(Budget::getMonthlyLimit).sum();
        double totalSpent = budgets.stream().mapToDouble(Budget::getCurrentSpent).sum();
        double totalRemaining = totalBudgeted - totalSpent;

        List<DashboardDto.BudgetSummaryDto> summaries = budgets.stream()
            .map(b -> new DashboardDto.BudgetSummaryDto(
                b.getCategory().name(), b.getMonthlyLimit(), b.getCurrentSpent(), b.getRemainingAmount()))
            .toList();

        List<DashboardDto.RecentTransactionDto> recentDtos = recent.stream()
            .map(t -> new DashboardDto.RecentTransactionDto(
                t.getTransactionId().getValue(),
                t.getDescription(),
                t.getAmount(),
                t.getType().name(),
                t.getDate().toString(),
                t.getAccountId(),
                t.getBudgetCategory()))
            .toList();

        return new DashboardDto(totalBalance, accounts.size(), totalBudgeted, totalSpent, totalRemaining, summaries, recentDtos);
    }
}
