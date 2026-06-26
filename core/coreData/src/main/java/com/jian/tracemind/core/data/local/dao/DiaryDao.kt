package com.jian.tracemind.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jian.tracemind.core.data.local.entity.DiaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diaries")
    fun getAllDiaries(): Flow<List<DiaryEntity>>

    @Query("SELECT * FROM diaries WHERE folderId = :folderId")
    fun getDiariesByFolderId(folderId: String): Flow<List<DiaryEntity>>
    
    @Query("SELECT * FROM diaries WHERE folderId IS NULL")
    fun getDiariesInRootFolder(): Flow<List<DiaryEntity>>

    @Query("SELECT * FROM diaries WHERE id = :id")
    suspend fun getDiaryById(id: String): DiaryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiary(diary: DiaryEntity)

    @Update
    suspend fun updateDiary(diary: DiaryEntity)

    @Query("DELETE FROM diaries WHERE id = :id")
    suspend fun deleteDiary(id: String)
}
