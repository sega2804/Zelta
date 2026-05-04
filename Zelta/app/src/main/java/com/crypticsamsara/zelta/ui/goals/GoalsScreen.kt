package com.crypticsamsara.zelta.ui.goals


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crypticsamsara.zelta.domain.model.Goal
import com.crypticsamsara.zelta.ui.component.ZeltaHeroCard
import com.crypticsamsara.zelta.ui.component.ZeltaPrimaryButton
import com.crypticsamsara.zelta.ui.component.ZeltaSmallButton
import com.crypticsamsara.zelta.ui.theme.ZeltaAmber
import com.crypticsamsara.zelta.ui.theme.ZeltaBgBase
import com.crypticsamsara.zelta.ui.theme.ZeltaBgCard
import com.crypticsamsara.zelta.ui.theme.ZeltaBgElevated
import com.crypticsamsara.zelta.ui.theme.ZeltaCoral
import com.crypticsamsara.zelta.ui.theme.ZeltaCoralDark
import com.crypticsamsara.zelta.ui.theme.ZeltaTeal
import com.crypticsamsara.zelta.ui.theme.ZeltaTealDark
import com.crypticsamsara.zelta.ui.theme.ZeltaTealLight
import com.crypticsamsara.zelta.ui.theme.ZeltaTextDim
import com.crypticsamsara.zelta.ui.theme.ZeltaTextPrimary
import com.crypticsamsara.zelta.ui.theme.ZeltaTextSecondary
import com.crypticsamsara.zelta.ui.theme.ZeltaTypography
import com.crypticsamsara.zelta.ui.theme.ZeltaWarning

