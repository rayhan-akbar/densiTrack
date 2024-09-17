package com.example.densitrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.densitrack.ui.screens.DetailsPage
import com.example.densitrack.ui.screens.HomePage
import com.example.densitrack.ui.screens.LoadingPage
import com.example.densitrack.ui.screens.MapPage
import com.example.densitrack.ui.theme.DensiTrackTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DensiTrackTheme {
                val navController = rememberNavController()
                var isLoading by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    delay(3000)
                    isLoading = false
                }

                if (isLoading) {
                    LoadingPage()
                } else {
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") { HomePage(navController) }
                        composable(
                            "details/{busStop}/{direction}",
                            arguments = listOf(
                                navArgument("busStop") { type = NavType.StringType },
                                navArgument("direction") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val busStop = backStackEntry.arguments?.getString("busStop") ?: "Unknown"
                            val direction = backStackEntry.arguments?.getString("direction") ?: "Unknown"
                            DetailsPage(navController, busStop, direction)
                        }
                        composable("map") { MapPage() }
                    }
                }
            }
        }
    }
}
