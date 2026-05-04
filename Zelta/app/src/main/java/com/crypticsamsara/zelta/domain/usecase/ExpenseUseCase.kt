package com.crypticsamsara.zelta.domain.usecase

import com.crypticsamsara.zelta.data.local.dao.CategoryTotal
import com.crypticsamsara.zelta.domain.model.Expense
import com.crypticsamsara.zelta.domain.model.SyncState
import com.crypticsamsara.zelta.domain.model.ZeltaResult
import com.crypticsamsara.zelta.domain.repository.BudgetRepository
import com.crypticsamsara.zelta.domain.repository.CategoryRepository
import com.crypticsamsara.zelta.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID
import javax.inject.Inject

// Add expense
class AddExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: BudgetRepository
){
    suspend operator fun invoke(
        amount: Double,
        categoryId: String,
        note: String,
        date: LocalDate = LocalDate.now()
    ): ZeltaResult<Unit> {
        // Validate input
        if (amount <= 0)return ZeltaResult.Error("Amount must be greater than zero")
        if (categoryId.isBlank()) return ZeltaResult.Error("Please select a category")

        val expense = Expense(
            id = UUID.randomUUID().toString(),
            amount = amount,
            categoryId = categoryId,
            note = note.trim(),
            date = date,
            syncState = SyncState.PENDING,
            localModified = Instant.now()
        )

        // save expense
        val result = expenseRepository.addExpense(expense)

        // update budget spending if save succeeded
        if (result is ZeltaResult.Success) {
            val monthYear = YearMonth.from(date).toString()
            budgetRepository.incrementSpending(categoryId, monthYear, amount)
        }

        return result
    }
}

// Delete expense
class DeleteExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: BudgetRepository
) {
    suspend operator fun invoke(expense: Expense): ZeltaResult<Unit> {
        val result = expenseRepository.deleteExpense(expense.id)

        // roll back budget spending
        if (result is ZeltaResult.Success) {
            val monthYear = YearMonth.from(expense.date).toString()
            budgetRepository.decrementSpending(
                expense.categoryId, monthYear, expense.amount)
        }

        return result
    }
}

// Get expenses by month
class GetExpensesByMonthUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    operator fun invoke(
        monthYear: String = YearMonth.now().toString()): Flow<List<Expense>> =
        expenseRepository.getExpensesByMonth(monthYear)
}

// Get all expenses
class GetAllExpensesUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    operator fun invoke(): Flow<List<Expense>> =
        expenseRepository.getAllExpenses()
}

// Get category totals
class GetCategoryTotalsUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
){
    operator fun invoke(
        monthYear: String = YearMonth.now().toString()
    ): Flow<List<CategoryTotal>> =
        expenseRepository.getCategoryTotalsForMonth(monthYear)
}

// Get total spent
class GetTotalSpentByMonthUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
){
    operator fun invoke(
        monthYear: String = YearMonth.now().toString()
    ): Flow<Double> =
        expenseRepository.getTotalSpentByMonth(monthYear)
}



