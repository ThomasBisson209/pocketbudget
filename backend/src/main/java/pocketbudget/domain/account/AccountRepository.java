package pocketbudget.domain.account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    void save(Account account);
    Optional<Account> findById(AccountId accountId);
    List<Account> findAll();
    void delete(AccountId accountId);
    boolean exists(AccountId accountId);
}
