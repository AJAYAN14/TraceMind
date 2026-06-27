package com.jian.tracemind.navigation

sealed class AppRoute(val route: String) {
    object Home : AppRoute("home")
    object Editor : AppRoute("editor?diaryId={diaryId}&folderId={folderId}") {
        fun createRoute(diaryId: String? = null, folderId: String? = null): String {
            val dId = diaryId ?: ""
            val fId = folderId ?: ""
            return "editor?diaryId=$dId&folderId=$fId"
        }
    }
    object Folder : AppRoute("folder?folderId={folderId}") {
        fun createRoute(folderId: String? = null): String {
            val fId = folderId ?: ""
            return "folder?folderId=$fId"
        }
    }
    object Insights : AppRoute("insights")
    object Profile : AppRoute("profile")
}
