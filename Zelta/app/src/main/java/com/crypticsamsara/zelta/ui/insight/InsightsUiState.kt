package com.crypticsamsara.zelta.ui.insight

import com.crypticsamsara.zelta.data.local.dao.CategoryTotal
import com.crypticsamsara.zelta.domain.model.Budget
import com.crypticsamsara.zelta.domain.model.Category
import com.crypticsamsara.zelta.domain.model.Expense
import com.crypticsamsara.zelta.domain.usecase.FinanceScore
import java.time.YearMonth

data class InsightsUiState(
    val isLoading: Boolean = true,
    val currentMonth: YearMonth = YearMonth.now(),
    val expenses: List<Expense> = emptyList(),
    val categoryTotals: List<CategoryTotal> = emptyList(),
    val categories: List<Category> = emptyList(),
    val budgets: List<Budget> = emptyList(),
    val totalSpent: Double = 0.0,
    val lastMonthTotal: Double = 0.0,
    val financeScore: FinanceScore? = null,
    val weeklySpend: Map<String, Double> = emptyMap(),
    val errorMessage: String? = null
) {
    // Month-over-month change
    val monthlyDelta: Double
        get() = totalSpent - lastMonthTotal

    val monthlyDeltaPercent: Float
        get() = if (lastMonthTotal == 0.0)  0f
                else ((totalSpent - lastMonthTotal) / lastMonthTotal * 100).toFloat()

    val isSpendingUp: Boolean
        get() = monthlyDelta > 0

    // top 3 categories by spend
    val topCategories: List<CategoryTotal>
        get() = categoryTotals.take(3)

    // Category with most spend
    val biggestCategory: CategoryTotal?
        get() = categoryTotals.firstOrNull()

}
