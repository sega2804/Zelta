package com.crypticsamsara.zelta.ui.goals

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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crypticsamsara.zelta.domain.model.Goal
import com.crypticsamsara.zelta.ui.component.ZeltaCard
import com.crypticsamsara.zelta.ui.component.ZeltaElevatedCard
import com.crypticsamsara.zelta.ui.component.ZeltaPrimaryButton
import com.crypticsamsara.zelta.ui.theme.ZeltaBgBase
import com.crypticsamsara.zelta.ui.theme.ZeltaBgElevated
import com.crypticsamsara.zelta.ui.theme.ZeltaIndigo
import com.crypticsamsara.zelta.ui.theme.ZeltaIndigoLight
import com.crypticsamsara.zelta.ui.theme.ZeltaMint
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
            GoalsHeader(
                totalSaved  = uiState.totalSaved,
                totalTarget = uiState.totalTarget
            )

            //Tabs
            GoalsTabs(
                selectedTab = uiState.selectedTab,
                onTabSelected = { viewModel.onTabSelected(it) }
            )

            Spacer(Modifier.height(16.dp))

            // Content
            val goalsToShow = when (uiState.selectedTab) {
                GoalTab.ACTIVE    -> uiState.activeGoals
                GoalTab.COMPLETED -> uiState.completedGoals
            }

            if (goalsToShow.isEmpty()) {
                EmptyGoalsState(
                    tab       = uiState.selectedTab,
                    onAddGoal = { viewModel.showAddGoalSheet() }
                )
            } else {
                LazyColumn(
                    contentPadding        = PaddingValues(
                        start  = 20.dp,
                        end    = 20.dp,
                        bottom = 100.dp
                    ),
                    verticalArrangement   = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = goalsToShow,
                        key   = { it.id }
                    ) { goal ->
                        GoalCard(
                            goal        = goal,
                            onContribute = {
                                viewModel.showContributeSheet(goal.id)
                            },
                            onDelete    = { viewModel.onDeleteGoal(goal.id) }
                        )
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick        = { viewModel.showAddGoalSheet() },
            modifier       = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            shape          = RoundedCornerShape(16.dp),
            containerColor = ZeltaIndigo,
            contentColor   = ZeltaTextPrimary
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Add Goal")
        }

        // Celebration overlay
        AnimatedVisibility(
            visible = uiState.justCompletedGoal != null,
            enter   = fadeIn(),
            exit    = fadeOut()
        ) {
            uiState.justCompletedGoal?.let { goal ->
                GoalCelebrationOverlay(
                    goal      = goal,
                    onDismiss = { viewModel.dismissCelebration() }
                )
            }
        }

        // Sheets
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
                    onConfirm = { amount ->
                        viewModel.onContribute(goal.id, amount)
                    }
                )
            }
        }

        SnackbarHost(
            hostState = snackbarState,
            modifier  = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// Header
@Composable
private fun GoalsHeader(
    totalSaved: Double,
    totalTarget: Double
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Text(
            text  = "My Goals",
            style = ZeltaTypography.headlineLarge,
            color = ZeltaTextPrimary
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text  = "$${"%.2f".format(totalSaved)} saved of $${"%.2f".format(totalTarget)}",
            style = ZeltaTypography.bodyMedium,
            color = ZeltaTextSecondary
        )
    }
}

// Tabs
@Composable
private fun GoalsTabs(
    selectedTab: GoalTab,
    onTabSelected: (GoalTab) -> Unit
) {
    val tabs = GoalTab.entries.toTypedArray()
    TabRow(
        selectedTabIndex = tabs.indexOf(selectedTab),
        containerColor   = ZeltaBgBase,
        contentColor     = ZeltaIndigoLight,
        indicator        = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier  = Modifier.tabIndicatorOffset(
                    tabPositions[tabs.indexOf(selectedTab)]
                ),
                color     = ZeltaIndigo,
                height    = 2.dp
            )
        }
    ) {
        tabs.forEach { tab ->
            Tab(
                selected         = selectedTab == tab,
                onClick          = { onTabSelected(tab) },
                text             = {
                    Text(
                        text  = tab.label,
                        style = ZeltaTypography.titleMedium,
                        color = if (selectedTab == tab)
                            ZeltaIndigoLight else ZeltaTextDim
                    )
                }
            )
        }
    }
}

