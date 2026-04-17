package pocketbudget.domain.transaction;

import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class TransactionId {
    private String value;

    protected TransactionId() {}

    public TransactionId(String value) {
        this.value = value;
    }

    public static TransactionId generate() {
        return new TransactionId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionId)) return false;
        return Objects.equals(value, ((TransactionId) o).value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
