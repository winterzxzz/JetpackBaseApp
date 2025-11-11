package com.example.jetpackbaseapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.jetpackbaseapp.presentation.navigation.NavGraph
import com.example.jetpackbaseapp.ui.theme.JetpackBaseAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetpackBaseAppTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}