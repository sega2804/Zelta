package com.crypticsamsara.zelta.ui.insight


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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crypticsamsara.zelta.data.local.dao.CategoryTotal
import com.crypticsamsara.zelta.domain.model.Category
import com.crypticsamsara.zelta.domain.usecase.FinanceScore
import com.crypticsamsara.zelta.domain.usecase.ScoreGrade
import com.crypticsamsara.zelta.ui.component.ZeltaCard
import com.crypticsamsara.zelta.ui.component.ZeltaHeroCard
import com.crypticsamsara.zelta.ui.theme.ZeltaBgBase
import com.crypticsamsara.zelta.ui.theme.ZeltaBgCard
import com.crypticsamsara.zelta.ui.theme.ZeltaBgElevated
import com.crypticsamsara.zelta.ui.theme.ZeltaCoral
import com.crypticsamsara.zelta.ui.theme.ZeltaCoralLight
import com.crypticsamsara.zelta.ui.theme.ZeltaDanger
import com.crypticsamsara.zelta.ui.theme.ZeltaTeal
import com.crypticsamsara.zelta.ui.theme.ZeltaTealLight
import com.crypticsamsara.zelta.ui.theme.ZeltaTextDim
import com.crypticsamsara.zelta.ui.theme.ZeltaTextPrimary
import com.crypticsamsara.zelta.ui.theme.ZeltaTextSecondary
import com.crypticsamsara.zelta.ui.theme.ZeltaTypography
import com.crypticsamsara.zelta.ui.theme.ZeltaWarning

