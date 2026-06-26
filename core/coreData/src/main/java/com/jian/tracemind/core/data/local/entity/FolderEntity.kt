package com.jian.tracemind.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jian.tracemind.core.domain.model.Folder

@Entity(tableName = "folders")
data class FolderEntity(
    @PrimaryKey
    val id: String,
    val parentId: String?,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long
)

fun FolderEntity.toDomainModel(): Folder {
    return Folder(
        id = id,
        parentId = parentId,
        name = name,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Folder.toEntity(): FolderEntity {
    return FolderEntity(
        id = id,
        parentId = parentId,
        name = name,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
