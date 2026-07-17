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
    object FolderList : AppRoute("folder_list")
    object FolderDetail : AppRoute("folder_detail?folderId={folderId}") {
        fun createRoute(folderId: String? = null): String {
            val fId = folderId ?: ""
            return "folder_detail?folderId=$fId"
        }
    }
    object Search : AppRoute("search")
    object Insights : AppRoute("insights")
    object Profile : AppRoute("profile")
}
