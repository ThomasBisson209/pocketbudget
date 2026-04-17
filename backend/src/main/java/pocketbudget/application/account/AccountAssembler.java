package pocketbudget.application.account;

import pocketbudget.application.account.dtos.AccountDto;
import pocketbudget.domain.account.Account;

import java.util.List;

public class AccountAssembler {
    public AccountDto toDto(Account account) {
        return new AccountDto(
            account.getAccountId().getValue(),
            account.getName(),
            account.getType().name(),
            account.getBalance()
        );
    }

    public List<AccountDto> toDtoList(List<Account> accounts) {
        return accounts.stream().map(this::toDto).toList();
    }
}
