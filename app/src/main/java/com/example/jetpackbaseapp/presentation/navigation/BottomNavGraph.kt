package com.example.jetpackbaseapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.jetpackbaseapp.presentation.screens.detail.DetailScreen
import com.example.jetpackbaseapp.presentation.screens.home.HomeScreen
import com.example.jetpackbaseapp.presentation.screens.users.UsersScreen

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Posts.route,
        modifier = modifier
    ) {
        composable(route = BottomNavItem.Posts.route) {
            HomeScreen(
                onPostClick = { postId ->
                    navController.navigate(Screen.Detail.createRoute(postId))
                }
            )
        }

        composable(route = BottomNavItem.Users.route) {
            UsersScreen(
                onUserClick = { userId ->
                    // Navigate to user detail if needed
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
