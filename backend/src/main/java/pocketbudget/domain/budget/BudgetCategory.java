package pocketbudget.domain.budget;

public enum BudgetCategory {
    FOOD,
    TRANSPORT,
    HOUSING,
    ENTERTAINMENT,
    HEALTH,
    EDUCATION,
    SAVINGS,
    OTHER;

    public static BudgetCategory fromString(String value) {
        for (BudgetCategory category : values()) {
            if (category.name().equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown budget category: " + value);
    }
}
