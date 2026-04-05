package com.nulp.edu.auradrip.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.nulp.edu.auradrip.R

sealed class BottomNavItem(val titleResId: Int, val icon: ImageVector, val route: String) {
    data object Dashboard : BottomNavItem(R.string.dashboard, Icons.Filled.Home, "dashboard")
    data object Analytics : BottomNavItem(R.string.analytics, Icons.Filled.Assessment, "analytics")
    data object Settings : BottomNavItem(R.string.settings, Icons.Filled.Settings, "settings")
}
