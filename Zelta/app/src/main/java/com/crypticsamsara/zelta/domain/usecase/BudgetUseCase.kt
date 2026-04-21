package com.crypticsamsara.zelta.domain.usecase

import com.crypticsamsara.zelta.domain.model.Budget
import com.crypticsamsara.zelta.domain.model.ZeltaResult
import com.crypticsamsara.zelta.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth
import java.util.UUID
import javax.inject.Inject

// set budgets
class SetBudgetUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    suspend operator fun invoke(
        categoryId: String,
        limitAmount: Double,
        monthYear: String = YearMonth.now().toString()
    ): ZeltaResult<Unit> {
        if (limitAmount <= 0) return ZeltaResult.Error("Budget limit amount must be greater than zero")

        val budget = Budget(
            id = UUID.randomUUID().toString(),
            categoryId = categoryId,
            monthYear = monthYear,
            limitAmount = limitAmount
        )

        return  budgetRepository.setBudget(budget)
    }
}

// Get budget for month
class GetBudgetsByMonthUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    operator fun invoke (
        monthYear: String = YearMonth.now().toString()
    ): Flow<List<Budget>> =
        budgetRepository.getBudgetsByMonth(monthYear)
}

// Delete budget
class DeleteBudgetUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    suspend operator fun invoke(id: String): ZeltaResult<Unit> = budgetRepository.deleteBudget(id)
}