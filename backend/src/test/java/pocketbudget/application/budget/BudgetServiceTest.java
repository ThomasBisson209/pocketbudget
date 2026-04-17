package pocketbudget.application.budget;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pocketbudget.application.budget.dtos.BudgetDto;
import pocketbudget.application.budget.dtos.CreateBudgetDto;
import pocketbudget.domain.budget.Budget;
import pocketbudget.domain.budget.BudgetCategory;
import pocketbudget.domain.budget.BudgetId;
import pocketbudget.domain.budget.BudgetRepository;
import pocketbudget.domain.budget.exceptions.BudgetNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {
    @Mock
    private BudgetRepository budgetRepositoryMock;

    private BudgetService budgetService;

    @BeforeEach
    void setup() {
        budgetService = new BudgetService(budgetRepositoryMock, new BudgetAssembler());
    }

    @Test
    void givenValidDto_whenCreateBudget_thenBudgetIsSavedAndReturned() {
        CreateBudgetDto dto = new CreateBudgetDto();
        dto.category = "FOOD";
        dto.monthlyLimit = 400.0;
        dto.month = 4;
        dto.year = 2026;

        BudgetDto result = budgetService.createBudget(dto);

        verify(budgetRepositoryMock).save(any(Budget.class));
        assertEquals("FOOD", result.category);
        assertEquals(400.0, result.monthlyLimit);
        assertEquals(0.0, result.currentSpent);
    }

    @Test
    void givenUnknownBudgetId_whenGetBudget_thenThrowsBudgetNotFoundException() {
        when(budgetRepositoryMock.findById(any())).thenReturn(Optional.empty());

        assertThrows(BudgetNotFoundException.class, () -> budgetService.getBudget("unknown-id"));
    }

    @Test
    void givenBudgetsForMonth_whenGetBudgetsByMonth_thenReturnsFilteredList() {
        List<Budget> budgets = List.of(
            new Budget(BudgetId.generate(), BudgetCategory.FOOD, 300.0, 4, 2026),
            new Budget(BudgetId.generate(), BudgetCategory.TRANSPORT, 150.0, 4, 2026)
        );
        when(budgetRepositoryMock.findByMonthAndYear(4, 2026)).thenReturn(budgets);

        List<BudgetDto> result = budgetService.getBudgetsByMonth(4, 2026);

        assertEquals(2, result.size());
    }
}
