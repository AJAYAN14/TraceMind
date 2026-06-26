package com.jian.tracemind.core.domain.model

data class DiaryAnalysis(
    val diaryId: String,
    val summary: String,
    val sentiment: String,
    val keywords: List<String>
)
