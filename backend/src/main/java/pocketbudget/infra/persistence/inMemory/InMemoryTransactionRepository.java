package pocketbudget.infra.persistence.inMemory;

import pocketbudget.domain.transaction.Transaction;
import pocketbudget.domain.transaction.TransactionId;
import pocketbudget.domain.transaction.TransactionRepository;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTransactionRepository implements TransactionRepository {
    private final Map<TransactionId, Transaction> store = new LinkedHashMap<>();

    @Override
    public void save(Transaction transaction) {
        store.put(transaction.getTransactionId(), transaction);
    }

    @Override
    public Optional<Transaction> findById(TransactionId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> all = new ArrayList<>(store.values());
        all.sort(Comparator.comparing(Transaction::getDate).reversed());
        return all;
    }

    @Override
    public List<Transaction> findAllByUserId(String userId) {
        return store.values().stream()
            .filter(t -> userId.equals(t.getUserId()))
            .sorted(Comparator.comparing(Transaction::getDate).reversed())
            .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByAccountId(String accountId) {
        return store.values().stream()
            .filter(t -> t.getAccountId().equals(accountId))
            .sorted(Comparator.comparing(Transaction::getDate).reversed())
            .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByAccountIdAndUserId(String accountId, String userId) {
        return store.values().stream()
            .filter(t -> t.getAccountId().equals(accountId) && userId.equals(t.getUserId()))
            .sorted(Comparator.comparing(Transaction::getDate).reversed())
            .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findRecentN(int n) {
        return store.values().stream()
            .sorted(Comparator.comparing(Transaction::getDate).reversed())
            .limit(n)
            .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findRecentNByUserId(int n, String userId) {
        return store.values().stream()
            .filter(t -> userId.equals(t.getUserId()))
            .sorted(Comparator.comparing(Transaction::getDate).reversed())
            .limit(n)
            .collect(Collectors.toList());
    }
}
