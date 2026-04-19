package com.nulp.edu.auradrip.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nulp.edu.auradrip.ui.navigation.BottomNavigationBar
import com.nulp.edu.auradrip.ui.navigation.BottomNavItem

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
                    DashboardScreen()
                }
                composable(BottomNavItem.Analytics.route) {
                    AnalyticsScreen()
                }
                composable(BottomNavItem.Settings.route) {
                    SettingsScreen()
                }
            }
        }
    }
}
