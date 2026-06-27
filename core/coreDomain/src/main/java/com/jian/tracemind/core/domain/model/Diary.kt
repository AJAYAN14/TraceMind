package com.jian.tracemind.core.domain.model

data class Diary(
    val id: String,
    val folderId: String?, // 所属的文件夹 ID，若为 null 则在根目录
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val mood: String?,
    val weather: String?,
    val tags: List<String>,
    val coverImage: String? = null // 自动提取的第一张图片，用于瀑布流封面展示
)
