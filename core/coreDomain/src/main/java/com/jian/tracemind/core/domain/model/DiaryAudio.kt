package com.jian.tracemind.core.domain.model

data class DiaryAudio(
    val id: String,
    val diaryId: String,
    val filePath: String,
    val duration: Long
)
