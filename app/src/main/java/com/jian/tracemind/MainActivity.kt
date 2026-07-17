package com.jian.tracemind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jian.tracemind.navigation.AppRoute
import com.jian.tracemind.navigation.AppTransitions
import com.jian.tracemind.navigation.MainBottomBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jian.tracemind.feature.editor.ui.EditorScreen
import com.jian.tracemind.feature.folder.ui.FolderScreen
import com.jian.tracemind.feature.home.ui.HomeScreen
import com.jian.tracemind.feature.insights.ui.InsightsScreen
import com.jian.tracemind.feature.profile.ui.ProfileScreen
import com.jian.tracemind.feature.search.ui.SearchScreen
import com.jian.tracemind.ui.theme.TraceMindTheme
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TraceMindTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: AppRoute.Home.route

                val backdrop = rememberLayerBackdrop()
                
                var showFluidBackground by remember { androidx.compose.runtime.mutableStateOf(true) }
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(5000)
                    showFluidBackground = false
                }
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        MainBottomBar(
                            navController = navController,
                            backdrop = backdrop,
                            currentRoute = currentRoute
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .layerBackdrop(backdrop)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = AppRoute.Home.route,
                            modifier = Modifier.fillMaxSize(),
                            enterTransition = AppTransitions.enterTransition,
                            exitTransition = AppTransitions.exitTransition,
                            popEnterTransition = AppTransitions.popEnterTransition,
                            popExitTransition = AppTransitions.popExitTransition
                        ) {
                            composable(AppRoute.Home.route) {
                                HomeScreen(
                                    innerPadding = innerPadding,
                                    onDiaryClick = { id -> navController.navigate(AppRoute.Editor.createRoute(diaryId = id)) },
                                    onFolderClick = { id -> navController.navigate(AppRoute.FolderDetail.createRoute(folderId = id)) },
                                    onSearchClick = { navController.navigate(AppRoute.Search.route) },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            composable(AppRoute.FolderList.route) {
                                com.jian.tracemind.feature.folder.ui.FolderListScreen(
                                    innerPadding = innerPadding,
                                    onNavigateToFolder = { id -> navController.navigate(AppRoute.FolderDetail.createRoute(folderId = id)) },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            composable(
                                route = AppRoute.FolderDetail.route,
                                arguments = listOf(
                                    navArgument("folderId") {
                                        type = NavType.StringType
                                        nullable = true
                                    }
                                )
                            ) {
                                FolderScreen(
                                    innerPadding = innerPadding, 
                                    onNavigateBack = { navController.popBackStack() },
                                    onNavigateToFolder = { id -> navController.navigate(AppRoute.FolderDetail.createRoute(folderId = id)) },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            composable(AppRoute.Insights.route) {
                                InsightsScreen(innerPadding = innerPadding, modifier = Modifier.fillMaxSize())
                            }
                            composable(AppRoute.Search.route) {
                                SearchScreen(
                                    innerPadding = innerPadding,
                                    onBack = { navController.popBackStack() },
                                    onDiaryClick = { id -> navController.navigate(AppRoute.Editor.createRoute(diaryId = id)) },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            composable(AppRoute.Profile.route) {
                                ProfileScreen(innerPadding = innerPadding, modifier = Modifier.fillMaxSize())
                            }
                            composable(
                                route = AppRoute.Editor.route,
                                arguments = listOf(
                                    navArgument("diaryId") {
                                        type = NavType.StringType
                                        nullable = true
                                        defaultValue = null
                                    },
                                    navArgument("folderId") {
                                        type = NavType.StringType
                                        nullable = true
                                        defaultValue = null
                                    }
                                )
                            ) {
                                EditorScreen(
                                    onBack = { navController.popBackStack() },
                                    modifier = Modifier.fillMaxSize(),
                                    innerPadding = innerPadding
                                )
                            }
                        }

                        val isMainScreen = currentRoute in listOf(
                            AppRoute.Home.route,
                            AppRoute.FolderList.route,
                            AppRoute.FolderDetail.route,
                            AppRoute.Insights.route,
                            AppRoute.Profile.route
                        )

                        androidx.compose.animation.AnimatedVisibility(
                            visible = isMainScreen && showFluidBackground,
                            modifier = Modifier.align(androidx.compose.ui.Alignment.BottomCenter),
                            enter = androidx.compose.animation.fadeIn(androidx.compose.animation.core.tween(500)),
                            exit = androidx.compose.animation.fadeOut(androidx.compose.animation.core.tween(500))
                        ) {
                            com.jian.tracemind.core.ui.components.FluidBottomBackground(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}