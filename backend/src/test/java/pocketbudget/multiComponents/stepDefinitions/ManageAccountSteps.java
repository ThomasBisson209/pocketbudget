package pocketbudget.multiComponents.stepDefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.fr.*;
import pocketbudget.api.ConfigurationServerRest;
import pocketbudget.application.account.AccountAssembler;
import pocketbudget.application.account.AccountService;
import pocketbudget.application.account.dtos.AccountDto;
import pocketbudget.application.account.dtos.CreateAccountDto;
import pocketbudget.domain.account.exceptions.AccountNotFoundException;
import pocketbudget.infra.persistence.inMemory.InMemoryAccountRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ManageAccountSteps {
    private AccountService accountService;
    private AccountDto createdAccount;
    private Exception thrownException;

    @Before
    public void setup() {
        ConfigurationServerRest.useInMemoryRepositories();
        accountService = new AccountService(new InMemoryAccountRepository(), new AccountAssembler());
    }

    @Etantdonné("aucun compte existant")
    public void givenNoExistingAccounts() {
        // état initial vide
    }

    @Quand("je crée un compte {string} de type {string} avec un solde de {double}")
    public void whenICreateAccount(String name, String type, double balance) {
        CreateAccountDto dto = new CreateAccountDto();
        dto.name = name;
        dto.type = type;
        dto.initialBalance = balance;
        createdAccount = accountService.createAccount(dto);
    }

    @Alors("le compte {string} devrait exister avec un solde de {double}")
    public void thenAccountShouldExist(String name, double expectedBalance) {
        assertNotNull(createdAccount);
        assertEquals(name, createdAccount.name);
        assertEquals(expectedBalance, createdAccount.balance);
    }

    @Quand("je supprime le compte créé")
    public void whenIDeleteTheCreatedAccount() {
        try {
            accountService.deleteAccount(createdAccount.accountId);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Alors("le compte ne devrait plus exister")
    public void thenAccountShouldNotExist() {
        assertThrows(AccountNotFoundException.class,
            () -> accountService.getAccount(createdAccount.accountId));
    }
}
