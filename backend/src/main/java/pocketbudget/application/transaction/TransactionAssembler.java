package pocketbudget.application.transaction;

import pocketbudget.application.transaction.dtos.TransactionDto;
import pocketbudget.domain.transaction.Transaction;

import java.util.List;

public class TransactionAssembler {
    public TransactionDto toDto(Transaction t) {
        return new TransactionDto(
            t.getTransactionId().getValue(),
            t.getAccountId(),
            t.getBudgetCategory(),
            t.getDescription(),
            t.getAmount(),
            t.getDate().toString(),
            t.getType().name()
        );
    }

    public List<TransactionDto> toDtoList(List<Transaction> list) {
        return list.stream().map(this::toDto).toList();
    }
}
