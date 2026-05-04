package com.crypticsamsara.zelta.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.crypticsamsara.zelta.ui.theme.ZeltaBgBase
import com.crypticsamsara.zelta.ui.theme.ZeltaBgCard
import com.crypticsamsara.zelta.ui.theme.ZeltaTeal
import com.crypticsamsara.zelta.ui.theme.ZeltaTealLight
import com.crypticsamsara.zelta.ui.theme.ZeltaTextDim
import com.crypticsamsara.zelta.ui.theme.ZeltaTypography

// nav destinations
sealed class ZeltaScreen(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : ZeltaScreen(
        route = "home",
        label = "Home",
        icon = Icons.Rounded.Home
    )
    object Track : ZeltaScreen(
        route = "track",
        label = "Track",
        icon = Icons.Rounded.TrackChanges
    )
    object Goals : ZeltaScreen(
        route = "goals",
        label = "Goals",
        icon = Icons.Rounded.TrackChanges
    )

    object Budget : ZeltaScreen(
        route = "budget",
        label = "Budget",
        icon = Icons.Rounded.AccountBalance
    )
    object Insights : ZeltaScreen(
        route = "insights",
        label = "Insights",
        icon = Icons.Rounded.BarChart
    )
    object Profile : ZeltaScreen (
        route = "profile",
        label = "Profile",
        icon = Icons.Rounded.Person
    )
}

val zeltaNavItems = listOf(
    ZeltaScreen.Home,
    ZeltaScreen.Track,
    ZeltaScreen.Goals,
    ZeltaScreen.Insights,
    ZeltaScreen.Profile,
    ZeltaScreen.Budget
)

// Bottom nav bar
@Composable
fun ZeltaBottomBar(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        containerColor = ZeltaBgCard,
        tonalElevation = 0.dp
    ) {
        zeltaNavItems.forEach { screen ->
            val selected = currentRoute == screen.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(ZeltaScreen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.label
                    )
                },
                label = {
                    Text(
                        text = screen.label,
                        style = ZeltaTypography.labelMedium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ZeltaTealLight,
                    selectedTextColor = ZeltaTealLight,
                    indicatorColor = ZeltaTeal.copy(alpha = 0.15f),
                    unselectedIconColor = ZeltaTextDim,
                    unselectedTextColor = ZeltaTextDim
                )
            )
        }
    }
}

// Main scaffold
@Composable
fun ZeltaScaffold(
    navController: NavController,
    showBottomBar: Boolean = true,
    content: @Composable () -> Unit
){
    Scaffold(
        containerColor = ZeltaBgBase,
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }
            ) {
                ZeltaBottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ZeltaBgBase)
                .padding(innerPadding)
        ) {
            content()
        }
    }
}