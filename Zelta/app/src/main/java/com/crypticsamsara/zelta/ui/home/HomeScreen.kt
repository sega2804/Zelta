package com.crypticsamsara.zelta.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crypticsamsara.zelta.domain.model.Expense
import com.crypticsamsara.zelta.domain.model.Goal
import com.crypticsamsara.zelta.domain.model.SyncState
import com.crypticsamsara.zelta.ui.component.HomeShimmer
import com.crypticsamsara.zelta.ui.component.ZeltaCard
import com.crypticsamsara.zelta.ui.component.ZeltaElevatedCard
import com.crypticsamsara.zelta.ui.theme.ZeltaBgBase
import com.crypticsamsara.zelta.ui.theme.ZeltaBgCard
import com.crypticsamsara.zelta.ui.theme.ZeltaDanger
import com.crypticsamsara.zelta.ui.theme.ZeltaExpense
import com.crypticsamsara.zelta.ui.theme.ZeltaIndigo
import com.crypticsamsara.zelta.ui.theme.ZeltaIndigoLight
import com.crypticsamsara.zelta.ui.theme.ZeltaMint
import com.crypticsamsara.zelta.ui.theme.ZeltaTextDim
import com.crypticsamsara.zelta.ui.theme.ZeltaTextPrimary
import com.crypticsamsara.zelta.ui.theme.ZeltaTextSecondary
import com.crypticsamsara.zelta.ui.theme.ZeltaTypography
import com.crypticsamsara.zelta.ui.theme.ZeltaWarning
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    onAddExpense: () -> Unit,
    onSeeAllExpenses: () -> Unit,
    onGoalClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ZeltaBgBase)
    ) {
        if (uiState.isLoading) {
            HomeShimmer()
        } else {
            HomeContent(
                uiState = uiState,
                onAddExpense = onAddExpense,
                onSeeAllExpenses = onSeeAllExpenses,
                onGoalClick = onGoalClick
            )
        }

        // FAB
        FloatingActionButton(
            onClick = onAddExpense,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp),
            containerColor = ZeltaIndigo,
            contentColor = ZeltaTextPrimary
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Add Expense"
            )
        }
    }
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onAddExpense: () -> Unit,
    onSeeAllExpenses: () -> Unit,
    onGoalClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 24.dp,
            bottom = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        item {
            HomeHeader(
                greeting = uiState.greeting,
                userName = uiState.userName.ifBlank { "there" },
                month = uiState.currentMonth.format(
                    DateTimeFormatter.ofPattern("MMMM yyyy")
                ),
                syncState = uiState.syncState
            )
        }

        // Balance Card
        item {
            BalanceCard(
                totalSpent = uiState.totalSpentThisMonth,
                budgetLimit = uiState.totalBudgetLimit,
                usagePercent = uiState.overallBudgetUsage,
                financeScore = uiState.financeScore?.score
                )
        }

        // Quick stats row
        item {
            QuickStatsRow(
                activeGoals = uiState.activeGoals.size,
                budgetCount = uiState.budgets.size,
                score = uiState.financeScore?.score ?: 0
            )
        }

        // Active goals
        if (uiState.activeGoals.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "ACTIVE GOALS"
                )
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(uiState.activeGoals) { goal ->
                        GoalProgressCard(
                            goal = goal,
                            onClick = { onGoalClick(goal.id) }
                        )
                    }
                }
            }
        }


        // Recent Expenses
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(title = "RECENT")
                Text(
                    text = "See all",
                    style = ZeltaTypography.labelLarge,
                    color = ZeltaIndigoLight,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .padding(4.dp)
                )
            }
        }

        if (uiState.recentExpenses.isNotEmpty()) {
            item { EmptyExpensesHint(onAddExpense) }
        } else {
            items(uiState.recentExpenses) { expense ->
                ExpenseItem(
                    expense = expense,
                )
            }
        }

        // Finance score tip
        uiState.financeScore?.let { score ->
            item {
                ScoreTipCard(tip = score.tip)
            }
        }
    }
}

// Header
@Composable
private fun HomeHeader(
    greeting: String,
    userName: String,
    month: String,
    syncState: SyncState
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column {
            Text(
                text = "$greeting, $userName",
                style = ZeltaTypography.headlineMedium,
                color = ZeltaTextPrimary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = month,
                style = ZeltaTypography.bodyMedium,
                color = ZeltaTextSecondary
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Rounded.Notifications,
                contentDescription = "Notifications",
                tint = ZeltaTextSecondary
            )
        }
        Box(
            modifier = Modifier.size(8.dp)
                .clip(CircleShape)
                .background(
                    when (syncState) {
                        SyncState.SYNCED -> ZeltaMint
                        SyncState.PENDING -> ZeltaWarning
                        SyncState.FAILED -> ZeltaDanger
                        else -> Color.Transparent
                    }
                )
        )
    }
}

