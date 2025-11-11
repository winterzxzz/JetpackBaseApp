package com.example.jetpackbaseapp.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Posts : BottomNavItem(
        route = "posts",
        title = "Posts",
        icon = Icons.Default.Home
    )
    
    object Users : BottomNavItem(
        route = "users",
        title = "Users",
        icon = Icons.Default.Person
    )
}
