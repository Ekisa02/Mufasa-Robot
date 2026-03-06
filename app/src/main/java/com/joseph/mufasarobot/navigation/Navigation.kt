package com.joseph.mufasarobot.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.joseph.mufasarobot.data.local.TokenManagerImpl
import com.joseph.mufasarobot.screens.AppLoginScreen
import com.joseph.mufasarobot.screens.AppLoginViewModel
import com.joseph.mufasarobot.screens.DashboardScreen
import com.joseph.mufasarobot.screens.DashboardViewModel
import com.joseph.mufasarobot.screens.SplashScreen
import com.joseph.mufasarobot.screens.SplashViewModel
import com.joseph.mufasarobot.screens.login.MT5LoginScreen
import com.joseph.mufasarobot.screens.login.MT5LoginViewModel


sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object AppLogin : Screen("app_login")
    data object Mt5Login : Screen("mt5_login")
    data object Dashboard : Screen("dashboard")
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            val viewModel = SplashViewModel()
            val state by viewModel.state.collectAsState()

            SplashScreen(
                state = state,
                onEvent = { event ->
                    when (event) {
                        SplashViewModel.SplashEvent.NavigateToLogin -> {
                            navController.navigate(Screen.AppLogin.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }

                        else -> {return@SplashScreen}
                    }
                }
            )
        }

        // App Login Screen (Email/Password)
        composable(Screen.AppLogin.route) {
            val context = LocalContext.current
            val tokenManager = TokenManagerImpl(context)
            val viewModel = AppLoginViewModel(tokenManager)
            val state by viewModel.state.collectAsState()

            AppLoginScreen(
                state = state,
                onEvent = { event ->
                    viewModel.handleEvent(event)
                }
            )

            LaunchedEffect(state.isAuthenticated) {
                if (state.isAuthenticated) {
                    navController.navigate(Screen.Mt5Login.route) {
                        popUpTo(Screen.AppLogin.route) { inclusive = true }
                    }
                }
            }
        }

        // MT5 Login Screen
        composable(Screen.Mt5Login.route) {
            val context = LocalContext.current
            val tokenManager = TokenManagerImpl(context)
            val viewModel = MT5LoginViewModel(tokenManager)
            val state by viewModel.state.collectAsState()

            MT5LoginScreen(
                state = state,
                onEvent = { event ->
                    viewModel.handleEvent(event)
                }
            )

            LaunchedEffect(state.isConnected) {
                if (state.isConnected) {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Mt5Login.route) { inclusive = true }
                    }
                }
            }
        }

        // Dashboard
        composable(Screen.Dashboard.route) {
            val viewModel = DashboardViewModel()
            val state by viewModel.state.collectAsState()

            DashboardScreen(
                state = state,
                onEvent = viewModel::handleEvent
            )
        }
    }
}