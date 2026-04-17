package pocketbudget.application.account.dtos;

public class AccountDto {
    public String accountId;
    public String name;
    public String type;
    public double balance;

    public AccountDto() {}

    public AccountDto(String accountId, String name, String type, double balance) {
        this.accountId = accountId;
        this.name = name;
        this.type = type;
        this.balance = balance;
    }
}
