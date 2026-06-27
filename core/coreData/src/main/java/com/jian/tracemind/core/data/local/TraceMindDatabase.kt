package com.jian.tracemind.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jian.tracemind.core.data.local.dao.DiaryDao
import com.jian.tracemind.core.data.local.dao.FolderDao
import com.jian.tracemind.core.data.local.entity.DiaryEntity
import com.jian.tracemind.core.data.local.entity.FolderEntity

@Database(
    entities = [FolderEntity::class, DiaryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class TraceMindDatabase : RoomDatabase() {
    abstract val folderDao: FolderDao
    abstract val diaryDao: DiaryDao
}
