package com.jian.tracemind.feature.folder.ui

data class FolderCardData(
    val id: Int,
    val title: String,
    val date: String,
    val imgH: Int,
    val img: String
)

object FolderMockData {
    val chips = listOf("全部", "#语法", "#词汇", "#文化", "#汉字", "#口语")

    val folderCards = listOf(
        FolderCardData(1, "平假名练习", "6月21日", 110, "https://images.unsplash.com/photo-1528360983277-13d401cdc186?w=180&h=110&fit=crop&auto=format"),
        FolderCardData(2, "第一次突破汉字关", "6月14日", 170, "https://images.unsplash.com/photo-1480796927426-f609979314bd?w=180&h=170&fit=crop&auto=format"),
        FolderCardData(3, "与由纪的对话", "6月8日", 140, "https://images.unsplash.com/photo-1460627390041-532a28402358?w=180&h=140&fit=crop&auto=format"),
        FolderCardData(4, "播客笔记 #12", "6月3日", 120, "https://images.unsplash.com/photo-1478720568477-152d9b164e26?w=180&h=120&fit=crop&auto=format"),
        FolderCardData(5, "语法深度解析", "5月28日", 100, "https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=180&h=100&fit=crop&auto=format"),
        FolderCardData(6, "东京之旅规划", "5月22日", 155, "https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?w=180&h=155&fit=crop&auto=format")
    )
}
