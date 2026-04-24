package com.crypticsamsara.zelta.ui.insight


import kotlin.math.abs
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crypticsamsara.zelta.data.local.dao.CategoryTotal
import com.crypticsamsara.zelta.domain.model.Category
import com.crypticsamsara.zelta.domain.usecase.FinanceScore
import com.crypticsamsara.zelta.domain.usecase.ScoreGrade
import com.crypticsamsara.zelta.ui.component.ZeltaCard
import com.crypticsamsara.zelta.ui.component.ZeltaElevatedCard
import com.crypticsamsara.zelta.ui.theme.ZeltaBgBase
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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.collections.find
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ZeltaBgBase),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 24.dp,
            bottom = 100.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            InsightsHeader(
                month = uiState.currentMonth.toString(),
                totalSpent = uiState.totalSpent
            )
        }

        // Month comparison
        item {
            MonthComparisonCard(
                totalSpent = uiState.totalSpent,
                lastMonthTotal = uiState.lastMonthTotal,
                deltaPercent = uiState.monthlyDeltaPercent,
                isSpendingUp = uiState.isSpendingUp
            )
        }

        // Finance score
        uiState.financeScore?.let { score ->
            item {
                FinanceScoreCard(score = score)
            }
        }

        // weekly bar chart
        if (uiState.weeklySpend.isNotEmpty()) {
            item {
                WeeklySpendCard(weeklySpend = uiState.weeklySpend)
            }
        }

        // category Breakdown
        if (uiState.categoryTotals.isNotEmpty()) {
            item {
                CategoryBreakDownCard(
                    categoryTotals = uiState.categoryTotals,
                    categories = uiState.categories,
                    totalSpent = uiState.totalSpent
                )
            }
        }

        // score tip
            uiState.financeScore?.let { score ->
                item {
                    ScoreTipCard(tip = score.tip)
                }
            }
    }
}

@Composable
private fun InsightsHeader(
    month: String,
    totalSpent: Double
) {
    Column {
        Text(
            text = "Insights",
            style = ZeltaTypography.headlineLarge,
            color = ZeltaTextPrimary
        )
    }
}

@Composable
private fun MonthComparisonCard(
    totalSpent: Double,
    lastMonthTotal: Double,
    deltaPercent: Float,
    isSpendingUp: Boolean
) {
    ZeltaCard(cornerRadius = 20.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "THIS MONTH",
                    style = ZeltaTypography.labelSmall,
                    color = ZeltaTextDim
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$${"%.2f".format(totalSpent)}",
                    style = ZeltaTypography.displaySmall,
                    color = ZeltaTextPrimary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "LAST MONTH",
                    style = ZeltaTypography.labelSmall,
                    color = ZeltaTextDim
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$${"%.2f".format(lastMonthTotal)}",
                    style = ZeltaTypography.displaySmall,
                    color = ZeltaTextPrimary
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Delta chip
        if (lastMonthTotal > 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(
                            if (isSpendingUp) ZeltaDanger.copy(alpha = 0.15f)
                            else ZeltaMint.copy(alpha = 015f)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text  = "${if (isSpendingUp) "↑" else "↓"} ${"%.1f".format(
                            abs(deltaPercent)
                        )}% vs last month",
                        style = ZeltaTypography.labelLarge,
                        color = if (isSpendingUp) ZeltaDanger else ZeltaMint
                    )
                }
            }
        }
    }
}

@Composable
private fun FinanceScoreCard(score: FinanceScore) {
    ZeltaCard(
        cornerRadius    = 20.dp,
        backgroundColor = Color(0xFF0D0D20)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Score circle
            Box(
                modifier         = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(ZeltaIndigo, ZeltaMint)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text  = score.score.toString(),
                        style = ZeltaTypography.displaySmall,
                        color = Color.White
                    )
                    Text(
                        text  = "/100",
                        style = ZeltaTypography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text  = "FINANCE SCORE",
                    style = ZeltaTypography.labelSmall,
                    color = ZeltaTextDim
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = score.grade.label,
                    style = ZeltaTypography.headlineMedium,
                    color = when (score.grade) {
                        ScoreGrade.EXCELLENT -> ZeltaMint
                        ScoreGrade.GOOD      -> ZeltaIndigoLight
                        ScoreGrade.FAIR      -> ZeltaWarning
                        ScoreGrade.POOR      -> ZeltaDanger
                    }
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = score.tip,
                    style = ZeltaTypography.bodySmall,
                    color = ZeltaTextSecondary,
                    maxLines = 2
                )
            }
        }
    }
}

