package com.example.jetpackbaseapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.jetpackbaseapp.presentation.screens.optimization.OptimizationMenuScreen
import com.example.jetpackbaseapp.presentation.screens.optimization.advanced.AdvancedOptimizationScreen
import com.example.jetpackbaseapp.presentation.screens.optimization.cache.CacheOptimizationScreen
import com.example.jetpackbaseapp.presentation.screens.optimization.datatype.DataTypeOptimizationScreen
import com.example.jetpackbaseapp.presentation.screens.optimization.recursion.RecursionOptimizationScreen

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "optimization",
        modifier = modifier
    ) {
        composable(route = "optimization") {
            OptimizationMenuScreen(
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        // Optimization Demo Screens
        composable(route = "recursion_demo") {
            RecursionOptimizationScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(route = "cache_demo") {
            CacheOptimizationScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(route = "datatype_demo") {
            DataTypeOptimizationScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(route = "advanced_demo") {
            AdvancedOptimizationScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
