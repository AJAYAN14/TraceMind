package com.jian.tracemind.core.data.di

import com.jian.tracemind.core.data.repository.DiaryRepositoryImpl
import com.jian.tracemind.core.data.repository.FolderRepositoryImpl
import com.jian.tracemind.core.domain.repository.DiaryRepository
import com.jian.tracemind.core.domain.repository.FolderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFolderRepository(
        folderRepositoryImpl: FolderRepositoryImpl
    ): FolderRepository

    @Binds
    @Singleton
    abstract fun bindDiaryRepository(
        diaryRepositoryImpl: DiaryRepositoryImpl
    ): DiaryRepository
}
