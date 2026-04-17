package pocketbudget.infra.persistence.inMemory;

import pocketbudget.domain.account.Account;
import pocketbudget.domain.account.AccountId;
import pocketbudget.domain.account.AccountRepository;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryAccountRepository implements AccountRepository {
    private final Map<AccountId, Account> store = new HashMap<>();

    @Override
    public void save(Account account) {
        store.put(account.getAccountId(), account);
    }

    @Override
    public Optional<Account> findById(AccountId accountId) {
        return Optional.ofNullable(store.get(accountId));
    }

    @Override
    public List<Account> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Account> findAllByUserId(String userId) {
        return store.values().stream()
            .filter(a -> userId.equals(a.getUserId()))
            .collect(Collectors.toList());
    }

    @Override
    public void delete(AccountId accountId) {
        store.remove(accountId);
    }

    @Override
    public boolean exists(AccountId accountId) {
        return store.containsKey(accountId);
    }
}
