package com.example.dragorderkmp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dragorderkmp.android.ui.DragOrderScreen
import com.example.dragorderkmp.android.ui.PersonDetailScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = "home") {
                composable("home") { DragOrderScreen(navController) }
                composable("detail/{id}") { backStack ->
                    PersonDetailScreen(
                        orderId = backStack.arguments?.getString("id")!!
                    )
                }
            }
            }
    }
}
