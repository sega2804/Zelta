package com.crypticsamsara.zelta.ui.budget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crypticsamsara.zelta.domain.model.Budget
import com.crypticsamsara.zelta.domain.model.BudgetState
import com.crypticsamsara.zelta.domain.model.Category
import com.crypticsamsara.zelta.ui.component.ZeltaCard
import com.crypticsamsara.zelta.ui.component.ZeltaElevatedCard
import com.crypticsamsara.zelta.ui.component.ZeltaPrimaryButton
import com.crypticsamsara.zelta.ui.theme.ZeltaBgBase
import com.crypticsamsara.zelta.ui.theme.ZeltaBgCard
import com.crypticsamsara.zelta.ui.theme.ZeltaBgElevated
import com.crypticsamsara.zelta.ui.theme.ZeltaDanger
import com.crypticsamsara.zelta.ui.theme.ZeltaIndigo
import com.crypticsamsara.zelta.ui.theme.ZeltaIndigoLight
import com.crypticsamsara.zelta.ui.theme.ZeltaMint
import com.crypticsamsara.zelta.ui.theme.ZeltaTextDim
import com.crypticsamsara.zelta.ui.theme.ZeltaTextPrimary
import com.crypticsamsara.zelta.ui.theme.ZeltaTextSecondary
import com.crypticsamsara.zelta.ui.theme.ZeltaTypography
import com.crypticsamsara.zelta.ui.theme.ZeltaWarning

