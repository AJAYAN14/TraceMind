package com.jian.tracemind.core.data.repository

import com.jian.tracemind.core.data.local.dao.FolderDao
import com.jian.tracemind.core.data.local.entity.toDomainModel
import com.jian.tracemind.core.data.local.entity.toEntity
import com.jian.tracemind.core.domain.model.Folder
import com.jian.tracemind.core.domain.repository.FolderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FolderRepositoryImpl @Inject constructor(
    private val folderDao: FolderDao
) : FolderRepository {

    override fun getAllFolders(): Flow<List<Folder>> {
        return folderDao.getAllFolders().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getFolderById(id: String): Folder? {
        return folderDao.getFolderById(id)?.toDomainModel()
    }

    override suspend fun insertFolder(folder: Folder) {
        folderDao.insertFolder(folder.toEntity())
    }

    override suspend fun updateFolder(folder: Folder) {
        folderDao.updateFolder(folder.toEntity())
    }

    override suspend fun deleteFolder(folderId: String) {
        folderDao.deleteFolder(folderId)
    }
}
