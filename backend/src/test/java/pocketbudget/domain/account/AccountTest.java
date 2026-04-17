package pocketbudget.domain.account;

import org.junit.jupiter.api.Test;
import pocketbudget.domain.account.exceptions.InvalidBalanceException;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void givenPositiveInitialBalance_whenCreatingAccount_thenAccountIsCreated() {
        Account account = new Account(AccountId.generate(), "Checking", AccountType.CHECKING, 500.0, "user1");
        assertEquals(500.0, account.getBalance());
        assertEquals("Checking", account.getName());
        assertEquals(AccountType.CHECKING, account.getType());
    }

    @Test
    void givenNegativeInitialBalance_whenCreatingAccount_thenThrowsInvalidBalanceException() {
        assertThrows(InvalidBalanceException.class,
            () -> new Account(AccountId.generate(), "Bad", AccountType.CASH, -100.0, "user1"));
    }

    @Test
    void givenValidAmount_whenDeposit_thenBalanceIncreases() {
        Account account = new Account(AccountId.generate(), "Savings", AccountType.SAVINGS, 100.0, "user1");
        account.deposit(50.0);
        assertEquals(150.0, account.getBalance());
    }

    @Test
    void givenZeroAmount_whenDeposit_thenThrowsInvalidBalanceException() {
        Account account = new Account(AccountId.generate(), "Savings", AccountType.SAVINGS, 100.0, "user1");
        assertThrows(InvalidBalanceException.class, () -> account.deposit(0));
    }

    @Test
    void givenSufficientFunds_whenWithdraw_thenBalanceDecreases() {
        Account account = new Account(AccountId.generate(), "Checking", AccountType.CHECKING, 200.0, "user1");
        account.withdraw(80.0);
        assertEquals(120.0, account.getBalance());
    }

    @Test
    void givenInsufficientFunds_whenWithdraw_thenThrowsInvalidBalanceException() {
        Account account = new Account(AccountId.generate(), "Checking", AccountType.CHECKING, 50.0, "user1");
        assertThrows(InvalidBalanceException.class, () -> account.withdraw(100.0));
    }
}
