package pocketbudget.domain.transaction;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
public class Transaction {
    @EmbeddedId
    private TransactionId transactionId;

    @Column(nullable = false)
    private String accountId;

    @Column(name = "user_id")
    private String userId;

    @Column
    private String budgetCategory;

    @Column
    private String description;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    protected Transaction() {}

    public Transaction(TransactionId transactionId, String accountId, String userId,
                       String budgetCategory, String description, double amount,
                       LocalDate date, TransactionType type) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.userId = userId;
        this.budgetCategory = budgetCategory;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.type = type;
    }

    public TransactionId getTransactionId() { return transactionId; }
    public String getAccountId() { return accountId; }
    public String getUserId() { return userId; }
    public String getBudgetCategory() { return budgetCategory; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public TransactionType getType() { return type; }
}
