package com.jian.tracemind.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.AutoGraph
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.jian.tracemind.R
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.draw.paint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.jian.tracemind.core.ui.components.LiquidBottomTab
import com.jian.tracemind.core.ui.components.LiquidBottomTabs
import com.kyant.backdrop.Backdrop

@Composable
fun MainBottomBar(
    navController: NavController,
    backdrop: Backdrop,
    currentRoute: String?
) {
    val showTabs = currentRoute in listOf(
        AppRoute.Home.route,
        AppRoute.FolderList.route,
        AppRoute.Insights.route,
        AppRoute.Profile.route
    )
    val showFAB = showTabs || currentRoute == AppRoute.FolderDetail.route

    AnimatedVisibility(
        visible = showFAB,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(300)
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(300)
        )
    ) {
        val bottomTabs = remember {
            listOf(
                AppRoute.Home,
                AppRoute.FolderList,
                AppRoute.Insights,
                AppRoute.Profile
            )
        }
        val navState = navController.currentBackStackEntryAsState()
        var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

        LaunchedEffect(navState.value?.destination?.route) {
            val route = navState.value?.destination?.route ?: AppRoute.Home.route
            val index = bottomTabs.indexOfFirst { it.route == route }
            if (index >= 0 && selectedTabIndex != index) {
                selectedTabIndex = index
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 24.dp, end = 20.dp, top = 32.dp, bottom = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = showTabs,
                    enter = androidx.compose.animation.fadeIn(tween(300)),
                    exit = androidx.compose.animation.fadeOut(tween(300))
                ) {
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
                        tabsCount = 4
                    ) {
                        val contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
                        val iconColorFilter = androidx.compose.ui.graphics.ColorFilter.tint(contentColor)
                        val labels = listOf("首页", "文件夹", "洞察", "个人")
                        val selectedIcons = listOf(
                            ImageVector.vectorResource(id = R.drawable.ic_home_rounded_filled),
                            Icons.Rounded.Folder,
                            Icons.Rounded.AutoGraph,
                            Icons.Rounded.Person
                        )
                        val unselectedIcons = listOf(
                            ImageVector.vectorResource(id = R.drawable.ic_home_rounded_outlined),
                            Icons.Outlined.Folder,
                            Icons.Outlined.AutoGraph,
                            Icons.Outlined.Person
                        )
    
                        repeat(4) { index ->
                            LiquidBottomTab(onClick = { 
                                if (selectedTabIndex == index) {
                                    val route = bottomTabs[index].route
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                                selectedTabIndex = index
                            }) {
                                val icon = if (selectedTabIndex == index) selectedIcons[index] else unselectedIcons[index]
                                val label = labels[index]
                                val painter = androidx.compose.ui.graphics.vector.rememberVectorPainter(icon)
                                
                                Box(
                                    Modifier
                                        .size(28.dp)
                                        .paint(painter, colorFilter = iconColorFilter)
                                )
                                BasicText(
                                    text = label,
                                    style = TextStyle(color = contentColor, fontSize = 12.sp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            GlobalAddButton(
                backdrop = backdrop,
                onNavigateToEditor = { navController.navigate(AppRoute.Editor.createRoute()) }
            )
        }
    }
}