@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier       = Modifier
            .fillMaxSize()
            .background(ZeltaBgBase),
        contentPadding = PaddingValues(
            start  = 20.dp,
            end    = 20.dp,
            top    = 20.dp,
            bottom = 110.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text  = "Insights",
                style = ZeltaTypography.headlineLarge,
                color = ZeltaTextPrimary
            )
            Text(
                text  = uiState.currentMonth.toString(),
                style = ZeltaTypography.bodyMedium,
                color = ZeltaTextSecondary
            )
        }

        // Month comparison
        item {
            ZeltaCard {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text  = "THIS MONTH",
                            style = ZeltaTypography.labelSmall,
                            color = ZeltaTextDim
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text  = "$${"%.2f".format(uiState.totalSpent)}",
                            style = ZeltaTypography.displaySmall,
                            color = ZeltaTextPrimary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text  = "LAST MONTH",
                            style = ZeltaTypography.labelSmall,
                            color = ZeltaTextDim
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text  = "$${"%.2f".format(uiState.lastMonthTotal)}",
                            style = ZeltaTypography.displaySmall,
                            color = ZeltaTextSecondary
                        )
                    }
                }

                if (uiState.lastMonthTotal > 0) {
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(
                                if (uiState.isSpendingUp)
                                    ZeltaCoral.copy(alpha = 0.12f)
                                else ZeltaTeal.copy(alpha = 0.12f)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text  = "${if (uiState.isSpendingUp) "↑" else "↓"} ${"%.1f".format(
                                kotlin.math.abs(uiState.monthlyDeltaPercent)
                            )}% vs last month",
                            style = ZeltaTypography.labelLarge,
                            color = if (uiState.isSpendingUp) ZeltaCoral else ZeltaTeal,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Finance Score
        uiState.financeScore?.let { score ->
            item { InsightsScoreCard(score = score) }
        }

        // Weekly chart
        if (uiState.weeklySpend.isNotEmpty()) {
            item { InsightsWeeklyCard(weeklySpend = uiState.weeklySpend) }
        }

        // Category breakdown
        if (uiState.categoryTotals.isNotEmpty()) {
            item {
                InsightsCategoryCard(
                    categoryTotals = uiState.categoryTotals,
                    categories     = uiState.categories,
                    totalSpent     = uiState.totalSpent ?: 0.0
                )
            }
        }

        // Tip
        uiState.financeScore?.let { score ->
            item {
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
                            text  = score.tip,
                            style = ZeltaTypography.bodyMedium,
                            color = ZeltaTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InsightsScoreCard(score: FinanceScore) {
    ZeltaHeroCard {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Box(
                modifier         = Modifier.size(90.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val strokeWidth = 8.dp.toPx()
                    val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = strokeWidth,
                        cap   = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    val sweep = (score.score / 100f) * 270f
                    drawArc(
                        color      = Color.White.copy(alpha = 0.08f),
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter  = false,
                        style      = stroke
                    )
                    drawArc(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFF6B4A),
                                Color(0xFF00E8C0)
                            )
                        ),
                        startAngle = 135f,
                        sweepAngle = sweep,
                        useCenter  = false,
                        style      = stroke
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text  = "${score.score}",
                        style = ZeltaTypography.headlineMedium,
                        color = Color.White
                    )
                    Text(
                        text  = "/100",
                        style = ZeltaTypography.labelSmall,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text  = "FINANCE SCORE",
                    style = ZeltaTypography.labelSmall,
                    color = Color.White.copy(alpha = 0.45f)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = score.grade.label,
                    style = ZeltaTypography.headlineSmall,
                    color = when (score.grade) {
                        ScoreGrade.EXCELLENT -> ZeltaTealLight
                        ScoreGrade.GOOD      -> ZeltaTeal
                        ScoreGrade.FAIR      -> ZeltaWarning
                        ScoreGrade.POOR      -> ZeltaDanger
                    }
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text     = score.tip,
                    style    = ZeltaTypography.bodySmall,
                    color    = Color.White.copy(alpha = 0.55f),
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun InsightsWeeklyCard(weeklySpend: Map<String, Double>) {
    var played by remember { mutableStateOf(false) }
    val prog   by animateFloatAsState(
        targetValue   = if (played) 1f else 0f,
        animationSpec = tween(1000),
        label         = "weekly"
    )
    LaunchedEffect(Unit) { played = true }

    ZeltaCard {
        Column {
            Text(
                text  = "Weekly Spending",
                style = ZeltaTypography.headlineSmall,
                color = ZeltaTextPrimary
            )
            Spacer(Modifier.height(16.dp))

            val maxSpend = weeklySpend.values.maxOrNull()?.takeIf { it > 0 } ?: 1.0

            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment     = Alignment.Bottom
            ) {
                weeklySpend.entries.forEachIndexed { index, (day, spend) ->
                    val barHeight = (spend / maxSpend).toFloat()
                    val isToday  = index == weeklySpend.size - 1
                    val animated = barHeight * prog

                    Column(
                        modifier            = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        if (spend > 0) {
                            Text(
                                text      = "$${"%.0f".format(spend)}",
                                style     = ZeltaTypography.labelSmall,
                                color     = if (isToday) ZeltaTeal else ZeltaTextDim,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(3.dp))
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((90 * animated).dp.coerceAtLeast(4.dp))
                                .clip(RoundedCornerShape(topStart = 7.dp, topEnd = 7.dp))
                                .background(
                                    if (isToday)
                                        Brush.verticalGradient(
                                            listOf(ZeltaTealLight, ZeltaTeal)
                                        )
                                    else Brush.verticalGradient(
                                        listOf(
                                            ZeltaTeal.copy(alpha = 0.35f),
                                            ZeltaTeal.copy(alpha = 0.12f)
                                        )
                                    )
                                )
                        )
                        Spacer(Modifier.height(6.dp))
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

@Composable
private fun InsightsCategoryCard(
    categoryTotals: List<CategoryTotal>,
    categories: List<Category>,
    totalSpent: Double
) {
    ZeltaCard {
        Column {
            Text(
                text  = "By Category",
                style = ZeltaTypography.headlineSmall,
                color = ZeltaTextPrimary
            )
            Spacer(Modifier.height(14.dp))

            categoryTotals.forEach { total ->
                val category = categories.find { it.id == total.categoryId }
                val safeTotal = totalSpent.takeIf { it > 0.0 } ?: 1.0
                val percent   = (total.total / safeTotal).toFloat()

                var played by remember { mutableStateOf(false) }
                val animWidth by animateFloatAsState(
                    targetValue   = if (played) percent else 0f,
                    animationSpec = tween(800),
                    label         = "cat_${total.categoryId}"
                )
                LaunchedEffect(Unit) { played = true }

                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(category?.color ?: ZeltaTeal)
                    )
                    Text(
                        text     = category?.name ?: total.categoryId,
                        style    = ZeltaTypography.bodyMedium,
                        color    = ZeltaTextPrimary,
                        modifier = Modifier.width(80.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(ZeltaBgElevated)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animWidth)
                                .height(5.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(category?.color ?: ZeltaTeal)
                        )
                    }
                    Text(
                        text      = "$${"%.0f".format(total.total)}",
                        style     = ZeltaTypography.labelLarge,
                        color     = ZeltaTextSecondary,
                        modifier  = Modifier.width(44.dp),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}