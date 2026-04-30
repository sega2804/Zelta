package com.crypticsamsara.zelta.ui.track

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crypticsamsara.zelta.domain.model.Category
import com.crypticsamsara.zelta.domain.model.Expense
import com.crypticsamsara.zelta.ui.component.ZeltaCard
import com.crypticsamsara.zelta.ui.component.ZeltaChip
import com.crypticsamsara.zelta.ui.component.ZeltaElevatedCard
import com.crypticsamsara.zelta.ui.theme.ZeltaBgBase
import com.crypticsamsara.zelta.ui.theme.ZeltaBgCard
import com.crypticsamsara.zelta.ui.theme.ZeltaDanger
import com.crypticsamsara.zelta.ui.theme.ZeltaExpense
import com.crypticsamsara.zelta.ui.theme.ZeltaIndigo
import com.crypticsamsara.zelta.ui.theme.ZeltaIndigoGlow
import com.crypticsamsara.zelta.ui.theme.ZeltaTextDim
import com.crypticsamsara.zelta.ui.theme.ZeltaTextPrimary
import com.crypticsamsara.zelta.ui.theme.ZeltaTextSecondary
import com.crypticsamsara.zelta.ui.theme.ZeltaTypography
import java.time.format.DateTimeFormatter
import kotlin.collections.find

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackScreen(
    viewModel: TrackViewModel = hiltViewModel()
) {
    val uiState       by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarState  = remember { SnackbarHostState() }

    // Show error in snackbar
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
        // Top glow
        Box(
            modifier = Modifier
                .size(250.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(ZeltaIndigoGlow, Color.Transparent)
                    ),
                    CircleShape
                )
                .align(Alignment.TopStart)
        )

        Column(modifier = Modifier.fillMaxSize()) {

            // Header
            TrackHeader(totalForPeriod = uiState.totalForPeriod)

            // Filter Chips
            LazyRow(
                contentPadding        = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier              = Modifier.padding(bottom = 10.dp)
            ) {
                items(ExpenseFilter.entries.toTypedArray()) { filter ->
                    ZeltaChip(
                        label    = filter.label,
                        selected = uiState.selectedFilter == filter,
                        onClick  = { viewModel.onFilterSelected(filter) }
                    )
                }
            }

            // Category Filters
            if (uiState.categories.isNotEmpty()) {
                LazyRow(
                    contentPadding        = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier              = Modifier.padding(bottom = 16.dp)
                ) {
                    item {
                        ZeltaChip(
                            label    = "All",
                            selected = uiState.selectedCategoryId == null,
                            onClick  = { viewModel.onCategoryFilterSelected(null) }
                        )
                    }
                    items(uiState.categories) { category ->
                        ZeltaChip(
                            label    = "${category.icon} ${category.name}",
                            selected = uiState.selectedCategoryId == category.id,
                            onClick  = {
                                viewModel.onCategoryFilterSelected(category.id)
                            }
                        )
                    }
                }
            }

            // Expense List
            val filteredExpenses = uiState.expenses.filter { expense ->
                uiState.selectedCategoryId == null ||
                        expense.categoryId == uiState.selectedCategoryId
            }

            val groupedExpenses = viewModel.groupExpensesByDate(filteredExpenses)

            if (filteredExpenses.isEmpty()) {
                EmptyTrackState()
            } else {
                LazyColumn(
                    contentPadding        = PaddingValues(
                        start  = 20.dp,
                        end    = 20.dp,
                        bottom = 110.dp
                    ),
                    verticalArrangement   = Arrangement.spacedBy(6.dp)
                ) {
                    groupedExpenses.forEach { group ->
                        // Date header
                        item {
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                Text(
                                    text  = group.dateLabel,
                                    style = ZeltaTypography.labelSmall,
                                    color = ZeltaTextDim
                                )
                                Text(
                                    text  = "-$${"%.2f".format(group.total)}",
                                    style = ZeltaTypography.labelLarge,
                                    color = ZeltaExpense
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                        }

                        // Expense items
                        items(
                            items = group.expenses,
                            key   = { it.id }
                        ) { expense ->
                            SwipeableExpenseItem(
                                expense    = expense,
                                categories = uiState.categories,
                                onDelete   = { viewModel.onDeleteExpense(expense) }
                            )
                        }
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick        = { viewModel.showAddSheet() },
            modifier       = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            shape          = RoundedCornerShape(20.dp),
            containerColor = ZeltaIndigo,
            contentColor   = ZeltaTextPrimary
        ) {
            Icon(Icons.Rounded.Add,
                contentDescription = "Add Expense",
                modifier = Modifier.size(26.dp))
        }

        // Snackbar
        SnackbarHost(
            hostState = snackbarState,
            modifier  = Modifier.align(Alignment.BottomCenter)
        )

        // Add Expense Sheet
        AnimatedVisibility(
            visible = uiState.isAddSheetVisible,
            enter   = fadeIn() + slideInVertically { it },
            exit    = fadeOut()
        ) {
            AddExpenseSheet(
                categories  = uiState.categories,
                isLoading   = uiState.isAddingExpense,
                onDismiss   = { viewModel.hideAddSheet() },
                onConfirm   = { amount, categoryId, note, date ->
                    viewModel.onAddExpense(amount, categoryId, note, date)
                }
            )
        }
    }
}

// Track Header
@Composable
private fun TrackHeader(totalForPeriod: Double) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Text(
            text = "Expenses",
            style = ZeltaTypography.headlineLarge,
            color = ZeltaTextPrimary
        )
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text  = "-$${"%.2f".format(totalForPeriod)}",
                style = ZeltaTypography.displaySmall,
                color = ZeltaExpense
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text     = "total",
                style    = ZeltaTypography.bodyMedium,
                color    = ZeltaTextDim,
                modifier = Modifier.padding(bottom = 3.dp)
            )
        }
    }
}

