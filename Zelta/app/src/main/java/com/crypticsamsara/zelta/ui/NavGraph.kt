package com.crypticsamsara.zelta.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.crypticsamsara.zelta.ui.component.ZeltaScaffold
import com.crypticsamsara.zelta.ui.theme.ZeltaBgBase
import com.crypticsamsara.zelta.ui.theme.ZeltaTextSecondary
import com.crypticsamsara.zelta.ui.theme.ZeltaTypography
import com.crypticsamsara.zelta.ui.component.ZeltaScreen
import com.crypticsamsara.zelta.ui.home.HomeScreen

@Composable
fun ZeltaNavGraph(
    navController: NavHostController = rememberNavController()
) {
    ZeltaScaffold(navController = navController) {
        NavHost(
            navController    = navController,
            startDestination = ZeltaScreen.Home.route
        ) {
            composable(ZeltaScreen.Home.route) {
                HomeScreen(
                    onAddExpense      = {},
                    onSeeAllExpenses  = {
                        navController.navigate(ZeltaScreen.Track.route)
                    },
                    onGoalClick       = {
                        navController.navigate(ZeltaScreen.Goals.route)
                    }
                )
            }

            composable(ZeltaScreen.Track.route) {

                PlaceholderScreen("Track")
            }

            composable(ZeltaScreen.Goals.route) {

                PlaceholderScreen("Goals")
            }

            composable(ZeltaScreen.Insights.route) {

                PlaceholderScreen("Insights")
            }

            composable(ZeltaScreen.Profile.route) {

                PlaceholderScreen("Profile")
            }
        }
    }
}

// Temporary placeholder
@Composable
private fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ZeltaBgBase),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text  = name,
            style = ZeltaTypography.headlineLarge,
            color = ZeltaTextSecondary
        )
    }
}