@Composable
fun BudgetScreen(
    viewModel: BudgetViewModel =  hiltViewModel()
) {
    val uiState  by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarState.showSnackbar(it)
            viewModel.dismissError()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ZeltaBgBase)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 24.dp,
                bottom = 100.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Header
            item {
                BudgetHeader(
                    month = uiState.currentMonth.toString(),
                    totalSpent = uiState.totalSpent,
                    totalLimit = uiState.totalLimit,
                    overallUsage = uiState.overallUsage
                )
            }

            // Health summary
            if (uiState.exceededCount > 0 || uiState.warningCount > 0) {
                item {
                    BudgetHealthCard(
                        exceededCount = uiState.exceededCount,
                        warningCount = uiState.warningCount
                    )
                }
            }

            // Budget card
            if (uiState.budgets.isNotEmpty()) {
                item {
                    Text(
                        text = "YOUR BUDGETS",
                        style = ZeltaTypography.labelSmall,
                        color = ZeltaTextDim
                    )
                }

                items (
                    items = uiState.budgets,
                    key = { it.id }
                ) { budget ->
                    val category = uiState.categories.find {
                        it.id == budget.categoryId
                    }
                    BudgetEnvelopeCard(
                        budget = budget,
                        category = category,
                        onEdit = {
                            viewModel.showSetBudgetSheet(budget.categoryId)
                        },
                        onDelete = { viewModel.onDeleteBudget(budget.id)}
                    )
                }
            }

            // Unbudgeted Categories
            if (uiState.unbudgetedCategories.isNotEmpty()) {
                item {
                    Text(
                        text = "NO BUDGET SET",
                        style = ZeltaTypography.labelSmall,
                        color = ZeltaTextDim,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(uiState.unbudgetedCategories) { category ->
                    UnbudgetedCategoryRow(
                        category = category,
                        onSetBudget = {
                            viewModel.showSetBudgetSheet(category.id)
                        }
                    )
                }
            }

            // Empty States
            if (uiState.budgets.isEmpty() && uiState.categories.isEmpty()) {
                item {
                    EmptyBudgetState(onAdd = { viewModel.showSetBudgetSheet() } )
                }
            }
        }

        // Fab
        FloatingActionButton(
            onClick = { viewModel.showSetBudgetSheet() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp),
            containerColor = ZeltaIndigo,
            contentColor = ZeltaTextPrimary
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Set Budget")
        }

        // Set budget sheet
        AnimatedVisibility(
            visible = uiState.isSetBudgetSheetVisible,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut()
        ) {
            SetBudgetSheet(
                categories = uiState.unbudgetedCategories
                    .ifEmpty { uiState.categories },
                preselectedCategoryId = uiState.selectedCategoryId,
                isLoading = uiState.isSubmitting,
                onDismiss = { viewModel.hideSetBudgetSheet() },
                onConfirm = { categoryId, limit ->
                    viewModel.onSetBudget(categoryId, limit)
                }
            )
        }

        SnackbarHost(
            hostState = snackbarState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// Budget header
@Composable
private fun BudgetHeader(
    month: String,
    totalSpent: Double,
    totalLimit: Double,
    overallUsage: Float
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) overallUsage else 0f,
        animationSpec = tween(1000),
        label = "overall_budget"
    )
    LaunchedEffect(Unit) { animationPlayed = true }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ZeltaBgCard)
            .padding(22.dp)
    ) {

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Budget",
                    style = ZeltaTypography.headlineLarge,
                    color = ZeltaTextPrimary
                )
                Text(
                    text = month,
                    style = ZeltaTypography.headlineLarge,
                    color = ZeltaTextPrimary
                )
            }
            // Usage badge
            if (totalLimit > 0) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when {
                                overallUsage >= 1f   -> ZeltaDanger.copy(alpha = 0.15f)
                                overallUsage >= 0.8f -> ZeltaWarning.copy(alpha = 0.15f)
                                else                 -> ZeltaMint.copy(alpha = 0.12f)
                            }
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text  = "${"%.0f".format(overallUsage * 100)}%",
                        style = ZeltaTypography.headlineSmall,
                        color = when {
                            overallUsage >= 1f   -> ZeltaDanger
                            overallUsage >= 0.8f -> ZeltaWarning
                            else                 -> ZeltaMint
                        }
                    )
                }
            }
        }

        if (totalLimit > 0) {
            Spacer(Modifier.height(20.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text  = "SPENT",
                        style = ZeltaTypography.labelSmall,
                        color = ZeltaTextDim
                    )
                    Text(
                        text  = "$${"%.2f".format(totalSpent)}",
                        style = ZeltaTypography.displaySmall,
                        color = ZeltaTextPrimary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text  = "LIMIT",
                        style = ZeltaTypography.labelSmall,
                        color = ZeltaTextDim
                    )
                    Text(
                        text  = "$${"%.2f".format(totalLimit)}",
                        style = ZeltaTypography.displaySmall,
                        color = ZeltaTextSecondary
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            LinearProgressIndicator(
                progress   = { animatedProgress },
                modifier   = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(100.dp)),
                color      = when {
                    overallUsage >= 1f   -> ZeltaDanger
                    overallUsage >= 0.8f -> ZeltaWarning
                    else                 -> ZeltaMint
                },
                trackColor = ZeltaBgElevated,
                strokeCap  = StrokeCap.Round
            )
        }
    }
    }
}

// Health Card
@Composable
private fun BudgetHealthCard(
    exceededCount: Int,
    warningCount: Int
) {
    ZeltaCard(
        cornerRadius = 16.dp,
        backgroundColor = ZeltaDanger.copy(alpha = 0.08f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = null,
                tint = ZeltaWarning,
                modifier = Modifier.size(22.dp)
            )
            Column {
                if (exceededCount > 0) {
                    Text(
                        text = "$exceededCount ${if (exceededCount == 1) "category" else "categories"} over budget",
                        style = ZeltaTypography.titleMedium,
                        color = ZeltaDanger
                    )
                }
                if (warningCount > 0) {
                    Text(
                        text = "$warningCount approaching limit",
                        style = ZeltaTypography.bodyMedium,
                        color = ZeltaWarning
                    )
                }
            }
        }
    }
}

