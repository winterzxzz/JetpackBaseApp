package com.example.jetpackbaseapp.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Optimization : BottomNavItem(
        route = "optimization",
        title = "Optimize",
        icon = Icons.Default.Settings
    )
}
