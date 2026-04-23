package com.crypticsamsara.zelta.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypticsamsara.zelta.domain.model.ZeltaResult
import com.crypticsamsara.zelta.domain.usecase.AddGoalUseCase
import com.crypticsamsara.zelta.domain.usecase.ContributeToGoalUseCase
import com.crypticsamsara.zelta.domain.usecase.DeleteGoalUseCase
import com.crypticsamsara.zelta.domain.usecase.GetAllGoalsUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.NonCancellable.isCompleted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class GoalsViewModel @Inject constructor(
    private val getAllGoals: GetAllGoalsUseCase,
    private val addGoal: AddGoalUseCase,
    private val contributeToGoal: ContributeToGoalUseCase,
    private val deleteGoal: DeleteGoalUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalUiState())
    val uiState: StateFlow<GoalUiState> = _uiState.asStateFlow()

    init {
        observeGoals()
    }

    private fun observeGoals() {
        viewModelScope.launch {
            getAllGoals().collect { goals ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        activeGoals = goals.filter { g -> !g.isCompleted },
                        completedGoals = goals.filter { g -> g.isCompleted }
                    )
                }
            }
        }
    }

    fun onTabSelected(tab: GoalTab) {
        _uiState.update {
            it.copy(selectedTab = tab)
        }
    }

    fun showAddGoalSheet() {
        _uiState.update {
            it.copy(isAddGoalSheetVisible = true)
        }
    }

    fun hideAddGoalSheet() {
        _uiState.update {
            it.copy(isAddGoalSheetVisible = false)
        }
    }

    fun onAddGoal(
        name: String,
        icon: String,
        targetAmount: Double,
        deadline: LocalDate?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }

            val result = addGoal(
                name = name,
                icon = icon,
                targetAmount = targetAmount,
                deadline = deadline
            )

            when (result) {
                is ZeltaResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            isAddGoalSheetVisible = false
                        )
                    }
                }

                is ZeltaResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = result.message
                        )
                    }
                }

                else -> Unit
            }
        }
    }

    // contribute
    fun showContributeSheet(goalId: String) {
        _uiState.update {
            it.copy(
                isContributeSheetVisible = true,
                selectedGoalId = goalId
            )
        }
    }

    fun hideContributeSheet() {
        _uiState.update {
            it.copy(
                isContributeSheetVisible = false,
                selectedGoalId = null
            )
        }
    }

    fun onContribute(goalId: String, amount: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }

            val result = contributeToGoal(goalId, amount)

            when (result) {
                is ZeltaResult.Success -> {
                    // to check if goal has been completed
                    val goal = _uiState.value.activeGoals.find { it.id == goalId }
                    val justCompleted = goal?.let {
                        (it.currentAmount + amount) >= it.targetAmount
                    } ?: false

                    _uiState.update { state ->
                        state.copy(
                            isSubmitting = false,
                            isContributeSheetVisible = false,
                            selectedGoalId = null,
                            justCompletedGoal = if (justCompleted) goal else null
                        )
                    }
                }

                is ZeltaResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = result.message
                        )
                    }
                }

                else -> Unit
            }
        }
    }

    // Delete
    fun onDeleteGoal(goalId: String) {
        viewModelScope.launch {
            deleteGoal(goalId)
        }
    }

    fun dismissCelebration() {
        _uiState.update { it.copy(justCompletedGoal = null) }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}