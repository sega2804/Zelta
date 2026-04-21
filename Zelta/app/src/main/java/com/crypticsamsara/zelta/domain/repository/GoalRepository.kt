package com.crypticsamsara.zelta.domain.repository

import com.crypticsamsara.zelta.domain.model.Goal
import com.crypticsamsara.zelta.domain.model.ZeltaResult
import kotlinx.coroutines.flow.Flow

interface GoalRepository {

    // writes
    suspend fun addGoal(goal: Goal): ZeltaResult<Unit>
    suspend fun updateGoal(goal: Goal): ZeltaResult<Unit>
    suspend fun deleteGoal(id: String): ZeltaResult<Unit>
    suspend fun contributeToGoal(id: String, amount: Double): ZeltaResult<Unit>


    // Read
    fun getAllGoals(): Flow<List<Goal>>
    fun getActiveGoals(): Flow<List<Goal>>
    fun getCompletedGoals(): Flow<List<Goal>>
}

