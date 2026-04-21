package com.crypticsamsara.zelta.domain.repository

import com.crypticsamsara.zelta.data.local.dao.GoalDao
import com.crypticsamsara.zelta.data.local.entity.toDomain
import com.crypticsamsara.zelta.data.local.entity.toEntity
import com.crypticsamsara.zelta.domain.model.Goal
import com.crypticsamsara.zelta.domain.model.ZeltaResult
import com.crypticsamsara.zelta.domain.model.safeCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao
) : GoalRepository {

    override suspend fun addGoal(
        goal: Goal
    ): ZeltaResult<Unit> = safeCall {
        goalDao.insertGoal(goal.toEntity())
    }

    override suspend fun updateGoal(
        goal: Goal
    ): ZeltaResult<Unit> = safeCall {
        goalDao.updateGoal(goal.toEntity())
    }

    override suspend fun deleteGoal(
        id: String
    ): ZeltaResult<Unit> = safeCall {
        goalDao.deleteGoalById(id)
    }

    override suspend fun contributeToGoal(
        id: String,
        amount: Double
    ): ZeltaResult<Unit> = safeCall {
        goalDao.contributeToGoal(id, amount)
        // Check if goal is now complete
        goalDao.getGoalById(id)?.let { entity ->
            if (entity.currentAmount >= entity.targetAmount) {
                goalDao.markGoalCompleted(id)
            }
        }
    }

    override fun getAllGoals(): Flow<List<Goal>> =
        goalDao.getAllGoals().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getActiveGoals(): Flow<List<Goal>> =
        goalDao.getActiveGoals().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getCompletedGoals(): Flow<List<Goal>> =
        goalDao.getCompletedGoals().map { entities ->
            entities.map { it.toDomain() }
        }
}