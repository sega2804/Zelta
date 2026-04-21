package com.crypticsamsara.zelta.domain.repository


import com.crypticsamsara.zelta.data.local.dao.CategoryTotal
import com.crypticsamsara.zelta.data.local.dao.ExpenseDao
import com.crypticsamsara.zelta.data.local.entity.toDomain
import com.crypticsamsara.zelta.data.local.entity.toEntity
import com.crypticsamsara.zelta.domain.model.Expense
import com.crypticsamsara.zelta.domain.model.ZeltaResult
import com.crypticsamsara.zelta.domain.model.safeCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {

    override suspend fun addExpense(
        expense: Expense
    ): ZeltaResult<Unit> = safeCall {
        expenseDao.insertExpense(expense.toEntity())
    }

    override suspend fun updateExpense(
        expense: Expense
    ): ZeltaResult<Unit> = safeCall {
        expenseDao.updateExpense(expense.toEntity())
    }

    override suspend fun deleteExpense(
        id: String
    ): ZeltaResult<Unit> = safeCall {
        expenseDao.deleteExpenseById(id)
    }

    override fun getAllExpenses(): Flow<List<Expense>> =
        expenseDao.getAllExpenses().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getExpensesByMonth(
        monthYear: String
    ): Flow<List<Expense>> =
        expenseDao.getExpensesByMonth(monthYear).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getExpensesByCategory(
        categoryId: String
    ): Flow<List<Expense>> =
        expenseDao.getExpensesByCategory(categoryId).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getExpensesBetweenDates(
        startDate: String,
        endDate: String
    ): Flow<List<Expense>> =
        expenseDao.getExpensesBetweenDates(startDate, endDate).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getTotalSpentByMonth(
        monthYear: String
    ): Flow<Double> =
        expenseDao.getTotalSpentByMonth(monthYear).map { it ?: 0.0 }

    override fun getTotalSpentByCategoryAndMonth(
        categoryId: String,
        monthYear: String
    ): Flow<Double> =
        expenseDao.getTotalSpentByCategoryAndMonth(
            categoryId, monthYear
        ).map { it ?: 0.0 }

    override fun getCategoryTotalsForMonth(
        monthYear: String
    ): Flow<List<CategoryTotal>> =
        expenseDao.getCategoryTotalsForMonth(monthYear)

    override suspend fun syncPendingExpenses(): ZeltaResult<Unit> = safeCall {
        // Firebase sync logic goes here in Week 2
        // For now this is a no-op placeholder
    }
}
