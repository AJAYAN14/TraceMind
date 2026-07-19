package com.jian.tracemind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
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
import com.jian.tracemind.navigation.BottomNavTransition
import com.jian.tracemind.navigation.HorizontalNavTransition
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
                        val systemBarsPadding = androidx.compose.foundation.layout.WindowInsets.systemBars.asPaddingValues()
                        val currentBottomPadding = innerPadding.calculateBottomPadding()
                        
                        var frozenBottomPadding by remember { androidx.compose.runtime.mutableStateOf(currentBottomPadding) }
                        val isBottomBarVisible = currentRoute in listOf(
                            AppRoute.Home.route,
                            AppRoute.FolderList.route,
                            AppRoute.FolderDetail.route,
                            AppRoute.Insights.route,
                            AppRoute.Profile.route
                        )
                        
                        if (isBottomBarVisible) {
                            frozenBottomPadding = currentBottomPadding
                        }
                        
                        val layoutDir = androidx.compose.ui.platform.LocalLayoutDirection.current
                        val stableMainPadding = PaddingValues(
                            top = innerPadding.calculateTopPadding(),
                            bottom = frozenBottomPadding,
                            start = innerPadding.calculateLeftPadding(layoutDir),
                            end = innerPadding.calculateRightPadding(layoutDir)
                        )
                        
                        val subScreenPadding = PaddingValues(
                            top = innerPadding.calculateTopPadding(),
                            bottom = systemBarsPadding.calculateBottomPadding(),
                            start = innerPadding.calculateLeftPadding(layoutDir),
                            end = innerPadding.calculateRightPadding(layoutDir)
                        )

                        val mainScreenRoutes = remember {
                            setOf(
                                AppRoute.Home.route,
                                AppRoute.FolderList.route,
                                AppRoute.Insights.route,
                                AppRoute.Profile.route
                            )
                        }

                        val mainEnterTransition: androidx.compose.animation.AnimatedContentTransitionScope<androidx.navigation.NavBackStackEntry>.() -> androidx.compose.animation.EnterTransition? = {
                            val initialRoute = initialState.destination.route
                            val targetRoute = targetState.destination.route
                            HorizontalNavTransition.enterTransition(initialRoute, targetRoute) ?:
                            if (initialRoute !in mainScreenRoutes) AppTransitions.popEnterTransition()
                            else BottomNavTransition.enterTransition()
                        }

                        val mainExitTransition: androidx.compose.animation.AnimatedContentTransitionScope<androidx.navigation.NavBackStackEntry>.() -> androidx.compose.animation.ExitTransition? = {
                            val initialRoute = initialState.destination.route
                            val targetRoute = targetState.destination.route
                            HorizontalNavTransition.exitTransition(initialRoute, targetRoute) ?:
                            if (targetRoute !in mainScreenRoutes) AppTransitions.exitTransition()
                            else BottomNavTransition.exitTransition()
                        }

                        val mainPopEnterTransition: androidx.compose.animation.AnimatedContentTransitionScope<androidx.navigation.NavBackStackEntry>.() -> androidx.compose.animation.EnterTransition? = {
                            val initialRoute = initialState.destination.route
                            val targetRoute = targetState.destination.route
                            HorizontalNavTransition.enterTransition(initialRoute, targetRoute) ?:
                            if (initialRoute !in mainScreenRoutes) AppTransitions.popEnterTransition()
                            else BottomNavTransition.enterTransition()
                        }

                        val mainPopExitTransition: androidx.compose.animation.AnimatedContentTransitionScope<androidx.navigation.NavBackStackEntry>.() -> androidx.compose.animation.ExitTransition? = {
                            val initialRoute = initialState.destination.route
                            val targetRoute = targetState.destination.route
                            HorizontalNavTransition.exitTransition(initialRoute, targetRoute) ?:
                            BottomNavTransition.exitTransition()
                        }

                        NavHost(
                            navController = navController,
                            startDestination = AppRoute.Home.route,
                            modifier = Modifier.fillMaxSize(),
                            enterTransition = { AppTransitions.enterTransition() },
                            exitTransition = { AppTransitions.exitTransition() },
                            popEnterTransition = { AppTransitions.popEnterTransition() },
                            popExitTransition = { AppTransitions.popExitTransition() }
                        ) {
                            composable(
                                route = AppRoute.Home.route,
                                enterTransition = mainEnterTransition,
                                exitTransition = mainExitTransition,
                                popEnterTransition = mainPopEnterTransition,
                                popExitTransition = mainPopExitTransition
                            ) {
                                HomeScreen(
                                    innerPadding = stableMainPadding,
                                    onDiaryClick = { id -> navController.navigate(AppRoute.Editor.createRoute(diaryId = id)) },
                                    onFolderClick = { id -> navController.navigate(AppRoute.FolderDetail.createRoute(folderId = id)) },
                                    onSearchClick = { navController.navigate(AppRoute.Search.route) },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            composable(
                                route = AppRoute.FolderList.route,
                                enterTransition = mainEnterTransition,
                                exitTransition = mainExitTransition,
                                popEnterTransition = mainPopEnterTransition,
                                popExitTransition = mainPopExitTransition
                            ) {
                                com.jian.tracemind.feature.folder.ui.FolderListScreen(
                                    innerPadding = stableMainPadding,
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
                                    innerPadding = stableMainPadding, 
                                    onNavigateBack = { navController.popBackStack() },
                                    onNavigateToFolder = { id -> navController.navigate(AppRoute.FolderDetail.createRoute(folderId = id)) },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            composable(
                                route = AppRoute.Insights.route,
                                enterTransition = mainEnterTransition,
                                exitTransition = mainExitTransition,
                                popEnterTransition = mainPopEnterTransition,
                                popExitTransition = mainPopExitTransition
                            ) {
                                InsightsScreen(innerPadding = stableMainPadding, modifier = Modifier.fillMaxSize())
                            }
                            composable(AppRoute.Search.route) {
                                SearchScreen(
                                    innerPadding = subScreenPadding,
                                    onBack = { navController.popBackStack() },
                                    onDiaryClick = { id -> navController.navigate(AppRoute.Editor.createRoute(diaryId = id)) },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            composable(
                                route = AppRoute.Profile.route,
                                enterTransition = mainEnterTransition,
                                exitTransition = mainExitTransition,
                                popEnterTransition = mainPopEnterTransition,
                                popExitTransition = mainPopExitTransition
                            ) {
                                ProfileScreen(innerPadding = stableMainPadding, modifier = Modifier.fillMaxSize())
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
                                    innerPadding = subScreenPadding
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