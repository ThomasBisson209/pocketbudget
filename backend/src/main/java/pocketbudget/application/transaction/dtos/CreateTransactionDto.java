package pocketbudget.application.transaction.dtos;

public class CreateTransactionDto {
    public String accountId;
    public String budgetCategory;
    public String description;
    public double amount;
    public String date;
    public String type;

    public CreateTransactionDto() {}
}
