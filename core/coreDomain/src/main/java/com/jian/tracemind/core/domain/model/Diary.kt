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
    val tags: List<String>
)
