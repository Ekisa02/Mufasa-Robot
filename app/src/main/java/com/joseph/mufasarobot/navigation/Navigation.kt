package com.joseph.mufasarobot.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.joseph.mufasarobot.screens.*

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Dashboard : Screen("dashboard")
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            val viewModel = SplashViewModel()
            val state by viewModel.state.collectAsState()

            SplashScreen(
                state = state,
                onEvent = { event ->
                    when (event) {
                        SplashViewModel.SplashEvent.NavigateToLogin -> {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }

                        else -> {return@SplashScreen}
                    }
                }
            )
        }

        composable(Screen.Login.route) { backStackEntry ->
            val viewModel = LoginViewModel()
            val state by viewModel.state.collectAsState()

            LoginScreen(
                state = state,
                onEvent = { event ->
                    when (event) {
                        is LoginViewModel.LoginEvent.Connect -> {
                            viewModel.handleEvent(event)
                        }
                        is LoginViewModel.LoginEvent.NavigateToDashboard -> {
                            // Navigate immediately when this event is received
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                        is LoginViewModel.LoginEvent.ResetMessages -> {
                            viewModel.handleEvent(event)
                        }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            val viewModel = DashboardViewModel()
            val state by viewModel.state.collectAsState()

            DashboardScreen(
                state = state,
                onEvent = { event ->
                    when (event) {
                        is DashboardViewModel.DashboardEvent.Refresh -> viewModel.handleEvent(event)
                        is DashboardViewModel.DashboardEvent.RefreshConnection -> viewModel.handleEvent(event)
                        is DashboardViewModel.DashboardEvent.StartBot -> viewModel.handleEvent(event)
                        is DashboardViewModel.DashboardEvent.StopBot -> viewModel.handleEvent(event)
                        is DashboardViewModel.DashboardEvent.ToggleAutoTrading -> viewModel.handleEvent(event)
                        is DashboardViewModel.DashboardEvent.SetAutoOpenEnabled -> viewModel.handleEvent(event)
                        is DashboardViewModel.DashboardEvent.SetAutoCloseEnabled -> viewModel.handleEvent(event)
                        is DashboardViewModel.DashboardEvent.ShowRiskDialog -> viewModel.handleEvent(event)
                        is DashboardViewModel.DashboardEvent.DismissRiskDialog -> viewModel.handleEvent(event)
                        is DashboardViewModel.DashboardEvent.SaveAutomationSettings -> viewModel.handleEvent(event)
                        is DashboardViewModel.DashboardEvent.AddFeedback -> viewModel.handleEvent(event)
                    }
                }
            )
        }
    }
}