package pocketbudget.application.transaction.dtos;

public class TransactionDto {
    public String transactionId;
    public String accountId;
    public String budgetCategory;
    public String description;
    public double amount;
    public String date;
    public String type;

    public TransactionDto() {}

    public TransactionDto(String transactionId, String accountId, String budgetCategory,
                          String description, double amount, String date, String type) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.budgetCategory = budgetCategory;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.type = type;
    }
}
