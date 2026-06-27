package com.jian.tracemind.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jian.tracemind.core.domain.model.Diary

@Entity(tableName = "diaries")
data class DiaryEntity(
    @PrimaryKey
    val id: String,
    val folderId: String?,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val mood: String?,
    val weather: String?,
    val tags: String, // stored as comma-separated or JSON string since Room doesn't support List out of the box without TypeConverter
    val images: String = "", // comma-separated URIs
    val audioPath: String? = null,
    val coverImage: String? = null
)

fun DiaryEntity.toDomainModel(): Diary {
    return Diary(
        id = id,
        folderId = folderId,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        mood = mood,
        weather = weather,
        tags = if (tags.isBlank()) emptyList() else tags.split(","),
        images = if (images.isBlank()) emptyList() else images.split(","),
        audioPath = audioPath,
        coverImage = coverImage
    )
}

fun Diary.toEntity(): DiaryEntity {
    return DiaryEntity(
        id = id,
        folderId = folderId,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        mood = mood,
        weather = weather,
        tags = tags.joinToString(","),
        images = images.joinToString(","),
        audioPath = audioPath,
        coverImage = coverImage
    )
}
