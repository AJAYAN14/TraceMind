package com.jian.tracemind.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
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
    AnimatedVisibility(
        visible = currentRoute != AppRoute.Editor.route && currentRoute != AppRoute.Search.route,
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