// Budget Envelope Card
@Composable
private fun BudgetEnvelopeCard(
    budget: Budget,
    category: Category?,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) budget.usagePercent else 0f,
        animationSpec = tween(800),
        label = "budget_${budget.id}"
    )
    LaunchedEffect(Unit) { animationPlayed = true }

    val stateColor = when (budget.budgetState) {
        BudgetState.HEALTHY -> ZeltaMint
        BudgetState.EXCEEDED -> ZeltaDanger
        BudgetState.WARNING -> ZeltaWarning
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(ZeltaBgCard)
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                (category?.color ?: ZeltaIndigo).copy(alpha = 0.12f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = category?.icon ?: "📦",
                            style = ZeltaTypography.headlineSmall
                        )
                    }
                    Column {
                        Text(
                            text  = category?.name ?: "Category",
                            style = ZeltaTypography.titleLarge,
                            color = ZeltaTextPrimary
                        )
                        // State badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(stateColor.copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text  = when (budget.budgetState) {
                                    BudgetState.HEALTHY  -> "On track ✓"
                                    BudgetState.WARNING  -> "Approaching limit"
                                    BudgetState.EXCEEDED -> "Over budget!"
                                },
                                style = ZeltaTypography.labelSmall,
                                color = stateColor
                            )
                        }
                    }
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Rounded.Edit,
                            contentDescription = "Edit",
                            tint               = ZeltaTextDim,
                            modifier           = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Rounded.Delete,
                            contentDescription = "Delete",
                            tint               = ZeltaTextDim,
                            modifier           = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            LinearProgressIndicator(
                progress   = { animatedProgress },
                modifier   = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(100.dp)),
                color      = stateColor,
                trackColor = ZeltaBgElevated,
                strokeCap  = StrokeCap.Round
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text  = "SPENT",
                        style = ZeltaTypography.labelSmall,
                        color = ZeltaTextDim
                    )
                    Text(
                        text  = "$${"%.2f".format(budget.spentAmount)}",
                        style = ZeltaTypography.headlineSmall,
                        color = stateColor
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text  = "USED",
                        style = ZeltaTypography.labelSmall,
                        color = ZeltaTextDim
                    )
                    Text(
                        text  = "${"%.0f".format(budget.usagePercent * 100)}%",
                        style = ZeltaTypography.headlineSmall,
                        color = stateColor
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text  = "REMAINING",
                        style = ZeltaTypography.labelSmall,
                        color = ZeltaTextDim
                    )
                    Text(
                        text  = "$${"%.2f".format(budget.remainingAmount)}",
                        style = ZeltaTypography.headlineSmall,
                        color = ZeltaTextSecondary
                    )
                }
            }
        }
    }
}

// Unbudgeted Category Row
@Composable
private fun UnbudgetedCategoryRow(
    category: Category,
    onSetBudget: () -> Unit
) {
    ZeltaElevatedCard(cornerRadius = 16.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(category.color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.icon,
                        style = ZeltaTypography.titleMedium
                    )
                }
                Text(
                    text = category.name,
                    style = ZeltaTypography.titleMedium,
                    color = ZeltaTextSecondary
                )
            }
            IconButton(onClick = onSetBudget) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Set Budget",
                    tint = ZeltaIndigoLight,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// Empty State
@Composable
private fun EmptyBudgetState(onAdd: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ZeltaElevatedCard(
            modifier = Modifier.padding(40.dp),
            cornerRadius = 24.dp
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "🗂", style = ZeltaTypography.displayMedium)
                Spacer(Modifier.height(12.dp))
                Text(
                    text  = "No budgets set",
                    style = ZeltaTypography.headlineSmall,
                    color = ZeltaTextPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = "Set monthly limits per category\nto stay in control",
                    style = ZeltaTypography.bodyMedium,
                    color = ZeltaTextSecondary
                )
                Spacer(Modifier.height(20.dp))
                ZeltaPrimaryButton(
                    text    = "Set First Budget",
                    onClick = onAdd
                )
            }
        }
    }
}