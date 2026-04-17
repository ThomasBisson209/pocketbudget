package pocketbudget.domain.account;

import jakarta.persistence.*;
import pocketbudget.domain.account.exceptions.InvalidBalanceException;

@Entity
@Table(name = "accounts")
public class Account {
    @EmbeddedId
    private AccountId accountId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @Column(nullable = false)
    private double balance;

    @Column(nullable = false)
    private String userId;

    protected Account() {}

    public Account(AccountId accountId, String name, AccountType type, double initialBalance, String userId) {
        if (initialBalance < 0) {
            throw new InvalidBalanceException("Initial balance cannot be negative");
        }
        this.accountId = accountId;
        this.name = name;
        this.type = type;
        this.balance = initialBalance;
        this.userId = userId;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new InvalidBalanceException("Deposit amount must be positive");
        }
        this.balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new InvalidBalanceException("Withdrawal amount must be positive");
        }
        if (amount > this.balance) {
            throw new InvalidBalanceException("Insufficient funds");
        }
        this.balance -= amount;
    }

    public AccountId getAccountId() { return accountId; }
    public String getName() { return name; }
    public AccountType getType() { return type; }
    public double getBalance() { return balance; }
    public String getUserId() { return userId; }
}
