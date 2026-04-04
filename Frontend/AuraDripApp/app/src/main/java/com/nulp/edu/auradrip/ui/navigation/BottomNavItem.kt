package com.nulp.edu.auradrip.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(var title: String, var icon: ImageVector, var route: String) {
    data object Dashboard : BottomNavItem("Dashboard", Icons.Filled.Home, "dashboard")
    data object Analytics : BottomNavItem("Analytics", Icons.Filled.Assessment, "analytics")
    data object Settings : BottomNavItem("Settings", Icons.Filled.Settings, "settings")
}
