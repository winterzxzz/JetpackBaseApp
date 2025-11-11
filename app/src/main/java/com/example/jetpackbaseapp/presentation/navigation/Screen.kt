package com.example.jetpackbaseapp.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Detail : Screen("detail/{postId}") {
        fun createRoute(postId: Int) = "detail/$postId"
    }
    object UserList : Screen("users")
    object UserDetail : Screen("user/{userId}") {
        fun createRoute(userId: Int) = "user/$userId"
    }
}
