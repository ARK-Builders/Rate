package dev.arkbuilders.rate.watchapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.arkbuilders.rate.watchapp.presentation.addquickpairs.AddQuickPairsScreen
import dev.arkbuilders.rate.watchapp.presentation.options.OptionsScreen
import dev.arkbuilders.rate.watchapp.presentation.options.SuccessScreen
import dev.arkbuilders.rate.watchapp.presentation.quickpairs.QuickPairsScreen
import dev.arkbuilders.rate.watchapp.presentation.search.SearchScreen
import dev.arkbuilders.rate.watchapp.presentation.theme.ArkrateTheme
import androidx.navigation.NavType
import androidx.navigation.navArgument

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            ArkrateTheme {
                val navController = rememberSwipeDismissableNavController()
                Scaffold(
                    vignette = {
                        Vignette(vignettePosition = VignettePosition.TopAndBottom)
                    }
                ) {
                    SwipeDismissableNavHost(
                        navController = navController,
                        startDestination = "list"
                    ) {
                        composable("list") {
                            QuickPairsScreen(
                                onNavigateToAdd = {
                                    navController.navigate("addquickpairs")
                                },
                                onNavigateToOptions = { id ->
                                    navController.navigate("options/$id")
                                }
                            )
                        }
                        composable(
                            route = "addquickpairs?id={id}",
                            arguments = listOf(navArgument("id") { 
                                type = NavType.StringType 
                                nullable = true
                                defaultValue = null
                            })
                        ) {
                            AddQuickPairsScreen(
                                navController = navController,
                                onNavigateToSearch = { field -> 
                                    navController.navigate("search/$field") 
                                },
                                onNavigateBack = { 
                                    navController.popBackStack("list", inclusive = false) 
                                },
                                onNavigateToSuccess = { message ->
                                    navController.navigate("success?message=$message")
                                }
                            )
                        }
                        composable(
                            route = "options/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.LongType })
                        ) {
                            OptionsScreen(
                                onDeleteSuccess = { message -> 
                                    navController.navigate("success?message=$message") 
                                },
                                onUpdateClick = { id -> 
                                    navController.navigate("addquickpairs?id=$id") 
                                },
                                onPinClick = { message -> 
                                    navController.navigate("success?message=$message") 
                                },
                                onDismiss = { navController.popBackStack() }
                            )
                        }
                        composable(
                            route = "search/{field}",
                            arguments = listOf(navArgument("field") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val field = backStackEntry.arguments?.getString("field")
                            SearchScreen(
                                onCurrencyClick = { code ->
                                    navController.previousBackStackEntry?.savedStateHandle?.set("selected_currency", code)
                                    navController.previousBackStackEntry?.savedStateHandle?.set("target_field", field)
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("search") {
                            SearchScreen(
                                onCurrencyClick = { code ->
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable(
                            route = "success?message={message}",
                            arguments = listOf(navArgument("message") { 
                                type = NavType.StringType 
                                nullable = true
                                defaultValue = "Success"
                            })
                        ) { backStackEntry ->
                            val message = backStackEntry.arguments?.getString("message") ?: "Success"
                            SuccessScreen(
                                message = message,
                                onTimeout = {
                                    navController.popBackStack("list", inclusive = false)
                                }
                            )
                        }
                    }
                }
            }

        }
    }
}
