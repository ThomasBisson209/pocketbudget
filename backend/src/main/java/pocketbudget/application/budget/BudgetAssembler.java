package pocketbudget.application.budget;

import pocketbudget.application.budget.dtos.BudgetDto;
import pocketbudget.domain.budget.Budget;

import java.util.List;

public class BudgetAssembler {
    public BudgetDto toDto(Budget budget) {
        return new BudgetDto(
            budget.getBudgetId().getValue(),
            budget.getCategory().name(),
            budget.getMonthlyLimit(),
            budget.getCurrentSpent(),
            budget.getRemainingAmount(),
            budget.getMonth(),
            budget.getYear()
        );
    }

    public List<BudgetDto> toDtoList(List<Budget> budgets) {
        return budgets.stream().map(this::toDto).toList();
    }
}
