package com.crypticsamsara.zelta.ui.home

import android.graphics.Color.parseColor
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
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crypticsamsara.zelta.domain.model.Expense
import com.crypticsamsara.zelta.domain.model.Goal
import com.crypticsamsara.zelta.domain.model.SyncState
import com.crypticsamsara.zelta.domain.usecase.FinanceScore
import com.crypticsamsara.zelta.ui.component.ZeltaCard
import com.crypticsamsara.zelta.ui.component.ZeltaHeroCard
import com.crypticsamsara.zelta.ui.component.HomeShimmer
import com.crypticsamsara.zelta.ui.home.QuickActionsRow
import com.crypticsamsara.zelta.ui.theme.ZeltaBgBase
import com.crypticsamsara.zelta.ui.theme.ZeltaBgCard
import com.crypticsamsara.zelta.ui.theme.ZeltaBgElevated
import com.crypticsamsara.zelta.ui.theme.ZeltaBorder
import com.crypticsamsara.zelta.ui.theme.ZeltaCoral
import com.crypticsamsara.zelta.ui.theme.ZeltaCoralLight
import com.crypticsamsara.zelta.ui.theme.ZeltaDanger
import com.crypticsamsara.zelta.ui.theme.ZeltaExpense
import com.crypticsamsara.zelta.ui.theme.ZeltaTeal
import com.crypticsamsara.zelta.ui.theme.ZeltaTealDark
import com.crypticsamsara.zelta.ui.theme.ZeltaTealGlow
import com.crypticsamsara.zelta.ui.theme.ZeltaTealLight
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
            LazyColumn(
                modifier       = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start  = 20.dp,
                    end    = 20.dp,
                    top    = 20.dp,
                    bottom = 110.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    HomeHeader(
                        greeting  = uiState.greeting,
                        userName  = uiState.userName.ifBlank { "there" },
                        syncState = uiState.syncState
                    )
                }

                item {
                    FinanceScoreHeroCard(
                        financeScore = uiState.financeScore
                    )
                }

                item {
                    QuickActionsRow(
                        dailySpend       = uiState.totalSpentThisMonth / 30,
                        remainingBudget  = (uiState.totalBudgetLimit - uiState.totalSpentThisMonth)
                            .coerceAtLeast(0.0),
                        onAddExpense     = onAddExpense
                    )
                }

                item {
                    WeeklySpendCard(uiState = uiState)
                }

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
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
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
                        title    = "RECENT TRANSACTIONS",
                        action   = "See all",
                        onAction = onSeeAllExpenses
                    )
                }

                if (uiState.recentExpenses.isEmpty()) {
                    item { EmptyTransactionsHint(onAddExpense) }
                } else {
                    items(uiState.recentExpenses) { expense ->
                        TransactionItem(expense = expense)
                    }
                }

                uiState.financeScore?.let { score ->
                    item { InsightTipCard(tip = score.tip) }
                }
            }
        }

        FloatingActionButton(
            onClick        = onAddExpense,
            modifier       = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            shape          = RoundedCornerShape(100.dp),
            containerColor = ZeltaTeal,
            contentColor   = Color(0xFF07080F)
        ) {
            Icon(
                imageVector        = Icons.Rounded.Add,
                contentDescription = "Add Expense",
                modifier           = Modifier.size(24.dp)
            )
        }
    }
}

//  Header 
@Composable
private fun HomeHeader(
    greeting: String,
    userName: String,
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
            Text(
                text  = userName,
                style = ZeltaTypography.headlineLarge,
                color = ZeltaTextPrimary
            )
        }
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        when (syncState) {
                            SyncState.SYNCED  -> ZeltaTeal
                            SyncState.PENDING -> ZeltaWarning
                            SyncState.FAILED  -> ZeltaDanger
                        }
                    )
            )
            Box(
                modifier         = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ZeltaBgCard),
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

