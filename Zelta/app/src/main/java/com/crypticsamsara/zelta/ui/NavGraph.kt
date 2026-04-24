package com.crypticsamsara.zelta.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.crypticsamsara.zelta.ui.auth.AuthScreen
import com.crypticsamsara.zelta.ui.auth.AuthViewModel
import com.crypticsamsara.zelta.ui.budget.BudgetScreen
import com.crypticsamsara.zelta.ui.component.ZeltaScaffold
import com.crypticsamsara.zelta.ui.theme.ZeltaBgBase
import com.crypticsamsara.zelta.ui.theme.ZeltaTextSecondary
import com.crypticsamsara.zelta.ui.theme.ZeltaTypography
import com.crypticsamsara.zelta.ui.component.ZeltaScreen
import com.crypticsamsara.zelta.ui.goals.GoalsScreen
import com.crypticsamsara.zelta.ui.home.HomeScreen
import com.crypticsamsara.zelta.ui.track.TrackScreen


// top level routes
private const val ROUTE_AUTH = "auth"
private const val ROUTE_MAIN = "main"

@Composable
fun ZeltaNavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    // start destination route
    val startDestination = if (authState.currentUser != null)
        ROUTE_MAIN else ROUTE_AUTH

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth flow
        composable(ROUTE_AUTH) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(ROUTE_MAIN) {
                        popUpTo(ROUTE_AUTH) { inclusive = true }
                    }
                }
            )
        }

        // main app
        composable(ROUTE_MAIN) {
            MainNavGraph(
                onSignOut = {
                    authViewModel.onSignOut()
                    navController.navigate(ROUTE_AUTH) {
                        popUpTo(ROUTE_MAIN) { inclusive = true }
                    }
                }
            )
        }
    }
}

// Main nav with bottom bar
@Composable
fun MainNavGraph(onSignOut: () -> Unit) {
    val mainNavController = rememberNavController()

    ZeltaScaffold(navController = mainNavController) {
        NavHost(
            navController    = mainNavController,
            startDestination = ZeltaScreen.Home.route
        ) {
            composable(ZeltaScreen.Home.route) {
                HomeScreen(
                    onAddExpense      = {
                        mainNavController.navigate(ZeltaScreen.Track.route)
                    },
                    onSeeAllExpenses  = {
                        mainNavController.navigate(ZeltaScreen.Track.route)
                    },
                    onGoalClick       = {
                        mainNavController.navigate(ZeltaScreen.Goals.route)
                    }
                )
            }

            composable(ZeltaScreen.Track.route) {
               TrackScreen()
            }

            composable(ZeltaScreen.Goals.route) {

                GoalsScreen()
            }

            composable(ZeltaScreen.Budget.route) {
                BudgetScreen()
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