package com.jian.tracemind.navigation

sealed class AppRoute(val route: String) {
    object Home : AppRoute("home")
    object Editor : AppRoute("editor")
    object Folder : AppRoute("folder")
    object Insights : AppRoute("insights")
    object Profile : AppRoute("profile")
}
