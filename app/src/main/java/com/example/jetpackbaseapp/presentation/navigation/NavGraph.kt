package com.example.jetpackbaseapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.jetpackbaseapp.presentation.screens.detail.DetailScreen
import com.example.jetpackbaseapp.presentation.screens.home.HomeScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                onPostClick = { postId ->
                    navController.navigate(Screen.Detail.createRoute(postId))
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("postId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getInt("postId") ?: 0
            DetailScreen(
                postId = postId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
