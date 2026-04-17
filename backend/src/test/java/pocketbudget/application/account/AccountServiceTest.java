package pocketbudget.application.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pocketbudget.application.account.dtos.AccountDto;
import pocketbudget.application.account.dtos.CreateAccountDto;
import pocketbudget.domain.account.Account;
import pocketbudget.domain.account.AccountId;
import pocketbudget.domain.account.AccountRepository;
import pocketbudget.domain.account.AccountType;
import pocketbudget.domain.account.exceptions.AccountNotFoundException;
import pocketbudget.domain.transaction.TransactionRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepositoryMock;
    @Mock
    private TransactionRepository transactionRepositoryMock;

    private AccountService accountService;

    @BeforeEach
    void setup() {
        accountService = new AccountService(accountRepositoryMock, new AccountAssembler(), transactionRepositoryMock);
    }

    @Test
    void givenValidDto_whenCreateAccount_thenAccountIsSavedAndReturned() {
        CreateAccountDto dto = new CreateAccountDto();
        dto.name = "My Savings";
        dto.type = "SAVINGS";
        dto.initialBalance = 1000.0;

        AccountDto result = accountService.createAccount(dto);

        verify(accountRepositoryMock).save(any(Account.class));
        assertEquals("My Savings", result.name);
        assertEquals("SAVINGS", result.type);
        assertEquals(1000.0, result.balance);
    }

    @Test
    void givenExistingAccountId_whenGetAccount_thenReturnsAccountDto() {
        AccountId id = AccountId.generate();
        Account account = new Account(id, "Checking", AccountType.CHECKING, 500.0);
        when(accountRepositoryMock.findById(id)).thenReturn(Optional.of(account));

        AccountDto result = accountService.getAccount(id.getValue());

        assertEquals("Checking", result.name);
        assertEquals(500.0, result.balance);
    }

    @Test
    void givenUnknownAccountId_whenGetAccount_thenThrowsAccountNotFoundException() {
        when(accountRepositoryMock.findById(any())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.getAccount("unknown-id"));
    }

    @Test
    void givenAccounts_whenGetAllAccounts_thenReturnsDtoList() {
        List<Account> accounts = List.of(
            new Account(AccountId.generate(), "A1", AccountType.CHECKING, 100.0),
            new Account(AccountId.generate(), "A2", AccountType.SAVINGS, 200.0)
        );
        when(accountRepositoryMock.findAll()).thenReturn(accounts);

        List<AccountDto> result = accountService.getAllAccounts();

        assertEquals(2, result.size());
    }

    @Test
    void givenExistingAccount_whenDeleteAccount_thenRepositoryDeleteIsCalled() {
        AccountId id = AccountId.generate();
        when(accountRepositoryMock.exists(id)).thenReturn(true);

        accountService.deleteAccount(id.getValue());

        verify(accountRepositoryMock).delete(any(AccountId.class));
    }

    @Test
    void givenUnknownAccount_whenDeleteAccount_thenThrowsAccountNotFoundException() {
        when(accountRepositoryMock.exists(any())).thenReturn(false);

        assertThrows(AccountNotFoundException.class, () -> accountService.deleteAccount("unknown-id"));
    }
}
