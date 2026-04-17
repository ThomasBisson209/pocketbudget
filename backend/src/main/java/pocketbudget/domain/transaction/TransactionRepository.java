package pocketbudget.domain.transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    void save(Transaction transaction);
    Optional<Transaction> findById(TransactionId transactionId);
    List<Transaction> findAll();
    List<Transaction> findAllByUserId(String userId);
    List<Transaction> findByAccountId(String accountId);
    List<Transaction> findByAccountIdAndUserId(String accountId, String userId);
    List<Transaction> findRecentN(int n);
    List<Transaction> findRecentNByUserId(int n, String userId);
}
