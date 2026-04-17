package pocketbudget.application.transaction;

import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pocketbudget.application.transaction.dtos.CreateTransactionDto;
import pocketbudget.application.transaction.dtos.TransactionDto;
import pocketbudget.domain.account.Account;
import pocketbudget.domain.account.AccountId;
import pocketbudget.domain.account.AccountRepository;
import pocketbudget.domain.account.exceptions.AccountNotFoundException;
import pocketbudget.domain.budget.Budget;
import pocketbudget.domain.budget.BudgetRepository;
import pocketbudget.domain.budget.exceptions.BudgetLimitExceededException;
import pocketbudget.domain.transaction.Transaction;
import pocketbudget.domain.transaction.TransactionId;
import pocketbudget.domain.transaction.TransactionRepository;
import pocketbudget.domain.transaction.TransactionType;
import pocketbudget.domain.transaction.exceptions.TransactionNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final BudgetRepository budgetRepository;
    private final TransactionAssembler assembler;

    @Inject
    public TransactionService(TransactionRepository transactionRepository,
                               AccountRepository accountRepository,
                               BudgetRepository budgetRepository,
                               TransactionAssembler assembler) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.budgetRepository = budgetRepository;
        this.assembler = assembler;
    }

    public TransactionDto createTransaction(CreateTransactionDto dto, String userId) {
        // Verify the account belongs to this user
        Account account = accountRepository.findById(new AccountId(dto.accountId))
            .filter(a -> userId.equals(a.getUserId()))
            .orElseThrow(() -> new AccountNotFoundException(dto.accountId));

        LocalDate date = LocalDate.parse(dto.date);
        TransactionType type = TransactionType.fromString(dto.type);

        // Pre-validate budget before touching account balance (user-scoped)
        Budget matchingBudget = null;
        if (type == TransactionType.DEBIT && dto.budgetCategory != null && !dto.budgetCategory.isBlank()) {
            Optional<Budget> budgetOpt = budgetRepository
                .findByMonthAndYearAndUserId(date.getMonthValue(), date.getYear(), userId)
                .stream()
                .filter(b -> b.getCategory().name().equalsIgnoreCase(dto.budgetCategory))
                .findFirst();

            if (budgetOpt.isPresent()) {
                Budget b = budgetOpt.get();
                if (b.getCurrentSpent() + dto.amount > b.getMonthlyLimit()) {
                    throw new BudgetLimitExceededException(b.getCategory(), b.getMonthlyLimit(), b.getCurrentSpent() + dto.amount);
                }
                matchingBudget = b;
            }
        }

        // Apply side effects
        if (type == TransactionType.DEBIT) {
            account.withdraw(dto.amount);
            if (matchingBudget != null) {
                matchingBudget.addExpense(dto.amount);
                budgetRepository.save(matchingBudget);
            }
        } else {
            account.deposit(dto.amount);
        }
        accountRepository.save(account);

        Transaction transaction = new Transaction(
            TransactionId.generate(),
            dto.accountId,
            userId,
            dto.budgetCategory,
            dto.description,
            dto.amount,
            date,
            type
        );
        transactionRepository.save(transaction);
        log.info("Transaction created: type={}, amount={}, account={}, category={}, userId={}", type, dto.amount, dto.accountId, dto.budgetCategory, userId);
        return assembler.toDto(transaction);
    }

    public TransactionDto getTransaction(String id, String userId) {
        Transaction t = transactionRepository.findById(new TransactionId(id))
            .filter(tx -> userId.equals(tx.getUserId()))
            .orElseThrow(() -> new TransactionNotFoundException(id));
        return assembler.toDto(t);
    }

    public List<TransactionDto> getAllTransactions(String userId) {
        return assembler.toDtoList(transactionRepository.findAllByUserId(userId));
    }

    public List<TransactionDto> getTransactionsByAccount(String accountId, String userId) {
        // Verify the account belongs to this user first
        accountRepository.findById(new AccountId(accountId))
            .filter(a -> userId.equals(a.getUserId()))
            .orElseThrow(() -> new AccountNotFoundException(accountId));
        return assembler.toDtoList(transactionRepository.findByAccountIdAndUserId(accountId, userId));
    }
}
