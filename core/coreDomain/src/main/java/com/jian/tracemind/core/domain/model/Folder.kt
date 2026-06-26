package com.jian.tracemind.core.domain.model

data class Folder(
    val id: String,
    val parentId: String?, // 指向父文件夹的 ID，若为 null 则在根目录
    val name: String,
    val createdAt: Long,
    val updatedAt: Long
)