@Composable
private fun BalanceCard(
    totalSpent: Double?,
    budgetLimit: Double,
    usagePercent: Float,
    financeScore: Int?
) {
    // Animate progress bar for first load
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) usagePercent else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "budget_progress"
    )
    LaunchedEffect(Unit) { animationPlayed = true }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF6C63FF),
                        Color(0xFF4A3FCC)
                    )
                )
            )
            .padding(22.dp)
    ) {
        // Background decoration circle
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
                .align(Alignment.TopEnd)
        )

        Column {
            Text(
                text = "SPENT THIS MONTH",
                style = ZeltaTypography.labelSmall,
                color = Color.White.copy(alpha = 0.7f)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "%.2f".format(totalSpent),
                style = ZeltaTypography.displayMedium,
                color = Color.White
            )
            Spacer(Modifier.height(16.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(100.dp)),
                color = ZeltaMint,
                trackColor = Color.White.copy(alpha = 0.2f),
                strokeCap = StrokeCap.Round
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                if (budgetLimit > 0) {
                    Text(
                        text = "Budget: $${"%.0f".format(budgetLimit)}",
                        style = ZeltaTypography.labelLarge,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "${"%.0f".format(usagePercent * 100)}% used",
                        style = ZeltaTypography.labelLarge,
                        color = ZeltaMint
                    )
                } else {
                    Text(
                        text = "No budget set",
                        style = ZeltaTypography.labelLarge,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickStatsRow(
    activeGoals: Int,
    budgetCount: Int,
    score: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickStatCard(
            modifier = Modifier.weight(1f),
            label    = "GOALS",
            value    = "$activeGoals Active",
            color    = ZeltaIndigo
        )
        QuickStatCard(
            modifier = Modifier.weight(1f),
            label    = "BUDGETS",
            value    = "$budgetCount Set",
            color    = ZeltaMint
        )
        QuickStatCard(
            modifier = Modifier.weight(1f),
            label    = "SCORE",
            value    = score.toString(),
            color    = when {
                score >= 85 -> ZeltaMint
                score >= 65 -> ZeltaIndigoLight
                score >= 45 -> ZeltaWarning
                else        -> ZeltaDanger
            }
        )
    }
}

@Composable
fun QuickStatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    color: Color
) {
    ZeltaElevatedCard(modifier = modifier, cornerRadius = 16.dp ) {
        Column {
            Text(
                text  = label,
                style = ZeltaTypography.labelSmall,
                color = ZeltaTextDim
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = value,
                style = ZeltaTypography.headlineSmall,
                color = color
            )
        }
    }
}

// Section Header
@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = ZeltaTypography.labelSmall,
        color = ZeltaTextDim
    )
}

// Goal Progress Card
@Composable
private fun GoalProgressCard(
    goal: Goal,
    onClick: () -> Unit
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) goal.progressPercent else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "goal_progress_${goal.id}"
    )
    LaunchedEffect(Unit) { animationPlayed = true }

    ZeltaElevatedCard(
        modifier = Modifier.width(160.dp),
        cornerRadius = 20.dp,
        onClick = onClick
    ) {
        Column {
            Text(
                text = goal.icon,
                style = ZeltaTypography.titleMedium,
                color = ZeltaTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "$${"%.0f".format(goal.currentAmount)} / ${"%.0f".format(goal.targetAmount)}",
                style = ZeltaTypography.bodySmall,
                color = ZeltaTextSecondary
            )
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(100.dp)),
                color = ZeltaIndigo,
                trackColor = ZeltaTextDim.copy(alpha = 0.3f),
                strokeCap = StrokeCap.Round
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${"%.0f".format(goal.progressPercent * 100)}%",
                style = ZeltaTypography.labelSmall,
                color = ZeltaIndigoLight
            )
        }
    }
}

// Expense Item
@Composable
private fun ExpenseItem(expense: Expense) {
    ZeltaCard(cornerRadius = 16.dp) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Category icon placeholder
            Box(
                modifier = Modifier.size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ZeltaIndigo.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "💸",
                    style = ZeltaTypography.titleLarge
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text     = expense.note.ifBlank { "Expense" },
                    style    = ZeltaTypography.titleMedium,
                    color    = ZeltaTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text  = expense.date.format(
                        DateTimeFormatter.ofPattern("MMM d")
                    ),
                    style = ZeltaTypography.bodySmall,
                    color = ZeltaTextSecondary
                )
            }

            Text(
                text  = "-$${"%.2f".format(expense.amount)}",
                style = ZeltaTypography.headlineSmall,
                color = ZeltaExpense
            )
        }
    }
}

// Empty state
@Composable
private fun EmptyExpensesHint(onAddExpense: () -> Unit) {
    ZeltaElevatedCard(cornerRadius = 20.dp) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text  = "💸",
                style = ZeltaTypography.displayMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text  = "No expenses yet",
                style = ZeltaTypography.headlineSmall,
                color = ZeltaTextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = "Tap + to log your first expense",
                style = ZeltaTypography.bodyMedium,
                color = ZeltaTextSecondary
            )
        }
    }
}

// Score Tip card
@Composable
private fun ScoreTipCard(tip: String) {
    ZeltaCard(
        cornerRadius = 20.dp,
        backgroundColor = ZeltaBgCard
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(text  = "💡",
                style = ZeltaTypography.titleLarge
            )
            Column {
                Text(
                    text  = "Zelta Tip",
                    style = ZeltaTypography.titleMedium,
                    color = ZeltaIndigoLight
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = tip,
                    style = ZeltaTypography.bodyMedium,
                    color = ZeltaTextSecondary
                )
            }
        }
    }
}