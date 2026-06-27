package com.jian.tracemind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jian.tracemind.navigation.AppRoute
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jian.tracemind.core.ui.components.LiquidBottomTab
import com.jian.tracemind.core.ui.components.LiquidBottomTabs
import com.jian.tracemind.feature.editor.ui.EditorScreen
import com.jian.tracemind.feature.folder.ui.FolderScreen
import com.jian.tracemind.feature.home.ui.HomeScreen
import com.jian.tracemind.feature.insights.ui.InsightsScreen
import com.jian.tracemind.feature.profile.ui.ProfileScreen
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
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (currentRoute != AppRoute.Editor.route) {
                            val bottomTabs = remember {
                                listOf(
                                    AppRoute.Home,
                                    AppRoute.Folder,
                                    AppRoute.Insights,
                                    AppRoute.Profile
                                )
                            }
                            val navState = navController.currentBackStackEntryAsState()
                            var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

                            LaunchedEffect(navState.value?.destination?.route) {
                                val route = navState.value?.destination?.route ?: AppRoute.Home.route
                                val index = bottomTabs.indexOfFirst { it.route == route }.takeIf { it >= 0 } ?: 0
                                if (selectedTabIndex != index) {
                                    selectedTabIndex = index
                                }
                            }

                            LiquidBottomTabs(
                                selectedTabIndex = { selectedTabIndex },
                                onTabSelected = { index ->
                                    val route = bottomTabs[index].route
                                    if (route != navState.value?.destination?.route) {
                                        navController.navigate(route) {
                                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                    if (selectedTabIndex != index) {
                                        selectedTabIndex = index
                                    }
                                },
                                backdrop = backdrop,
                                tabsCount = 4,
                                modifier = Modifier
                                    .padding(horizontal = 36.dp, vertical = 24.dp)
                            ) {
                                val contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
                                val labels = listOf("首页", "文件夹", "洞察", "个人")
                                val icons = listOf(
                                    Icons.Default.Home,
                                    Icons.Default.Folder,
                                    Icons.Default.AutoGraph,
                                    Icons.Default.Person
                                )

                                repeat(4) { index ->
                                    LiquidBottomTab(onClick = { 
                                        selectedTabIndex = index
                                    }) {
                                        val icon = icons[index]
                                        val label = labels[index]
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = label,
                                            tint = contentColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        BasicText(
                                            text = label,
                                            style = TextStyle(color = contentColor, fontSize = 10.sp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().layerBackdrop(backdrop)) {
                        NavHost(
                            navController = navController,
                            startDestination = AppRoute.Home.route,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            composable(AppRoute.Home.route) {
                                HomeScreen(
                                    innerPadding = innerPadding,
                                    onAddClick = { navController.navigate(AppRoute.Editor.createRoute()) },
                                    onDiaryClick = { id -> navController.navigate(AppRoute.Editor.createRoute(diaryId = id)) },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            composable(AppRoute.Folder.route) {
                                FolderScreen(innerPadding = innerPadding, modifier = Modifier.fillMaxSize())
                            }
                            composable(AppRoute.Insights.route) {
                                InsightsScreen(innerPadding = innerPadding, modifier = Modifier.fillMaxSize())
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
                    }
                }
            }
        }
    }
}