package com.example.biasharax.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.biasharax.ui.screens.login.LoginScreen
import com.example.biasharax.ui.screens.register.RegisterScreen
import com.example.biasharax.ui.screens.dashboard.DashboardScreen

// ── ROUTES (Centralized) ─────────────────────────────
object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
}

// ── NAV GRAPH ────────────────────────────────────────
@Composable
fun AppNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN   // first screen user sees
    ) {

        // 🔐 Login
        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }

        // 📝 Register
        composable(Routes.REGISTER) {
            RegisterScreen(navController)
        }

        // 📊 Dashboard
        composable(Routes.DASHBOARD) {
            DashboardScreen(navController)
        }
    }
}