package com.crypticsamsara.zelta.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypticsamsara.zelta.domain.model.ZeltaResult
import com.crypticsamsara.zelta.domain.usecase.AddGoalUseCase
import com.crypticsamsara.zelta.domain.usecase.ContributeToGoalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class AddGoalUiState(
    val isLoading       : Boolean = false,
    val isGoalAdded     : Boolean = false,   // dismisses AddGoalSheet
    val isContributed   : Boolean = false,   // dismisses ContributeSheet
    val error           : String? = null
)

@HiltViewModel
class AddGoalViewModel @Inject constructor(
    private val addGoalUseCase       : AddGoalUseCase,
    private val contributeToGoalUseCase: ContributeToGoalUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddGoalUiState())
    val uiState: StateFlow<AddGoalUiState> = _uiState.asStateFlow()

    fun addGoal(
        name        : String,
        icon        : String,
        targetAmount: Double,
        deadline    : LocalDate? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = addGoalUseCase(name, icon, targetAmount, deadline)) {
                is ZeltaResult.Success ->
                    _uiState.update { it.copy(isLoading = false, isGoalAdded = true) }
                is ZeltaResult.Error   ->
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                else -> {}
            }
        }
    }

    fun contribute(goalId: String, amount: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = contributeToGoalUseCase(goalId, amount)) {
                is ZeltaResult.Success ->
                    _uiState.update { it.copy(isLoading = false, isContributed = true) }
                is ZeltaResult.Error   ->
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                else -> {}
            }
        }
    }

    fun resetGoalAdded()    = _uiState.update { it.copy(isGoalAdded   = false) }
    fun resetContributed()  = _uiState.update { it.copy(isContributed = false) }
}