// Swipeable Expense Item
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableExpenseItem(
    expense: Expense,
    categories: List<Category>,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        initialValue       = SwipeToDismissBoxValue.Settled,
        confirmValueChange = { dismissValue: SwipeToDismissBoxValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        },
        positionalThreshold = SwipeToDismissBoxDefaults.positionalThreshold
    )
    SwipeToDismissBox(
        state                       = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent           = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(ZeltaDanger.copy(alpha = 0.12f)),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    modifier = Modifier.padding(end = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete",
                        tint = ZeltaDanger,
                        modifier = Modifier.padding(end = 20.dp)
                    )
                    Text(
                        text = "Delete",
                        style = ZeltaTypography.labelLarge,
                        color = ZeltaDanger
                    )
                }
            }
        }
    ) {
        val category = categories.find { it.id == expense.categoryId }
        ExpenseRow(expense = expense, category = category)
    }
}

// Expense Row
@Composable
private fun ExpenseRow(
    expense: Expense,
    category: Category?
) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(ZeltaBgCard)
                .padding(14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Category icon bubble
            Box(
                modifier         = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        (category?.color ?: ZeltaIndigo).copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text  = category?.icon ?: "💸",
                    style = ZeltaTypography.headlineSmall
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text     = expense.note.ifBlank { category?.name ?: "Expense" },
                    style    = ZeltaTypography.titleMedium,
                    color    = ZeltaTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(category?.color ?: ZeltaIndigo)
                    )
                    Text(
                    text  = category?.name ?: "Uncategorized",
                    style = ZeltaTypography.bodySmall,
                    color = ZeltaTextDim
                    )
                    Text(
                        text  = "·",
                        style = ZeltaTypography.bodySmall,
                        color = ZeltaTextDim
                    )
                    Text(
                        text  = expense.date.format(DateTimeFormatter.ofPattern("MMM d")),
                        style = ZeltaTypography.bodySmall,
                        color = ZeltaTextDim
                    )
                }
            }

            Text(
                text  = "-$${"%.2f".format(expense.amount)}",
                style = ZeltaTypography.headlineSmall,
                color = ZeltaExpense
            )
        }
    }

// Empty State
@Composable
private fun EmptyTrackState() {
    Box(
        modifier         = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier            = Modifier
                .padding(40.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(ZeltaBgCard)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "🧾", style = ZeltaTypography.displayMedium)
            Text(
                text  = "No expenses found",
                style = ZeltaTypography.headlineSmall,
                color = ZeltaTextPrimary
            )
            Text(
                text  = "Tap + to log your first one",
                style = ZeltaTypography.bodyMedium,
                color = ZeltaTextDim
            )
        }
    }
}