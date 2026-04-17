package pocketbudget.domain.transaction;

public enum TransactionType {
    DEBIT,
    CREDIT;

    public static TransactionType fromString(String value) {
        for (TransactionType t : values()) {
            if (t.name().equalsIgnoreCase(value)) return t;
        }
        throw new IllegalArgumentException("Unknown transaction type: " + value);
    }
}
