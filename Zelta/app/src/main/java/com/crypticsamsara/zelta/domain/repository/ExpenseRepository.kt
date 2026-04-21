package com.crypticsamsara.zelta.domain.repository

import com.crypticsamsara.zelta.data.local.dao.CategoryTotal
import com.crypticsamsara.zelta.domain.model.Expense
import com.crypticsamsara.zelta.domain.model.ZeltaResult
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {

    // writes
    suspend fun addExpense(expense: Expense): ZeltaResult<Unit>
    suspend fun updateExpense(expense: Expense): ZeltaResult<Unit>
    suspend fun deleteExpense(id: String): ZeltaResult<Unit>

    // Read
    fun getAllExpenses(): Flow<List<Expense>>
    fun getExpensesByMonth(monthYear: String): Flow<List<Expense>>
    fun getExpensesByCategory(categoryId: String): Flow<List<Expense>>
    fun getExpensesBetweenDates(startDate: String, endDate: String): Flow<List<Expense>>

    // Analytics
    fun getTotalSpentByMonth(monthYear: String): Flow<Double>
    fun getTotalSpentByCategoryAndMonth(
        categoryId: String, monthYear: String
    ): Flow<Double>
    fun getCategoryTotalsForMonth(
        monthYear: String
    ): Flow<List<CategoryTotal>>

    // Sync
    suspend fun syncPendingExpenses(): ZeltaResult<Unit>
}