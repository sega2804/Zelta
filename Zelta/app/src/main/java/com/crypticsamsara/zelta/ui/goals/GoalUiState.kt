package com.crypticsamsara.zelta.ui.goals

import com.crypticsamsara.zelta.domain.model.Goal

data class GoalUiState(
    val isLoading: Boolean = true,
    val activeGoals: List<Goal> = emptyList(),
    val completedGoals: List<Goal> = emptyList(),
    val selectedTab: GoalTab = GoalTab.ACTIVE,
    val errorMessage: String? = null,

    // Sheet visibility
    val isAddGoalSheetVisible: Boolean = false,
    val isContributeSheetVisible: Boolean = false,
    val selectedGoalId: String? = null,
    val isSubmitting: Boolean = false,

    // Celebration
    val justCompletedGoal: Goal? = null
) {
    val selectedGoal: Goal?
        get() = activeGoals.find { it.id == selectedGoalId }
            ?: completedGoals.find { it.id == selectedGoalId }

    val totalSaved: Double
        get() = activeGoals.sumOf { it.currentAmount } +
                completedGoals.sumOf { it.currentAmount }

    val totalTarget: Double
        get() = activeGoals.sumOf { it.targetAmount } +
                completedGoals.sumOf { it.targetAmount }
}

enum class GoalTab(val label: String) {
    ACTIVE("Active"),
    COMPLETED("Completed")
}