// Finance Score Hero 
@Composable
private fun FinanceScoreHeroCard(financeScore: FinanceScore?) {
    ZeltaHeroCard {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text  = "FINANCE SCORE",
                style = ZeltaTypography.labelSmall,
                color = Color.White.copy(alpha = 0.45f)
            )
            Spacer(Modifier.height(12.dp))

            // Score ring
            Box(
                modifier         = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val stroke = Stroke(
                        width = 8.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    val score      = financeScore?.score ?: 0
                    val sweep      = (score / 100f) * 270f
                    val startAngle = 135f

                    drawArc(
                        color      = Color.White.copy(alpha = 0.08f),
                        startAngle = startAngle,
                        sweepAngle = 270f,
                        useCenter  = false,
                        style      = stroke
                    )
                    drawArc(
                        brush      = Brush.linearGradient(
                            colors = listOf(
                                Color(parseColor("#FF6B4A")),
                                Color(parseColor("#00E8C0"))
                            )
                        ),
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter  = false,
                        style      = stroke
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🧑", style = ZeltaTypography.titleLarge)
                    Text(
                        text  = "${financeScore?.score ?: 0}",
                        style = ZeltaTypography.headlineMedium,
                        color = Color.White
                    )
                    Text(
                        text  = "/100",
                        style = ZeltaTypography.bodySmall,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text  = "Tip: ",
                    style = ZeltaTypography.bodyMedium,
                    color = ZeltaCoralLight,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text     = financeScore?.tip ?: "Keep tracking your expenses daily.",
                    style    = ZeltaTypography.bodyMedium,
                    color    = Color.White.copy(alpha = 0.65f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// Quick Actions
@Composable
private fun QuickActionsRow(
    dailySpend: Double,
    remainingBudget: Double,
    onAddExpense: () -> Unit
) {
    Row(
        modifier            = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ZeltaCard(
            modifier     = Modifier.weight(1f),
            cornerRadius = 14.dp
        ) {
            Column {
                Text(
                    text  = "DAILY SPEND",
                    style = ZeltaTypography.labelSmall,
                    color = ZeltaTextDim
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = "$${"%.2f".format(dailySpend)}",
                    style = ZeltaTypography.headlineSmall,
                    color = ZeltaTextPrimary
                )
            }
        }

        ZeltaCard(
            modifier     = Modifier.weight(1f),
            cornerRadius = 14.dp
        ) {
            Column {
                Text(
                    text  = "REMAINING",
                    style = ZeltaTypography.labelSmall,
                    color = ZeltaTextDim
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = "$${"%.2f".format(remainingBudget)}",
                    style = ZeltaTypography.headlineSmall,
                    color = ZeltaTeal
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(ZeltaTealDark, ZeltaTeal)
                    )
                )
                .clickable { onAddExpense() }
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text  = "+",
                    style = ZeltaTypography.headlineMedium,
                    color = Color(0xFF07080F),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text  = "Add",
                    style = ZeltaTypography.labelSmall,
                    color = Color(0xFF07080F)
                )
            }
        }
    }
}

// Weekly Chart
@Composable
private fun WeeklySpendCard(uiState: HomeUiState) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue   = if (animationPlayed) 1f else 0f,
        animationSpec = tween(1000),
        label         = "weekly"
    )
    LaunchedEffect(Unit) { animationPlayed = true }

    ZeltaCard {
        Column {
            Text(
                text  = "Weekly Spending",
                style = ZeltaTypography.headlineSmall,
                color = ZeltaTextPrimary
            )
            Spacer(Modifier.height(14.dp))

            val weeklyData = uiState.categoryTotals
            val maxVal     = 100.0

            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment     = Alignment.Bottom
            ) {
                val days    = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")
                val heights = listOf(0.35f, 0.55f, 0.40f, 0.20f, 0.80f, 0.55f, 0.15f)

                days.forEachIndexed { index, day ->
                    val isToday  = index == 4
                    val barH     = heights[index] * animatedProgress

                    Column(
                        modifier            = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((70 * barH).dp.coerceAtLeast(4.dp))
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(
                                    if (isToday)
                                        Brush.verticalGradient(
                                            listOf(ZeltaTealLight, ZeltaTeal)
                                        )
                                    else Brush.verticalGradient(
                                        listOf(
                                            ZeltaTeal.copy(alpha = 0.35f),
                                            ZeltaTeal.copy(alpha = 0.15f)
                                        )
                                    )
                                )
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(
                            text  = day,
                            style = ZeltaTypography.labelSmall,
                            color = if (isToday) ZeltaTeal else ZeltaTextDim
                        )
                    }
                }
            }
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
            Text(
                text     = "$action →",
                style    = ZeltaTypography.labelLarge,
                color    = ZeltaTeal,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onAction() }
                    .padding(4.dp)
            )
        }
    }
}

// Goal Progress Card
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
            .width(150.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(ZeltaBgCard)
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(text = goal.icon, style = ZeltaTypography.headlineSmall)
                Text(
                    text  = "${"%.0f".format(goal.progressPercent * 100)}%",
                    style = ZeltaTypography.labelLarge,
                    color = ZeltaTeal,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text     = goal.name,
                style    = ZeltaTypography.titleMedium,
                color    = ZeltaTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text  = "$${"%.0f".format(goal.currentAmount)} of ${"%.0f".format(goal.targetAmount)}",
                style = ZeltaTypography.bodySmall,
                color = ZeltaTextDim
            )
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress   = { animatedProgress },
                modifier   = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(100.dp)),
                color      = ZeltaTeal,
                trackColor = ZeltaBgElevated,
                strokeCap  = StrokeCap.Round
            )
        }
    }
}

// Transaction Item
@Composable
private fun TransactionItem(expense: Expense) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ZeltaBgCard)
            .padding(12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier         = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ZeltaTeal.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "💸", style = ZeltaTypography.titleMedium)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text     = expense.note.ifBlank { "Expense" },
                style    = ZeltaTypography.titleMedium,
                color    = ZeltaTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(ZeltaTeal)
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
private fun EmptyTransactionsHint(onAdd: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(ZeltaBgCard)
            .clickable { onAdd() }
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = "💸", style = ZeltaTypography.displaySmall)
            Text(
                text  = "No transactions yet",
                style = ZeltaTypography.headlineSmall,
                color = ZeltaTextPrimary
            )
            Text(
                text  = "Tap to log your first expense",
                style = ZeltaTypography.bodyMedium,
                color = ZeltaTextDim
            )
        }
    }
}

// Insight Tip
@Composable
private fun InsightTipCard(tip: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        ZeltaTeal.copy(alpha = 0.08f),
                        ZeltaCoral.copy(alpha = 0.04f)
                    )
                )
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment     = Alignment.Top
    ) {
        Text(text = "💡", style = ZeltaTypography.titleLarge)
        Column {
            Text(
                text  = "Zelta Insight",
                style = ZeltaTypography.titleMedium,
                color = ZeltaTealLight,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text  = tip,
                style = ZeltaTypography.bodyMedium,
                color = ZeltaTextSecondary
            )
        }
    }
}