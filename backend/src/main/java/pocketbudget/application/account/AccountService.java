package pocketbudget.application.account;

import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pocketbudget.application.account.dtos.AccountDto;
import pocketbudget.application.account.dtos.BalanceHistoryDto;
import pocketbudget.application.account.dtos.CreateAccountDto;
import pocketbudget.domain.account.Account;
import pocketbudget.domain.account.AccountId;
import pocketbudget.domain.account.AccountRepository;
import pocketbudget.domain.account.AccountType;
import pocketbudget.domain.account.exceptions.AccountNotFoundException;
import pocketbudget.domain.transaction.Transaction;
import pocketbudget.domain.transaction.TransactionRepository;
import pocketbudget.domain.transaction.TransactionType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AccountService {
    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final AccountAssembler accountAssembler;
    private final TransactionRepository transactionRepository;

    @Inject
    public AccountService(AccountRepository accountRepository, AccountAssembler accountAssembler, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.accountAssembler = accountAssembler;
        this.transactionRepository = transactionRepository;
    }

    public AccountDto createAccount(CreateAccountDto dto, String userId) {
        Account account = new Account(
            AccountId.generate(),
            dto.name,
            AccountType.fromString(dto.type),
            dto.initialBalance,
            userId
        );
        accountRepository.save(account);
        log.info("Account created: name={}, type={}, balance={}, userId={}", dto.name, dto.type, dto.initialBalance, userId);
        return accountAssembler.toDto(account);
    }

    public AccountDto getAccount(String accountId, String userId) {
        Account account = accountRepository.findById(new AccountId(accountId))
            .filter(a -> userId.equals(a.getUserId()))
            .orElseThrow(() -> new AccountNotFoundException(accountId));
        return accountAssembler.toDto(account);
    }

    public List<AccountDto> getAllAccounts(String userId) {
        return accountAssembler.toDtoList(accountRepository.findAllByUserId(userId));
    }

    public void deleteAccount(String accountId, String userId) {
        Account account = accountRepository.findById(new AccountId(accountId))
            .filter(a -> userId.equals(a.getUserId()))
            .orElseThrow(() -> new AccountNotFoundException(accountId));
        accountRepository.delete(account.getAccountId());
        log.info("Account deleted: id={}, userId={}", accountId, userId);
    }

    public BalanceHistoryDto getBalanceHistory(String accountId, int month, int year, String userId) {
        Account account = accountRepository.findById(new AccountId(accountId))
            .filter(a -> userId.equals(a.getUserId()))
            .orElseThrow(() -> new AccountNotFoundException(accountId));

        double currentBalance = account.getBalance();
        List<Transaction> allTx = transactionRepository.findByAccountIdAndUserId(accountId, userId);

        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        double netAfter = allTx.stream()
            .filter(t -> t.getDate().isAfter(lastDay))
            .mapToDouble(t -> t.getType() == TransactionType.CREDIT ? t.getAmount() : -t.getAmount())
            .sum();

        double netInMonth = allTx.stream()
            .filter(t -> !t.getDate().isBefore(firstDay) && !t.getDate().isAfter(lastDay))
            .mapToDouble(t -> t.getType() == TransactionType.CREDIT ? t.getAmount() : -t.getAmount())
            .sum();

        double startBalance = currentBalance - netAfter - netInMonth;

        Map<LocalDate, List<Transaction>> byDate = allTx.stream()
            .filter(t -> !t.getDate().isBefore(firstDay) && !t.getDate().isAfter(lastDay))
            .collect(Collectors.groupingBy(Transaction::getDate));

        List<BalanceHistoryDto.DataPoint> points = new ArrayList<>();
        double balance = startBalance;
        for (int day = 1; day <= lastDay.getDayOfMonth(); day++) {
            LocalDate date = LocalDate.of(year, month, day);
            List<Transaction> dayTx = byDate.getOrDefault(date, List.of());
            for (Transaction t : dayTx) {
                balance += t.getType() == TransactionType.CREDIT ? t.getAmount() : -t.getAmount();
            }
            points.add(new BalanceHistoryDto.DataPoint(date.toString(), Math.round(balance * 100.0) / 100.0));
        }

        log.info("Balance history fetched: accountId={}, month={}/{}, userId={}", accountId, month, year, userId);
        return new BalanceHistoryDto(accountId, account.getName(), points);
    }
}
