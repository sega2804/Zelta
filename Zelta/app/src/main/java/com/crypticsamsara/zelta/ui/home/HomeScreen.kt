package com.crypticsamsara.zelta.ui.home


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.crypticsamsara.zelta.ui.theme.ZeltaBgBase
import com.crypticsamsara.zelta.ui.theme.ZeltaBgCard
import com.crypticsamsara.zelta.ui.theme.ZeltaBgElevated
import com.crypticsamsara.zelta.ui.theme.ZeltaDanger
import com.crypticsamsara.zelta.ui.theme.ZeltaExpense
import com.crypticsamsara.zelta.ui.theme.ZeltaIndigo
import com.crypticsamsara.zelta.ui.theme.ZeltaIndigoGlow
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
        // Background glow effects
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ZeltaIndigoGlow,
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
                .align(Alignment.TopEnd)
        )

        if (uiState.isLoading) {
            HomeShimmer()
        } else {
            HomeContent(
                uiState          = uiState,
                onAddExpense     = onAddExpense,
                onSeeAllExpenses = onSeeAllExpenses,
                onGoalClick      = onGoalClick
            )
        }

        FloatingActionButton(
            onClick        = onAddExpense,
            modifier       = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            shape          = RoundedCornerShape(20.dp),
            containerColor = ZeltaIndigo,
            contentColor   = Color.White
        ) {
            Icon(
                imageVector        = Icons.Rounded.Add,
                contentDescription = "Add Expense",
                modifier           = Modifier.size(26.dp)
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
        modifier       = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start  = 20.dp,
            end    = 20.dp,
            top    = 24.dp,
            bottom = 110.dp
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        item {
            HomeHeader(
                greeting  = uiState.greeting,
                userName  = uiState.userName.ifBlank { "there" },
                month     = uiState.currentMonth
                    .format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                syncState = uiState.syncState
            )
        }

        item { BalanceCard(uiState = uiState) }

        item { QuickStatsRow(uiState = uiState) }

        if (uiState.activeGoals.isNotEmpty()) {
            item {
                SectionHeader(
                    title    = "ACTIVE GOALS",
                    action   = null,
                    onAction = {}
                )
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding        = PaddingValues(horizontal = 2.dp)
                ) {
                    items(uiState.activeGoals) { goal ->
                        GoalProgressCard(
                            goal    = goal,
                            onClick = { onGoalClick(goal.id) }
                        )
                    }
                }
            }
        }

        item {
            SectionHeader(
                title    = "RECENT",
                action   = "See all",
                onAction = onSeeAllExpenses
            )
        }

        if (uiState.recentExpenses.isEmpty()) {
            item { EmptyExpensesHint(onAddExpense) }
        } else {
            items(uiState.recentExpenses) { expense ->
                ExpenseItem(expense = expense)
            }
        }

        uiState.financeScore?.let { score ->
            item { ScoreTipCard(tip = score.tip) }
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
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text  = "$greeting 👋",
                style = ZeltaTypography.bodyMedium,
                color = ZeltaTextSecondary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text  = userName,
                style = ZeltaTypography.headlineLarge,
                color = ZeltaTextPrimary
            )
        }

        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Sync dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        when (syncState) {
                            SyncState.SYNCED  -> ZeltaMint
                            SyncState.PENDING -> ZeltaWarning
                            SyncState.FAILED  -> ZeltaDanger
                        }
                    )
            )

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(ZeltaBgElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Rounded.Notifications,
                    contentDescription = "Notifications",
                    tint               = ZeltaTextSecondary,
                    modifier           = Modifier.size(20.dp)
                )
            }
        }
    }
}

