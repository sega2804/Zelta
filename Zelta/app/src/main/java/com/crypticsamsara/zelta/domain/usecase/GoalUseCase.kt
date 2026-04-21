package com.crypticsamsara.zelta.domain.usecase

import com.crypticsamsara.zelta.domain.model.Goal
import com.crypticsamsara.zelta.domain.model.ZeltaResult
import com.crypticsamsara.zelta.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

// add goal
class AddGoalUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    suspend operator fun invoke(
        name: String,
        icon: String,
        targetAmount: Double,
        deadline: LocalDate?
    ): ZeltaResult<Unit> {
        if (name.isBlank()) return ZeltaResult.Error("Goal name cannot be empty")
        if (targetAmount <= 0) return ZeltaResult.Error("Target amount must be greater than zero")
        if (deadline != null && deadline.isBefore(LocalDate.now())) {
            return ZeltaResult.Error("Deadline cannot be in the past")
        }

        val goal = Goal(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            icon = icon,
            targetAmount = targetAmount,
            deadline = deadline,
            currentAmount = 0.0,
            createdAt = LocalDate.now(),
        )

        return goalRepository.addGoal(goal)
    }
}

// contribute to goal
class ContributeToGoalUseCase @Inject constructor(
    private val goalRepository: GoalRepository
){
    suspend operator fun invoke(
        goalId: String,
        amount: Double
    ): ZeltaResult<Unit> {
        if (amount <= 0) return ZeltaResult.Error("Contribution must be greater than zero")
        return goalRepository.contributeToGoal(goalId, amount)
    }
}

// Delete goal
class DeleteGoalUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    suspend operator fun invoke(id: String): ZeltaResult<Unit> =
        goalRepository.deleteGoal(id)
}

// Get active goals
class GetActiveGoalsUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    operator fun invoke(): Flow<List<Goal>> =
        goalRepository.getActiveGoals()
}

// Get all goals
class GetAllGoalsUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    operator fun invoke(): Flow<List<Goal>> =
        goalRepository.getAllGoals()
}