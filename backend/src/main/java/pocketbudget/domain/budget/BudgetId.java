package pocketbudget.domain.budget;

import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class BudgetId {
    private String value;

    protected BudgetId() {}

    public BudgetId(String value) {
        this.value = value;
    }

    public static BudgetId generate() {
        return new BudgetId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BudgetId)) return false;
        return Objects.equals(value, ((BudgetId) o).value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
