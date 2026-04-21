package com.crypticsamsara.zelta.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllExpenses: GetAllExpensesUseCase,
    private val getTotalSpentByMonth: GetTotalSpentByMonthUseCase,
    private val getCategoryTotals: GetCategoryTotalsUseCase,
    private val getActiveGoals: GetActiveGoalsUseCase,
    private val getBudgetsByMonth: GetBudgetsByMonthUseCase,
    private val calculateFinanceScore: CalculateFinanceScoreUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val currentMonthYear = YearMonth.now().toString()

    init {
        observeHomeData()
    }

    private fun observeHomeData() {
        viewModelScope.launch {

            combine(
                getAllExpenses(),
                getTotalSpentByMonth(currentMonthYear),
                getCategoryTotals(currentMonthYear),
                getActiveGoals(),
                getBudgetsByMonth(currentMonthYear)
            ) { expenses, totalSpent, categoryTotals, goals, budgets ->

                // calculate finance score from combined data
                val score = calculateFinanceScore(
                    expenses = expenses,
                    budgets = budgets,
                    goals = goals
                )

                HomeUiState(
                    isLoading = false,
                    totalSpentThisMonth = totalSpent,
                    recentExpenses = expenses.take(5),
                    categoryTotals = categoryTotals,
                    activeGoals = goals,
                    budgets = budgets,
                    financeScore = score,
                    currentMonth = YearMonth.now()
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun setUserName(name: String) {
        _uiState.update { it.copy(userName = name)}
    }




}