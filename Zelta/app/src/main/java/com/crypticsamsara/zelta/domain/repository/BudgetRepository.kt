package com.crypticsamsara.zelta.domain.repository

import com.crypticsamsara.zelta.domain.model.Budget
import com.crypticsamsara.zelta.domain.model.ZeltaResult
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {

    // writes
    suspend fun setBudget(budget: Budget): ZeltaResult<Unit>
    suspend fun updateBudget(budget: Budget): ZeltaResult<Unit>
    suspend fun deleteBudget(id: String): ZeltaResult<Unit>

    // Read
    fun getBudgetsByMonth(monthYear: String): Flow<List<Budget>>
    fun getBudgetForCategory(categoryId: String, monthYear: String): Flow<Budget?>

    // Spending
    suspend fun incrementSpending(
        categoryId: String,
        monthYear: String,
        amount: Double
    ): ZeltaResult<Unit>

    suspend fun decrementSpending(
        categoryId: String,
        monthYear: String,
        amount: Double
    ): ZeltaResult<Unit>
}