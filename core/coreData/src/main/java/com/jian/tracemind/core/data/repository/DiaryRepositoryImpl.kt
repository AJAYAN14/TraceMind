package com.jian.tracemind.core.data.repository

import com.jian.tracemind.core.data.local.dao.DiaryDao
import com.jian.tracemind.core.data.local.entity.toDomainModel
import com.jian.tracemind.core.data.local.entity.toEntity
import com.jian.tracemind.core.domain.model.Diary
import com.jian.tracemind.core.domain.repository.DiaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DiaryRepositoryImpl @Inject constructor(
    private val diaryDao: DiaryDao
) : DiaryRepository {

    override fun getDiariesByFolderId(folderId: String?): Flow<List<Diary>> {
        val entitiesFlow = if (folderId == null) {
            diaryDao.getDiariesInRootFolder()
        } else {
            diaryDao.getDiariesByFolderId(folderId)
        }
        return entitiesFlow.map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getAllDiaries(): Flow<List<Diary>> {
        return diaryDao.getAllDiaries().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun searchDiaries(query: String): Flow<List<Diary>> {
        return diaryDao.searchDiaries(query).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getAllDiaryTimestamps(): Flow<List<Long>> {
        return diaryDao.getAllDiaryTimestamps()
    }

    override fun getDiariesByMonthDay(monthDay: String): Flow<List<Diary>> {
        return diaryDao.getDiariesByMonthDay(monthDay).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getDiaryById(id: String): Diary? {
        return diaryDao.getDiaryById(id)?.toDomainModel()
    }

    override suspend fun insertDiary(diary: Diary) {
        diaryDao.insertDiary(diary.toEntity())
    }

    override suspend fun updateDiary(diary: Diary) {
        diaryDao.updateDiary(diary.toEntity())
    }

    override suspend fun deleteDiary(diaryId: String) {
        diaryDao.deleteDiary(diaryId)
    }
}