// ── Weekly Bar Chart ───────────────────────────────────
@Composable
private fun WeeklySpendCard(
    weeklySpend: Map<String, Double>
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue   = if (animationPlayed) 1f else 0f,
        animationSpec = tween(1000),
        label         = "weekly_bars"
    )
    LaunchedEffect(Unit) { animationPlayed = true }

    ZeltaCard(cornerRadius = 20.dp) {
        Column {
            Text(
                text  = "LAST 7 DAYS",
                style = ZeltaTypography.labelSmall,
                color = ZeltaTextDim
            )
            Spacer(Modifier.height(16.dp))

            val maxSpend = weeklySpend.values.maxOrNull() ?: 1.0

            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment     = Alignment.Bottom
            ) {
                weeklySpend.entries.forEach { (day, spend) ->
                    val barHeight = if (maxSpend == 0.0) 0f
                    else (spend / maxSpend).toFloat()
                    val isToday  = day == weeklySpend.keys.last()

                    Column(
                        modifier            = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        // Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((100 * barHeight * animatedProgress).dp)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(
                                    if (isToday)
                                        Brush.linearGradient(
                                            listOf(ZeltaIndigo, ZeltaIndigoLight)
                                        )
                                    else Brush.linearGradient(
                                        listOf(
                                            ZeltaIndigo.copy(alpha = 0.4f),
                                            ZeltaIndigo.copy(alpha = 0.3f)
                                        )
                                    )
                                )
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text  = day,
                            style = ZeltaTypography.labelSmall,
                            color = if (isToday) ZeltaIndigoLight
                            else ZeltaTextDim
                        )
                    }
                }
            }
        }
    }
}

// Category Breakdown Card
@Composable
private fun CategoryBreakDownCard(
    categoryTotals: List<CategoryTotal>,
    categories: List<Category>,
    totalSpent: Double
) {
    ZeltaCard(cornerRadius = 20.dp) {
        Column {
            Text(
                text  = "BY CATEGORY",
                style = ZeltaTypography.labelSmall,
                color = ZeltaTextDim
            )
            Spacer(Modifier.height(16.dp))

            categoryTotals.forEach { total ->
                val category = categories.find { it.id == total.categoryId }
                val percent  = if (totalSpent == 0.0) 0f
                else (total.total / totalSpent).toFloat()

                var animationPlayed by remember { mutableStateOf(false) }
                val animatedWidth by animateFloatAsState(
                    targetValue   = if (animationPlayed) percent else 0f,
                    animationSpec = tween(800),
                    label         = "category_${total.categoryId}"
                )
                LaunchedEffect(Unit) { animationPlayed = true }

                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Color dot
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(category?.color ?: ZeltaIndigo)
                    )

                    // Category name
                    Text(
                        text     = category?.name ?: total.categoryId,
                        style    = ZeltaTypography.bodyMedium,
                        color    = ZeltaTextPrimary,
                        modifier = Modifier.width(90.dp),
                        maxLines = 1
                    )

                    // Progress bar
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(ZeltaBgElevated)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedWidth)
                                .height(5.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(category?.color ?: ZeltaIndigo)
                        )
                    }

                    // Amount
                    Text(
                        text      = "$${"%.0f".format(total.total)}",
                        style     = ZeltaTypography.labelLarge,
                        color     = ZeltaTextSecondary,
                        modifier  = Modifier.width(48.dp),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

// Score Tip Card
@Composable
private fun ScoreTipCard(tip: String) {
    ZeltaElevatedCard(cornerRadius = 20.dp) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment     = Alignment.Top
        ) {
            Text(text = "💡", style = ZeltaTypography.titleLarge)
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
