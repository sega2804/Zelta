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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crypticsamsara.zelta.domain.model.Budget
import com.crypticsamsara.zelta.domain.model.BudgetState
import com.crypticsamsara.zelta.domain.model.Category
import com.crypticsamsara.zelta.ui.component.ZeltaHeroCard
import com.crypticsamsara.zelta.ui.component.ZeltaPrimaryButton
import com.crypticsamsara.zelta.ui.theme.ZeltaBgBase
import com.crypticsamsara.zelta.ui.theme.ZeltaBgCard
import com.crypticsamsara.zelta.ui.theme.ZeltaBgElevated
import com.crypticsamsara.zelta.ui.theme.ZeltaBorder
import com.crypticsamsara.zelta.ui.theme.ZeltaDanger
import com.crypticsamsara.zelta.ui.theme.ZeltaDangerGlow
import com.crypticsamsara.zelta.ui.theme.ZeltaTeal
import com.crypticsamsara.zelta.ui.theme.ZeltaTealDark
import com.crypticsamsara.zelta.ui.theme.ZeltaTealLight
import com.crypticsamsara.zelta.ui.theme.ZeltaTextDim
import com.crypticsamsara.zelta.ui.theme.ZeltaTextPrimary
import com.crypticsamsara.zelta.ui.theme.ZeltaTextSecondary
import com.crypticsamsara.zelta.ui.theme.ZeltaTypography
import com.crypticsamsara.zelta.ui.theme.ZeltaWarning
import com.crypticsamsara.zelta.ui.theme.ZeltaWarningGlow