@Composable
fun GoalsScreen(
    viewModel: GoalsViewModel = hiltViewModel()
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
        Column(modifier = Modifier.fillMaxSize()) {

            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Text(
                    text  = "My Goals",
                    style = ZeltaTypography.headlineLarge,
                    color = ZeltaTextPrimary
                )
                Row(
                    verticalAlignment     = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text  = "$${"%.2f".format(uiState.totalSaved)}",
                        style = ZeltaTypography.displaySmall,
                        color = ZeltaTeal
                    )
                    Text(
                        text     = "saved of $${"%.2f".format(uiState.totalTarget)}",
                        style    = ZeltaTypography.bodyMedium,
                        color    = ZeltaTextDim,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }

            // Summary hero
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                ZeltaHeroCard {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text  = "ACTIVE",
                                style = ZeltaTypography.labelSmall,
                                color = Color.White.copy(alpha = 0.45f)
                            )
                            Text(
                                text  = "${uiState.activeGoals.size}",
                                style = ZeltaTypography.displaySmall,
                                color = Color.White
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(1.dp, 40.dp)
                                .background(Color.White.copy(alpha = 0.1f))
                                .align(Alignment.CenterVertically)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text  = "TOTAL SAVED",
                                style = ZeltaTypography.labelSmall,
                                color = Color.White.copy(alpha = 0.45f)
                            )
                            Text(
                                text  = "$${"%.0f".format(uiState.totalSaved)}",
                                style = ZeltaTypography.displaySmall,
                                color = ZeltaTealLight
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(1.dp, 40.dp)
                                .background(Color.White.copy(alpha = 0.1f))
                                .align(Alignment.CenterVertically)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text  = "COMPLETED",
                                style = ZeltaTypography.labelSmall,
                                color = Color.White.copy(alpha = 0.45f)
                            )
                            Text(
                                text  = "${uiState.completedGoals.size}",
                                style = ZeltaTypography.displaySmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Tabs
            TabRow(
                selectedTabIndex = GoalTab.entries.indexOf(uiState.selectedTab),
                containerColor   = ZeltaBgBase,
                contentColor     = ZeltaTeal,
                indicator        = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(
                            tabPositions[GoalTab.entries.indexOf(uiState.selectedTab)]
                        ),
                        color    = ZeltaTeal,
                        height   = 2.dp
                    )
                }
            ) {
                GoalTab.entries.forEach { tab ->
                    Tab(
                        selected = uiState.selectedTab == tab,
                        onClick  = { viewModel.onTabSelected(tab) },
                        text     = {
                            Text(
                                text  = tab.label,
                                style = ZeltaTypography.titleMedium,
                                color = if (uiState.selectedTab == tab)
                                    ZeltaTeal else ZeltaTextDim
                            )
                        }
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            val goalsToShow = when (uiState.selectedTab) {
                GoalTab.ACTIVE    -> uiState.activeGoals
                GoalTab.COMPLETED -> uiState.completedGoals
            }

            if (goalsToShow.isEmpty()) {
                Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .padding(40.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(ZeltaBgCard)
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text  = if (uiState.selectedTab == GoalTab.ACTIVE) "🎯" else "🏆",
                            style = ZeltaTypography.displaySmall
                        )
                        Text(
                            text  = if (uiState.selectedTab == GoalTab.ACTIVE)
                                "No active goals" else "No completed goals yet",
                            style = ZeltaTypography.headlineSmall,
                            color = ZeltaTextPrimary
                        )
                        Text(
                            text  = if (uiState.selectedTab == GoalTab.ACTIVE)
                                "Tap + to create your first goal"
                            else "Complete a goal to see it here",
                            style = ZeltaTypography.bodyMedium,
                            color = ZeltaTextDim
                        )
                        if (uiState.selectedTab == GoalTab.ACTIVE) {
                            Spacer(Modifier.height(8.dp))
                            ZeltaPrimaryButton(
                                text    = "Create Goal",
                                onClick = { viewModel.showAddGoalSheet() }
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding      = PaddingValues(
                        start  = 20.dp,
                        end    = 20.dp,
                        bottom = 110.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(goalsToShow, key = { it.id }) { goal ->
                        GoalCard(
                            goal         = goal,
                            index        = goalsToShow.indexOf(goal),
                            onContribute = { viewModel.showContributeSheet(goal.id) },
                            onDelete     = { viewModel.onDeleteGoal(goal.id) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick        = { viewModel.showAddGoalSheet() },
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
            visible = uiState.justCompletedGoal != null,
            enter   = fadeIn(), exit = fadeOut()
        ) {
            uiState.justCompletedGoal?.let { goal ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ZeltaBgBase.copy(alpha = 0.95f))
                        .clickable { viewModel.dismissCelebration() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .padding(32.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(ZeltaTeal.copy(alpha = 0.2f), ZeltaBgCard)
                                )
                            )
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "🎉", style = ZeltaTypography.displayLarge)
                        Icon(
                            Icons.Rounded.EmojiEvents,
                            contentDescription = null,
                            tint               = ZeltaAmber,
                            modifier           = Modifier.size(48.dp)
                        )
                        Text(
                            text  = "Goal Complete!",
                            style = ZeltaTypography.headlineLarge,
                            color = ZeltaTextPrimary
                        )
                        Text(
                            text  = "\"${goal.name}\" — you did it!",
                            style = ZeltaTypography.bodyLarge,
                            color = ZeltaTextSecondary
                        )
                        Spacer(Modifier.height(8.dp))
                        ZeltaPrimaryButton(
                            text    = "Celebrate! 🥳",
                            onClick = { viewModel.dismissCelebration() }
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = uiState.isAddGoalSheetVisible,
            enter   = fadeIn() + slideInVertically { it },
            exit    = fadeOut()
        ) {
            AddGoalSheet(
                isLoading = uiState.isSubmitting,
                onDismiss = { viewModel.hideAddGoalSheet() },
                onConfirm = { name, icon, target, deadline ->
                    viewModel.onAddGoal(name, icon, target, deadline)
                }
            )
        }

        AnimatedVisibility(
            visible = uiState.isContributeSheetVisible,
            enter   = fadeIn() + slideInVertically { it },
            exit    = fadeOut()
        ) {
            uiState.selectedGoal?.let { goal ->
                ContributeSheet(
                    goal      = goal,
                    isLoading = uiState.isSubmitting,
                    onDismiss = { viewModel.hideContributeSheet() },
                    onConfirm = { amount -> viewModel.onContribute(goal.id, amount) }
                )
            }
        }

        SnackbarHost(
            hostState = snackbarState,
            modifier  = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun GoalCard(
    goal: Goal,
    index: Int,
    onContribute: () -> Unit,
    onDelete: () -> Unit
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue   = if (animationPlayed) goal.progressPercent else 0f,
        animationSpec = tween(1000),
        label         = "goal_${goal.id}"
    )
    LaunchedEffect(Unit) { animationPlayed = true }

    // Alternate teal and coral for goal progress bars
    val accentColor = if (index % 2 == 0) ZeltaTeal else ZeltaCoral
    val accentDark  = if (index % 2 == 0) ZeltaTealDark else ZeltaCoralDark

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(ZeltaBgCard)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(accentColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = goal.icon, style = ZeltaTypography.headlineSmall)
                    }
                    Column {
                        Text(
                            text     = goal.name,
                            style    = ZeltaTypography.titleLarge,
                            color    = ZeltaTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (goal.deadline != null) {
                            Text(
                                text  = "By ${goal.deadline}",
                                style = ZeltaTypography.bodySmall,
                                color = if (goal.isOverdue) ZeltaWarning else ZeltaTextDim
                            )
                        }
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Rounded.Delete,
                        contentDescription = "Delete",
                        tint     = ZeltaTextDim,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            LinearProgressIndicator(
                progress   = { animatedProgress },
                modifier   = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(100.dp)),
                color      = accentColor,
                trackColor = ZeltaBgElevated,
                strokeCap  = StrokeCap.Round
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text  = "SAVED",
                        style = ZeltaTypography.labelSmall,
                        color = ZeltaTextDim
                    )
                    Text(
                        text  = "$${"%.2f".format(goal.currentAmount)}",
                        style = ZeltaTypography.headlineSmall,
                        color = accentColor
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text  = "PROGRESS",
                        style = ZeltaTypography.labelSmall,
                        color = ZeltaTextDim
                    )
                    Text(
                        text  = "${"%.0f".format(goal.progressPercent * 100)}%",
                        style = ZeltaTypography.headlineSmall,
                        color = accentColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text  = "REMAINING",
                        style = ZeltaTypography.labelSmall,
                        color = ZeltaTextDim
                    )
                    Text(
                        text  = "$${"%.2f".format(goal.remainingAmount)}",
                        style = ZeltaTypography.headlineSmall,
                        color = ZeltaTextSecondary
                    )
                }
            }

            if (!goal.isCompleted) {
                Spacer(Modifier.height(14.dp))
                ZeltaSmallButton(
                    text    = "+ Add Funds",
                    onClick = onContribute,
                    isCoral = index % 2 != 0
                )
            }
        }
    }
}