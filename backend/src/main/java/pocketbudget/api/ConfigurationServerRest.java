package pocketbudget.api;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import pocketbudget.application.account.AccountAssembler;
import pocketbudget.application.account.AccountService;
import pocketbudget.application.auth.AuthService;
import pocketbudget.application.budget.BudgetAssembler;
import pocketbudget.application.budget.BudgetService;
import pocketbudget.application.dashboard.DashboardService;
import pocketbudget.application.transaction.TransactionAssembler;
import pocketbudget.application.transaction.TransactionService;
import pocketbudget.domain.account.AccountRepository;
import pocketbudget.domain.budget.BudgetRepository;
import pocketbudget.domain.transaction.TransactionRepository;
import pocketbudget.domain.user.UserRepository;
import pocketbudget.infra.auth.JwtService;
import pocketbudget.infra.persistence.hibernate.*;
import pocketbudget.infra.persistence.inMemory.*;

public class ConfigurationServerRest extends ResourceConfig {
    private static AccountRepository accountRepositoryInstance;
    private static BudgetRepository budgetRepositoryInstance;
    private static UserRepository userRepositoryInstance;
    private static TransactionRepository transactionRepositoryInstance;

    public ConfigurationServerRest() {
        packages("pocketbudget.api");
        register(buildBinder());
    }

    public static void useInMemoryRepositories() {
        accountRepositoryInstance = new InMemoryAccountRepository();
        budgetRepositoryInstance = new InMemoryBudgetRepository();
        userRepositoryInstance = new InMemoryUserRepository();
        transactionRepositoryInstance = new InMemoryTransactionRepository();
    }

    private static AccountRepository getAccountRepository() {
        if (accountRepositoryInstance == null) accountRepositoryInstance = new HibernateAccountRepository();
        return accountRepositoryInstance;
    }

    private static BudgetRepository getBudgetRepository() {
        if (budgetRepositoryInstance == null) budgetRepositoryInstance = new HibernateBudgetRepository();
        return budgetRepositoryInstance;
    }

    private static UserRepository getUserRepository() {
        if (userRepositoryInstance == null) userRepositoryInstance = new HibernateUserRepository();
        return userRepositoryInstance;
    }

    private static TransactionRepository getTransactionRepository() {
        if (transactionRepositoryInstance == null) transactionRepositoryInstance = new HibernateTransactionRepository();
        return transactionRepositoryInstance;
    }

    private AbstractBinder buildBinder() {
        return new AbstractBinder() {
            @Override
            protected void configure() {
                bind(getAccountRepository()).to(AccountRepository.class);
                bind(getBudgetRepository()).to(BudgetRepository.class);
                bind(getUserRepository()).to(UserRepository.class);
                bind(getTransactionRepository()).to(TransactionRepository.class);
                bindAsContract(AccountAssembler.class);
                bindAsContract(BudgetAssembler.class);
                bindAsContract(TransactionAssembler.class);
                bindAsContract(AccountService.class);
                bindAsContract(BudgetService.class);
                bindAsContract(DashboardService.class);
                bindAsContract(TransactionService.class);
                bindAsContract(AuthService.class);
                bindAsContract(JwtService.class);
            }
        };
    }
}
