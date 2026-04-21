package com.crypticsamsara.zelta.domain.repository

import com.crypticsamsara.zelta.data.local.dao.BudgetDao
import com.crypticsamsara.zelta.data.local.entity.toDomain
import com.crypticsamsara.zelta.data.local.entity.toEntity
import com.crypticsamsara.zelta.domain.model.Budget
import com.crypticsamsara.zelta.domain.model.ZeltaResult
import com.crypticsamsara.zelta.domain.model.safeCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao
) : BudgetRepository {

    override suspend fun setBudget(
        budget: Budget
    ): ZeltaResult<Unit> = safeCall {
        budgetDao.insertBudget(budget.toEntity())
    }

    override suspend fun updateBudget(
        budget: Budget
    ): ZeltaResult<Unit> = safeCall {
        budgetDao.updateBudget(budget.toEntity())
    }

    override suspend fun deleteBudget(
        id: String
    ): ZeltaResult<Unit> = safeCall {
        budgetDao.deleteBudget(id)
    }

    override fun getBudgetsByMonth(
        monthYear: String
    ): Flow<List<Budget>> =
        budgetDao.getBudgetsByMonth(monthYear).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getBudgetForCategory(
        categoryId: String,
        monthYear: String
    ): Flow<Budget?> =
        budgetDao.getBudgetForCategory(categoryId, monthYear).map { entity ->
            entity?.toDomain()
        }

    override suspend fun incrementSpending(
        categoryId: String,
        monthYear: String,
        amount: Double
    ): ZeltaResult<Unit> = safeCall {
        budgetDao.incrementSpending(categoryId, monthYear, amount)
    }

    override suspend fun decrementSpending(
        categoryId: String,
        monthYear: String,
        amount: Double
    ): ZeltaResult<Unit> = safeCall {
        budgetDao.decrementSpending(categoryId, monthYear, amount)
    }
}