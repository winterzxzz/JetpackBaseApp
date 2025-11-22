package com.example.jetpackbaseapp.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold {
        BottomNavGraph(
            navController = navController,
            modifier = Modifier.padding(it)
        )
    }
}
