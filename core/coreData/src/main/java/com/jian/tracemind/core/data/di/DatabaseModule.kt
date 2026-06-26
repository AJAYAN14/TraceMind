package com.jian.tracemind.core.data.di

import android.content.Context
import androidx.room.Room
import com.jian.tracemind.core.data.local.TraceMindDatabase
import com.jian.tracemind.core.data.local.dao.DiaryDao
import com.jian.tracemind.core.data.local.dao.FolderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTraceMindDatabase(
        @ApplicationContext context: Context
    ): TraceMindDatabase {
        return Room.databaseBuilder(
            context,
            TraceMindDatabase::class.java,
            "tracemind.db"
        ).build()
    }

    @Provides
    fun provideFolderDao(database: TraceMindDatabase): FolderDao {
        return database.folderDao
    }

    @Provides
    fun provideDiaryDao(database: TraceMindDatabase): DiaryDao {
        return database.diaryDao
    }
}
