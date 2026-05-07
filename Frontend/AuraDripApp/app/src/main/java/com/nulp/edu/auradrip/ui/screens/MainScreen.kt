package com.nulp.edu.auradrip.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nulp.edu.auradrip.ui.navigation.BottomNavigationBar
import com.nulp.edu.auradrip.ui.navigation.BottomNavItem
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nulp.edu.auradrip.AuraDripApplication
import com.nulp.edu.auradrip.ui.viewmodel.AnalyticsViewModel

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Dashboard.route
            ) {
                composable(BottomNavItem.Dashboard.route) {
                    DashboardScreen(navController = navController)
                }
                composable(BottomNavItem.Analytics.route) {
                    // Отримуємо посилання на репозиторій через Application context
                    val context = LocalContext.current
                    val application = context.applicationContext as AuraDripApplication

                    // Створюємо ViewModel за допомогою твоєї фабрики
                    val analyticsViewModel: AnalyticsViewModel = viewModel(
                        factory = AnalyticsViewModel.provideFactory(application.plantRepository)
                    )

                    // Передаємо створену ViewModel в екран
                    AnalyticsScreen(viewModel = analyticsViewModel)
                }
                composable(BottomNavItem.Settings.route) {
                    SettingsScreen(navController = navController)
                }
                composable(
                    route = "plant_config/{plantId}",
                    arguments = listOf(navArgument("plantId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val plantId = backStackEntry.arguments?.getInt("plantId") ?: 1
                    PlantConfigScreen(navController = navController, plantId = plantId)
                }
            }
        }
    }
}
