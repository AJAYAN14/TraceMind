package com.jian.tracemind.core.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE diaries ADD COLUMN images TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE diaries ADD COLUMN audioPath TEXT")
            }
        }
        
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE diaries ADD COLUMN location TEXT")
            }
        }
        
        return Room.databaseBuilder(
            context,
            TraceMindDatabase::class.java,
            "tracemind.db"
        )
        .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
        .build()
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
