package com.example.jetpackbaseapp.presentation.navigation

sealed class Screen(val route: String) {
    object Optimization : Screen("optimization")
    object RecursionDemo : Screen("recursion_demo")
    object CacheDemo : Screen("cache_demo")
    object DataTypeDemo : Screen("datatype_demo")
}