// Balance Card
@Composable
private fun BalanceCard(uiState: HomeUiState) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue   = if (animationPlayed) uiState.overallBudgetUsage else 0f,
        animationSpec = tween(1200),
        label         = "budget_progress"
    )
    LaunchedEffect(Unit) { animationPlayed = true }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF2A1FCC),
                        Color(0xFF6C63FF),
                        Color(0xFF8B5CF6)
                    )
                )
            )
            .padding(24.dp)
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.04f))
                .align(Alignment.TopEnd)
        )
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.04f))
                .align(Alignment.BottomStart)
        )

        Column {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text  = "SPENT THIS MONTH",
                        style = ZeltaTypography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text  = "$${"%.2f".format(uiState.totalSpentThisMonth)}",
                        style = ZeltaTypography.displayLarge,
                        color = Color.White
                    )
                }

                // Score bubble
                uiState.financeScore?.let { score ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text  = score.score.toString(),
                            style = ZeltaTypography.headlineMedium,
                            color = Color.White
                        )
                        Text(
                            text  = "SCORE",
                            style = ZeltaTypography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Progress bar
            LinearProgressIndicator(
                progress      = { animatedProgress },
                modifier      = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(100.dp)),
                color         = ZeltaMint,
                trackColor    = Color.White.copy(alpha = 0.15f),
                strokeCap     = StrokeCap.Round
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (uiState.totalBudgetLimit > 0) {
                    Text(
                        text  = "Budget $${"%.0f".format(uiState.totalBudgetLimit)}",
                        style = ZeltaTypography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text  = "${"%.0f".format(uiState.overallBudgetUsage * 100)}% used",
                        style = ZeltaTypography.bodySmall,
                        color = ZeltaMint
                    )
                } else {
                    Text(
                        text  = "No budget set yet",
                        style = ZeltaTypography.bodySmall,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

// Quick Stats
@Composable
private fun QuickStatsRow(uiState: HomeUiState) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            emoji    = "🎯",
            label    = "Goals",
            value    = "${uiState.activeGoals.size}",
            color    = ZeltaIndigo
        )
        StatCard(
            modifier = Modifier.weight(1f),
            emoji    = "🗂",
            label    = "Budgets",
            value    = "${uiState.budgets.size}",
            color    = ZeltaMint
        )
        StatCard(
            modifier = Modifier.weight(1f),
            emoji    = "✨",
            label    = "Score",
            value    = "${uiState.financeScore?.score ?: 0}",
            color    = when {
                (uiState.financeScore?.score ?: 0) >= 85 -> ZeltaMint
                (uiState.financeScore?.score ?: 0) >= 65 -> ZeltaIndigoLight
                (uiState.financeScore?.score ?: 0) >= 45 -> ZeltaWarning
                else                                      -> ZeltaDanger
            }
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier,
    emoji: String,
    label: String,
    value: String,
    color: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(ZeltaBgCard)
            .padding(14.dp)
    ) {
        Column {
            Text(text = emoji, style = ZeltaTypography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(
                text  = value,
                style = ZeltaTypography.headlineMedium,
                color = color
            )
            Text(
                text  = label,
                style = ZeltaTypography.labelSmall,
                color = ZeltaTextDim
            )
        }
    }
}

// Section Header
@Composable
private fun SectionHeader(
    title: String,
    action: String?,
    onAction: () -> Unit
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text  = title,
            style = ZeltaTypography.labelSmall,
            color = ZeltaTextDim
        )
        if (action != null) {
            Row(
                modifier          = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onAction() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text  = action,
                    style = ZeltaTypography.labelLarge,
                    color = ZeltaIndigoLight
                )
                Icon(
                    imageVector        = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    contentDescription = null,
                    tint               = ZeltaIndigoLight,
                    modifier           = Modifier.size(10.dp)
                )
            }
        }
    }
}

// Goal Card
@Composable
private fun GoalProgressCard(
    goal: Goal,
    onClick: () -> Unit
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue   = if (animationPlayed) goal.progressPercent else 0f,
        animationSpec = tween(1000),
        label         = "goal_${goal.id}"
    )
    LaunchedEffect(Unit) { animationPlayed = true }

    Box(
        modifier = Modifier
            .width(160.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(ZeltaBgCard)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text  = goal.icon,
                    style = ZeltaTypography.headlineMedium
                )
                Text(
                    text  = "${"%.0f".format(goal.progressPercent * 100)}%",
                    style = ZeltaTypography.labelLarge,
                    color = ZeltaIndigoLight
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text     = goal.name,
                style    = ZeltaTypography.titleMedium,
                color    = ZeltaTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text  = "$${"%.0f".format(goal.currentAmount)} / ${"%.0f".format(goal.targetAmount)}",
                style = ZeltaTypography.bodySmall,
                color = ZeltaTextDim
            )
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress   = { animatedProgress },
                modifier   = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(100.dp)),
                color      = ZeltaIndigo,
                trackColor = ZeltaBgElevated,
                strokeCap  = StrokeCap.Round
            )
        }
    }
}

// Expense Item
@Composable
private fun ExpenseItem(expense: Expense) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(ZeltaBgCard)
            .padding(14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier         = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(ZeltaIndigo.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text  = "💸",
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
                color = ZeltaTextDim
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text  = "-$${"%.2f".format(expense.amount)}",
                style = ZeltaTypography.headlineSmall,
                color = ZeltaExpense
            )
        }
    }
}

// Empty State
@Composable
private fun EmptyExpensesHint(onAddExpense: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(ZeltaBgCard)
            .clickable { onAddExpense() }
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "💸", style = ZeltaTypography.displayMedium)
            Text(
                text  = "No expenses yet",
                style = ZeltaTypography.headlineSmall,
                color = ZeltaTextPrimary
            )
            Text(
                text  = "Tap anywhere to log your first one",
                style = ZeltaTypography.bodyMedium,
                color = ZeltaTextDim
            )
        }
    }
}

//Score Tip
@Composable
private fun ScoreTipCard(tip: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        ZeltaIndigo.copy(alpha = 0.15f),
                        ZeltaMint.copy(alpha = 0.05f)
                    )
                )
            )
            .padding(18.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment     = Alignment.Top
    ) {
        Text(text = "💡", style = ZeltaTypography.headlineMedium)
        Column {
            Text(
                text  = "Zelta Insight",
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