package com.upitracker.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.upitracker.presentation.screens.HomeScreen
import com.upitracker.presentation.screens.TransactionDetailScreen
import com.upitracker.presentation.screens.CategoryScreen
import com.upitracker.presentation.screens.ReportsScreen
import com.upitracker.presentation.theme.UpiTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            UpiTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {
                            HomeScreen(navController = navController)
                        }
                        composable("transaction/{transactionId}") { backStackEntry ->
                            val transactionId = backStackEntry.arguments?.getString("transactionId")?.toLongOrNull() ?: 0L
                            TransactionDetailScreen(
                                transactionId = transactionId,
                                navController = navController
                            )
                        }
                        composable("categories") {
                            CategoryScreen(navController = navController)
                        }
                        composable("reports") {
                            ReportsScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}