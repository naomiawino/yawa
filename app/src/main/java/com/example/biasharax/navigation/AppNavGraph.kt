package com.example.biasharax.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.biasharax.ui.screens.login.LoginScreen
import com.example.biasharax.ui.screens.register.RegisterScreen
import com.example.biasharax.ui.screens.dashboard.DashboardScreen
import com.example.biasharax.ui.screens.payment.PaymentScreen
import com.example.biasharax.ui.screens.addproduct.AddProductScreen

// ── ROUTES (Centralized) ─────────────────────────────
object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val PAYMENT = "payment"
    const val ADD_PRODUCT = "add_product"
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

        // 💳 Payment
        composable(Routes.PAYMENT) {
            PaymentScreen(navController)
        }

        // ➕ Add Product (Admin)
        composable(Routes.ADD_PRODUCT) {
            AddProductScreen(navController)
        }
    }
}