// Goal Card
@Composable
private fun GoalCard(
    goal: Goal,
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

    ZeltaCard(cornerRadius = 20.dp) {
        Column {

            //Top Row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Icon bubble
                    Box(
                        modifier         = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ZeltaIndigo.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = goal.icon,
                            style = ZeltaTypography.titleLarge
                        )
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
                                color = if (goal.isOverdue)
                                    ZeltaWarning else ZeltaTextSecondary
                            )
                        }
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector        = Icons.Rounded.Delete,
                        contentDescription = "Delete goal",
                        tint               = ZeltaTextDim,
                        modifier           = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Amounts
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text  = "SAVED",
                        style = ZeltaTypography.labelSmall,
                        color = ZeltaTextDim
                    )
                    Text(
                        text  = "$${"%.2f".format(goal.currentAmount)}",
                        style = ZeltaTypography.headlineMedium,
                        color = ZeltaMint
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
                        style = ZeltaTypography.headlineMedium,
                        color = ZeltaTextSecondary
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress      = { animatedProgress },
                modifier      = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(100.dp)),
                color         = ZeltaIndigo,
                trackColor    = ZeltaBgElevated,
                strokeCap     = StrokeCap.Round
            )

            Spacer(Modifier.height(6.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text  = "${"%.0f".format(goal.progressPercent * 100)}% complete",
                    style = ZeltaTypography.labelMedium,
                    color = ZeltaIndigoLight
                )
                Text(
                    text  = "Target: $${"%.2f".format(goal.targetAmount)}",
                    style = ZeltaTypography.labelMedium,
                    color = ZeltaTextDim
                )
            }

            if (!goal.isCompleted) {
                Spacer(Modifier.height(16.dp))
                ZeltaPrimaryButton(
                    text    = "Add Funds",
                    onClick = onContribute
                )
            }
        }
    }
}

// Empty State
@Composable
private fun EmptyGoalsState(
    tab: GoalTab,
    onAddGoal: () -> Unit
) {
    Box(
        modifier         = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ZeltaElevatedCard(
            modifier     = Modifier.padding(40.dp),
            cornerRadius = 24.dp
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier            = Modifier.fillMaxWidth()
            ) {
                Text(
                    text  = if (tab == GoalTab.ACTIVE) "🎯" else "🏆",
                    style = ZeltaTypography.displayMedium
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text  = if (tab == GoalTab.ACTIVE)
                        "No active goals" else "No completed goals yet",
                    style = ZeltaTypography.headlineSmall,
                    color = ZeltaTextPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = if (tab == GoalTab.ACTIVE)
                        "Set a goal and start saving" else "Complete a goal to see it here",
                    style = ZeltaTypography.bodyMedium,
                    color = ZeltaTextSecondary
                )
                if (tab == GoalTab.ACTIVE) {
                    Spacer(Modifier.height(16.dp))
                    ZeltaPrimaryButton(
                        text    = "Create Goal",
                        onClick = onAddGoal
                    )
                }
            }
        }
    }
}

// Celebration Overlay
@Composable
private fun GoalCelebrationOverlay(
    goal: Goal,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ZeltaBgBase.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        ZeltaCard(
            modifier     = Modifier.padding(32.dp),
            cornerRadius = 28.dp
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier            = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector        = Icons.Rounded.EmojiEvents,
                    contentDescription = null,
                    tint               = ZeltaWarning,
                    modifier           = Modifier.size(56.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text  = "Goal Complete! 🎉",
                    style = ZeltaTypography.headlineLarge,
                    color = ZeltaTextPrimary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text  = "You hit your target for\n\"${goal.name}\"",
                    style = ZeltaTypography.bodyLarge,
                    color = ZeltaTextSecondary
                )
                Spacer(Modifier.height(24.dp))
                ZeltaPrimaryButton(
                    text    = "Celebrate! 🥳",
                    onClick = onDismiss
                )
            }
        }
    }
}