package pocketbudget.domain.user;

import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class UserId {
    private String value;

    protected UserId() {}

    public UserId(String value) {
        this.value = value;
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserId)) return false;
        return Objects.equals(value, ((UserId) o).value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