@Composable
fun BudgetScreen(
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val uiState       by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarState  = remember { SnackbarHostState() }

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
            contentPadding      = PaddingValues(
                start  = 20.dp,
                end    = 20.dp,
                top    = 20.dp,
                bottom = 110.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // Title
            item {
                Text(
                    text  = "Budgets",
                    style = ZeltaTypography.headlineLarge,
                    color = ZeltaTextPrimary
                )
                Text(
                    text  = "${uiState.currentMonth} · ${uiState.budgets.size} set",
                    style = ZeltaTypography.bodyMedium,
                    color = ZeltaTextSecondary
                )
            }

            // Overall hero
            if (uiState.totalLimit > 0) {
                item {
                    var played by remember { mutableStateOf(false) }
                    val prog   by animateFloatAsState(
                        targetValue   = if (played) uiState.overallUsage else 0f,
                        animationSpec = tween(1000),
                        label         = "overall"
                    )
                    LaunchedEffect(Unit) { played = true }

                    ZeltaHeroCard {
                        Column {
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text  = "TOTAL SPENT",
                                        style = ZeltaTypography.labelSmall,
                                        color = Color.White.copy(alpha = 0.45f)
                                    )
                                    Text(
                                        text  = "$${"%.2f".format(uiState.totalSpent)}",
                                        style = ZeltaTypography.displaySmall,
                                        color = Color.White
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text  = "TOTAL LIMIT",
                                        style = ZeltaTypography.labelSmall,
                                        color = Color.White.copy(alpha = 0.45f)
                                    )
                                    Text(
                                        text  = "$${"%.2f".format(uiState.totalLimit)}",
                                        style = ZeltaTypography.displaySmall,
                                        color = Color.White.copy(alpha = 0.5f)
                                    )
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            LinearProgressIndicator(
                                progress   = { prog },
                                modifier   = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(100.dp)),
                                color      = when {
                                    uiState.overallUsage >= 1f   -> ZeltaDanger
                                    uiState.overallUsage >= 0.8f -> ZeltaWarning
                                    else                         -> ZeltaTeal
                                },
                                trackColor = Color.White.copy(alpha = 0.1f),
                                strokeCap  = StrokeCap.Round
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text  = "${"%.0f".format(uiState.overallUsage * 100)}% of total budget used",
                                style = ZeltaTypography.bodySmall,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            // Health warning
            if (uiState.exceededCount > 0 || uiState.warningCount > 0) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(ZeltaDanger.copy(alpha = 0.07f))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.Warning,
                            contentDescription = null,
                            tint               = ZeltaWarning,
                            modifier           = Modifier.size(18.dp)
                        )
                        Column {
                            if (uiState.exceededCount > 0) {
                                Text(
                                    text  = "${uiState.exceededCount} ${if (uiState.exceededCount == 1) "category" else "categories"} over budget",
                                    style = ZeltaTypography.titleMedium,
                                    color = ZeltaDanger
                                )
                            }
                            if (uiState.warningCount > 0) {
                                Text(
                                    text  = "${uiState.warningCount} approaching limit",
                                    style = ZeltaTypography.bodyMedium,
                                    color = ZeltaWarning
                                )
                            }
                        }
                    }
                }
            }

            // Budget cards
            if (uiState.budgets.isNotEmpty()) {
                item {
                    Text(
                        text  = "YOUR BUDGETS",
                        style = ZeltaTypography.labelSmall,
                        color = ZeltaTextDim
                    )
                }
                items(uiState.budgets, key = { it.id }) { budget ->
                    val category = uiState.categories.find { it.id == budget.categoryId }
                    BudgetEnvelopeCard(
                        budget   = budget,
                        category = category,
                        onEdit   = { viewModel.showSetBudgetSheet(budget.categoryId) },
                        onDelete = { viewModel.onDeleteBudget(budget.id) }
                    )
                }
            }

            // Unbudgeted
            if (uiState.unbudgetedCategories.isNotEmpty()) {
                item {
                    Text(
                        text  = "NO BUDGET SET",
                        style = ZeltaTypography.labelSmall,
                        color = ZeltaTextDim
                    )
                }
                items(uiState.unbudgetedCategories) { category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(ZeltaBgCard)
                            .padding(12.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(category.color.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = category.icon, style = ZeltaTypography.titleMedium)
                        }
                        Text(
                            text     = category.name,
                            style    = ZeltaTypography.titleMedium,
                            color    = ZeltaTextSecondary,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick  = { viewModel.showSetBudgetSheet(category.id) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Add,
                                contentDescription = "Set budget",
                                tint               = ZeltaTeal,
                                modifier           = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Empty
            if (uiState.budgets.isEmpty() && uiState.categories.isEmpty()) {
                item {
                    Box(
                        modifier         = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(22.dp))
                                .background(ZeltaBgCard)
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "🗂", style = ZeltaTypography.displaySmall)
                            Text(
                                text  = "No budgets set",
                                style = ZeltaTypography.headlineSmall,
                                color = ZeltaTextPrimary
                            )
                            Text(
                                text  = "Set monthly limits per category",
                                style = ZeltaTypography.bodyMedium,
                                color = ZeltaTextDim
                            )
                            Spacer(Modifier.height(8.dp))
                            ZeltaPrimaryButton(
                                text    = "Set First Budget",
                                onClick = { viewModel.showSetBudgetSheet() }
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick        = { viewModel.showSetBudgetSheet() },
            modifier       = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            shape          = RoundedCornerShape(100.dp),
            containerColor = ZeltaTeal,
            contentColor   = Color(0xFF07080F)
        ) {
            Icon(Icons.Rounded.Add, null, modifier = Modifier.size(24.dp))
        }

        AnimatedVisibility(
            visible = uiState.isSetBudgetSheetVisible,
            enter   = fadeIn() + slideInVertically { it },
            exit    = fadeOut()
        ) {
            SetBudgetSheet(
                categories            = uiState.unbudgetedCategories
                    .ifEmpty { uiState.categories },
                preselectedCategoryId = uiState.selectedCategoryId,
                isLoading             = uiState.isSubmitting,
                onDismiss             = { viewModel.hideSetBudgetSheet() },
                onConfirm             = { categoryId, limit ->
                    viewModel.onSetBudget(categoryId, limit)
                }
            )
        }

        SnackbarHost(
            hostState = snackbarState,
            modifier  = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun BudgetEnvelopeCard(
    budget: Budget,
    category: Category?,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var played by remember { mutableStateOf(false) }
    val prog   by animateFloatAsState(
        targetValue   = if (played) budget.usagePercent else 0f,
        animationSpec = tween(800),
        label         = "budget_${budget.id}"
    )
    LaunchedEffect(Unit) { played = true }

    val stateColor = when (budget.budgetState) {
        BudgetState.HEALTHY  -> ZeltaTeal
        BudgetState.WARNING  -> ZeltaWarning
        BudgetState.EXCEEDED -> ZeltaDanger
    }

    val stateLabel = when (budget.budgetState) {
        BudgetState.HEALTHY  -> "On track ✓"
        BudgetState.WARNING  -> "Approaching limit"
        BudgetState.EXCEEDED -> "Over budget!"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(ZeltaBgCard)
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background((category?.color ?: ZeltaTeal).copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = category?.icon ?: "📦", style = ZeltaTypography.headlineSmall)
                    }
                    Column {
                        Text(
                            text  = category?.name ?: "Category",
                            style = ZeltaTypography.titleLarge,
                            color = ZeltaTextPrimary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(stateColor.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text  = stateLabel,
                                style = ZeltaTypography.labelSmall,
                                color = stateColor
                            )
                        }
                    }
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Rounded.Edit, null, tint = ZeltaTextDim, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Rounded.Delete, null, tint = ZeltaTextDim, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress   = { prog },
                modifier   = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(100.dp)),
                color      = stateColor,
                trackColor = ZeltaBgElevated,
                strokeCap  = StrokeCap.Round
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "SPENT", style = ZeltaTypography.labelSmall, color = ZeltaTextDim)
                    Text(
                        text  = "$${"%.2f".format(budget.spentAmount)}",
                        style = ZeltaTypography.headlineSmall,
                        color = stateColor
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "USED", style = ZeltaTypography.labelSmall, color = ZeltaTextDim)
                    Text(
                        text  = "${"%.0f".format(budget.usagePercent * 100)}%",
                        style = ZeltaTypography.headlineSmall,
                        color = stateColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "REMAINING", style = ZeltaTypography.labelSmall, color = ZeltaTextDim)
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