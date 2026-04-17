package pocketbudget.application.budget;

import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pocketbudget.application.budget.dtos.BudgetDto;
import pocketbudget.application.budget.dtos.CreateBudgetDto;
import pocketbudget.domain.budget.Budget;
import pocketbudget.domain.budget.BudgetCategory;
import pocketbudget.domain.budget.BudgetId;
import pocketbudget.domain.budget.BudgetRepository;
import pocketbudget.domain.budget.exceptions.BudgetNotFoundException;

import java.util.List;

public class BudgetService {
    private static final Logger log = LoggerFactory.getLogger(BudgetService.class);

    private final BudgetRepository budgetRepository;
    private final BudgetAssembler budgetAssembler;

    @Inject
    public BudgetService(BudgetRepository budgetRepository, BudgetAssembler budgetAssembler) {
        this.budgetRepository = budgetRepository;
        this.budgetAssembler = budgetAssembler;
    }

    public BudgetDto createBudget(CreateBudgetDto dto) {
        Budget budget = new Budget(
            BudgetId.generate(),
            BudgetCategory.fromString(dto.category),
            dto.monthlyLimit,
            dto.month,
            dto.year
        );
        budgetRepository.save(budget);
        log.info("Budget created: category={}, limit={}, period={}/{}", dto.category, dto.monthlyLimit, dto.month, dto.year);
        return budgetAssembler.toDto(budget);
    }

    public BudgetDto getBudget(String budgetId) {
        Budget budget = budgetRepository.findById(new BudgetId(budgetId))
            .orElseThrow(() -> new BudgetNotFoundException(budgetId));
        return budgetAssembler.toDto(budget);
    }

    public List<BudgetDto> getAllBudgets() {
        return budgetAssembler.toDtoList(budgetRepository.findAll());
    }

    public List<BudgetDto> getBudgetsByMonth(int month, int year) {
        return budgetAssembler.toDtoList(budgetRepository.findByMonthAndYear(month, year));
    }

    public void deleteBudget(String budgetId) {
        if (!budgetRepository.exists(new BudgetId(budgetId))) {
            throw new BudgetNotFoundException(budgetId);
        }
        budgetRepository.delete(new BudgetId(budgetId));
        log.info("Budget deleted: id={}", budgetId);
    }
}
