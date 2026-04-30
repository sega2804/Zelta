package com.crypticsamsara.zelta.ui.insight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypticsamsara.zelta.domain.model.Expense
import com.crypticsamsara.zelta.domain.repository.CategoryRepository
import com.crypticsamsara.zelta.domain.usecase.CalculateFinanceScoreUseCase
import com.crypticsamsara.zelta.domain.usecase.GetActiveGoalsUseCase
import com.crypticsamsara.zelta.domain.usecase.GetAllExpensesUseCase
import com.crypticsamsara.zelta.domain.usecase.GetBudgetsByMonthUseCase
import com.crypticsamsara.zelta.domain.usecase.GetCategoryTotalsUseCase
import com.crypticsamsara.zelta.domain.usecase.GetTotalSpentByMonthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject
import kotlin.collections.filter


@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val getAllExpenses: GetAllExpensesUseCase,
    private val getTotalSpentByMonth: GetTotalSpentByMonthUseCase,
    private val getCategoryTotals: GetCategoryTotalsUseCase,
    private val getActiveGoals: GetActiveGoalsUseCase,
    private val getBudgetsByMonth: GetBudgetsByMonthUseCase,
    private val categoryRepository: CategoryRepository,
    private val calculateFinanceScore: CalculateFinanceScoreUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    private val currentMonth = YearMonth.now().toString()
    private val lastMonth = YearMonth.now().minusMonths(1).toString()

    init {
        observeInsightsData()
    }

    private fun observeInsightsData() {
        viewModelScope.launch {
            /*combine(
                getAllExpenses(),
                getTotalSpentByMonth(currentMonth),
                getTotalSpentByMonth(lastMonth),
                getCategoryTotals(currentMonth),
                categoryRepository.getAllCategories(),
                getBudgetsByMonth(currentMonth),
                getActiveGoals()
            ) { results ->
                // Destructure combined results
                @Suppress("UNCHECKED_CAST")
                val expenses       = results[0] as List<Expense>
                val totalSpent     = results[1] as Double
                val lastMonthTotal = results[2] as Double
                val categoryTotals = results[3] as List<CategoryTotal>
                val categories     = results[4] as List<Category>
                val budgets        = results[5] as List<Budget>
                val goals          = results[6] as List<Goal>

                val score        = calculateFinanceScore(expenses, budgets, goals)
                val weeklySpend  = buildWeeklySpend(expenses)

             */ // revert if error persists
            combine(
                getAllExpenses(),
                getTotalSpentByMonth(currentMonth),
                getTotalSpentByMonth(lastMonth),
                getCategoryTotals(currentMonth),
                categoryRepository.getAllCategories()
            ) { expenses, totalSpent, lastMonthTotal, categoryTotals, categories ->
                Quintuple(expenses, totalSpent, lastMonthTotal, categoryTotals, categories)
            }.combine(
                // Second combine — remaining 2 flows
                combine(
                    getBudgetsByMonth(currentMonth),
                    getActiveGoals()
                ) { budgets, goals -> Pair(budgets, goals) }
            ) { quintuple, pair ->
                val (expenses, totalSpent, lastMonthTotal, categoryTotals, categories) = quintuple
                val (budgets, goals) = pair

                val score       = calculateFinanceScore(expenses, budgets, goals)
                val weeklySpend = buildWeeklySpend(expenses)

                InsightsUiState(
                    isLoading = false,
                    expenses = expenses,
                    totalSpent = totalSpent ?: 0.0,
                    lastMonthTotal = lastMonthTotal ?: 0.0,
                    categoryTotals = categoryTotals,
                    categories = categories,
                    budgets = budgets,
                    financeScore = score,
                    weeklySpend = weeklySpend
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    // Build last 7 days spend map
    private fun buildWeeklySpend(
        expenses: List<Expense>
    ): Map<String, Double> {
        val today  = LocalDate.now()
        val result = LinkedHashMap<String, Double>()

        // Initialize all 7 days with 0
        (6 downTo 0).forEach { daysAgo ->
            val date  = today.minusDays(daysAgo.toLong())
            val label = date.dayOfWeek
                .getDisplayName(TextStyle.SHORT, Locale.getDefault())
            result[label] = 0.0
        }

        // Fill in actual spend
        expenses
            .filter { it.date >= today.minusDays(6) }
            .forEach { expense ->
                val label = expense.date.dayOfWeek
                    .getDisplayName(TextStyle.SHORT, Locale.getDefault())
                result[label] = (result[label] ?: 0.0) + expense.amount
            }

        return result
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private data class Quintuple<A, B, C, D, E>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D,
        val fifth: E
    )

    private operator fun <A, B, C, D, E> Quintuple<A, B, C, D, E>.component1() = first
    private operator fun <A, B, C, D, E> Quintuple<A, B, C, D, E>.component2() = second
    private operator fun <A, B, C, D, E> Quintuple<A, B, C, D, E>.component3() = third
    private operator fun <A, B, C, D, E> Quintuple<A, B, C, D, E>.component4() = fourth
    private operator fun <A, B, C, D, E> Quintuple<A, B, C, D, E>.component5() = fifth
}

