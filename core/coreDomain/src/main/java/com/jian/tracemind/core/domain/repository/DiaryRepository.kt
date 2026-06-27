package com.jian.tracemind.core.domain.repository

import com.jian.tracemind.core.domain.model.Diary
import kotlinx.coroutines.flow.Flow

interface DiaryRepository {
    fun getDiariesByFolderId(folderId: String?): Flow<List<Diary>>
    fun getAllDiaries(): Flow<List<Diary>>
    fun searchDiaries(query: String): Flow<List<Diary>>
    fun getAllDiaryTimestamps(): Flow<List<Long>>
    fun getDiariesByMonthDay(monthDay: String): Flow<List<Diary>>
    suspend fun getDiaryById(id: String): Diary?
    suspend fun insertDiary(diary: Diary)
    suspend fun updateDiary(diary: Diary)
    suspend fun deleteDiary(diaryId: String)
}
