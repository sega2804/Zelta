package com.crypticsamsara.zelta.domain.usecase

import com.crypticsamsara.zelta.domain.model.Budget
import com.crypticsamsara.zelta.domain.model.BudgetState
import com.crypticsamsara.zelta.domain.model.Expense
import com.crypticsamsara.zelta.domain.model.Goal
import com.crypticsamsara.zelta.domain.repository.ExpenseRepository
import com.crypticsamsara.zelta.domain.repository.GoalRepository
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

data class FinanceScore(
    val score: Int,
    val grade: ScoreGrade,
    val tip: String
)

enum class ScoreGrade(val label: String) {
    EXCELLENT("Excellent 🌟"),
    GOOD("Good 👍"),
    FAIR("Fair 📈"),
    POOR("Needs Work 💪")
}

class CalculateFinanceScoreUseCase @Inject constructor() {

    operator fun invoke(
        expenses: List<Expense>,
        budgets: List<Budget>,
        goals: List<Goal>
    ): FinanceScore {
        var score = 0

        // budget adherence (40 points)
        val budgetScore = calculateBudgetScore(budgets)
        score += budgetScore

        // Goal contributions (30 points)
        val goalScore = calculateGoalScore(goals)
        score += goalScore

        // logging consistency adherence (20 points)
        val consistencyScore = calculateConsistencyScore(expenses)
        score += consistencyScore

        // budget adherence (10 points)
        val savingsScore = calculateSavingsScore(goals)
        score += savingsScore

        val clampedScore = score.coerceIn(0, 100)

        return FinanceScore(
            score = clampedScore,
            grade = when {
                clampedScore >= 85 -> ScoreGrade.EXCELLENT
                clampedScore >= 65 -> ScoreGrade.GOOD
                clampedScore >= 45 -> ScoreGrade.FAIR
                else -> ScoreGrade.POOR
            },
            tip = generateTip(budgets, goals, expenses)
        )
    }

    private fun calculateBudgetScore(budgets: List<Budget>): Int {
        if (budgets.isEmpty()) return 20 // neutral if no budgets set
        val healthyCount = budgets.count { it.budgetState == BudgetState.HEALTHY }
        return ((healthyCount.toFloat() / budgets.size) * 40).toInt()
    }

    private fun calculateGoalScore(goals: List<Goal>): Int {
        if (goals.isEmpty()) return 15 // neutral if no goals set
        val activeGoals = goals.filter { !it.isCompleted }
        if (activeGoals.isEmpty()) return 30
        val withProgress = activeGoals.count { it.currentAmount > 0 }
        return ((withProgress.toFloat() / activeGoals.size) * 30).toInt()
    }

    private fun calculateConsistencyScore(expenses: List<Expense>): Int {
        val currentMonth = YearMonth.now()
        val daysInMonth = currentMonth.lengthOfMonth()
        val today = LocalDate.now().dayOfMonth

        // How many of the past days have at least one expense logged
        val loggedDays = expenses
            .filter { YearMonth.from(it.date) == currentMonth }
            .map { it.date.dayOfMonth }
            .toSet()
            .size

        val expectedDays = today.coerceAtMost(daysInMonth)
        if (expectedDays == 0) return 20

        return ((loggedDays.toFloat() / expectedDays) * 20)
            .toInt()
            .coerceIn(0, 20)
    }

    private fun calculateSavingsScore(goals: List<Goal>): Int {
        val completedGoals = goals.count { it.isCompleted }
        return when {
            completedGoals >= 3 -> 10
            completedGoals >= 1 -> 5
            else                -> 0
        }
    }

    private fun generateTip(
        budgets: List<Budget>,
        goals: List<Goal>,
        expenses: List<Expense>
    ): String {
        // Find the most overspent category
        val exceededBudget = budgets
            .filter { it.budgetState == BudgetState.EXCEEDED }
            .maxByOrNull { it.usagePercent }

        if (exceededBudget != null) {
            return "You've exceeded your budget on one category. Try reducing spending there next month."
        }

        // Check if any goals have no contributions
        val staleGoal = goals
            .filter { !it.isCompleted && it.currentAmount == 0.0 }
            .firstOrNull()

        if (staleGoal != null) {
            return "Start contributing to \"${staleGoal.name}\" — even small amounts add up fast!"
        }

        // Check logging consistency
        val thisMonthExpenses = expenses.filter {
            YearMonth.from(it.date) == YearMonth.now()
        }
        if (thisMonthExpenses.size < 5) {
            return "Log your expenses daily to get a more accurate finance score."
        }

        return "Great work! Keep tracking and stay consistent to hit 90+."
    }
}
