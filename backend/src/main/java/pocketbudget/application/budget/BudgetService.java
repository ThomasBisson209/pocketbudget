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

    public BudgetDto createBudget(CreateBudgetDto dto, String userId) {
        Budget budget = new Budget(
            BudgetId.generate(),
            BudgetCategory.fromString(dto.category),
            dto.monthlyLimit,
            dto.month,
            dto.year,
            userId
        );
        budgetRepository.save(budget);
        log.info("Budget created: category={}, limit={}, period={}/{}, userId={}", dto.category, dto.monthlyLimit, dto.month, dto.year, userId);
        return budgetAssembler.toDto(budget);
    }

    public BudgetDto getBudget(String budgetId, String userId) {
        Budget budget = budgetRepository.findById(new BudgetId(budgetId))
            .filter(b -> userId.equals(b.getUserId()))
            .orElseThrow(() -> new BudgetNotFoundException(budgetId));
        return budgetAssembler.toDto(budget);
    }

    public List<BudgetDto> getAllBudgets(String userId) {
        return budgetAssembler.toDtoList(budgetRepository.findAllByUserId(userId));
    }

    public List<BudgetDto> getBudgetsByMonth(int month, int year, String userId) {
        return budgetAssembler.toDtoList(budgetRepository.findByMonthAndYearAndUserId(month, year, userId));
    }

    public void deleteBudget(String budgetId, String userId) {
        Budget budget = budgetRepository.findById(new BudgetId(budgetId))
            .filter(b -> userId.equals(b.getUserId()))
            .orElseThrow(() -> new BudgetNotFoundException(budgetId));
        budgetRepository.delete(budget.getBudgetId());
        log.info("Budget deleted: id={}, userId={}", budgetId, userId);
    }
}
