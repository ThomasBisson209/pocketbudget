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

    public TransactionDto createTransaction(CreateTransactionDto dto) {
        Account account = accountRepository.findById(new AccountId(dto.accountId))
            .orElseThrow(() -> new AccountNotFoundException(dto.accountId));

        LocalDate date = LocalDate.parse(dto.date);
        TransactionType type = TransactionType.fromString(dto.type);

        // Pre-validate budget before touching account balance
        Budget matchingBudget = null;
        if (type == TransactionType.DEBIT && dto.budgetCategory != null && !dto.budgetCategory.isBlank()) {
            Optional<Budget> budgetOpt = budgetRepository
                .findByMonthAndYear(date.getMonthValue(), date.getYear())
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
            dto.budgetCategory,
            dto.description,
            dto.amount,
            date,
            type
        );
        transactionRepository.save(transaction);
        log.info("Transaction created: type={}, amount={}, account={}, category={}", type, dto.amount, dto.accountId, dto.budgetCategory);
        return assembler.toDto(transaction);
    }

    public TransactionDto getTransaction(String id) {
        return assembler.toDto(
            transactionRepository.findById(new TransactionId(id))
                .orElseThrow(() -> new TransactionNotFoundException(id))
        );
    }

    public List<TransactionDto> getAllTransactions() {
        return assembler.toDtoList(transactionRepository.findAll());
    }

    public List<TransactionDto> getTransactionsByAccount(String accountId) {
        return assembler.toDtoList(transactionRepository.findByAccountId(accountId));
    }
}
