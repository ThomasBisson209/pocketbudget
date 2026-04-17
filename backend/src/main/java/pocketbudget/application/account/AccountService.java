package pocketbudget.application.account;

import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pocketbudget.application.account.dtos.AccountDto;
import pocketbudget.application.account.dtos.CreateAccountDto;
import pocketbudget.domain.account.Account;
import pocketbudget.domain.account.AccountId;
import pocketbudget.domain.account.AccountRepository;
import pocketbudget.domain.account.AccountType;
import pocketbudget.domain.account.exceptions.AccountNotFoundException;

import java.util.List;

public class AccountService {
    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final AccountAssembler accountAssembler;

    @Inject
    public AccountService(AccountRepository accountRepository, AccountAssembler accountAssembler) {
        this.accountRepository = accountRepository;
        this.accountAssembler = accountAssembler;
    }

    public AccountDto createAccount(CreateAccountDto dto) {
        Account account = new Account(
            AccountId.generate(),
            dto.name,
            AccountType.fromString(dto.type),
            dto.initialBalance
        );
        accountRepository.save(account);
        log.info("Account created: name={}, type={}, balance={}", dto.name, dto.type, dto.initialBalance);
        return accountAssembler.toDto(account);
    }

    public AccountDto getAccount(String accountId) {
        Account account = accountRepository.findById(new AccountId(accountId))
            .orElseThrow(() -> new AccountNotFoundException(accountId));
        return accountAssembler.toDto(account);
    }

    public List<AccountDto> getAllAccounts() {
        return accountAssembler.toDtoList(accountRepository.findAll());
    }

    public void deleteAccount(String accountId) {
        if (!accountRepository.exists(new AccountId(accountId))) {
            throw new AccountNotFoundException(accountId);
        }
        accountRepository.delete(new AccountId(accountId));
        log.info("Account deleted: id={}", accountId);
    }
}
