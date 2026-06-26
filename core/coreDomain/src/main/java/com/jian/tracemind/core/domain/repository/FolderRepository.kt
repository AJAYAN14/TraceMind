package com.jian.tracemind.core.domain.repository

import com.jian.tracemind.core.domain.model.Folder
import kotlinx.coroutines.flow.Flow

interface FolderRepository {
    fun getAllFolders(): Flow<List<Folder>>
    suspend fun getFolderById(id: String): Folder?
    suspend fun insertFolder(folder: Folder)
    suspend fun updateFolder(folder: Folder)
    suspend fun deleteFolder(folderId: String)
}
