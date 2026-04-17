package pocketbudget.domain.account;

public enum AccountType {
    CHECKING,
    SAVINGS,
    CASH;

    public static AccountType fromString(String value) {
        for (AccountType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown account type: " + value);
    }
}
