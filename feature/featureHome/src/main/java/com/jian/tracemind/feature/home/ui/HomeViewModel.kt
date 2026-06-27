package com.jian.tracemind.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jian.tracemind.core.domain.model.Diary
import com.jian.tracemind.core.domain.model.Folder
import com.jian.tracemind.core.domain.repository.DiaryRepository
import com.jian.tracemind.core.domain.repository.FolderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar
import javax.inject.Inject

data class FolderUiModel(val folder: Folder, val diaryCount: Int)

data class HomeUiState(
    val folders: List<FolderUiModel> = emptyList(),
    val onThisDayDiaries: List<Diary> = emptyList(),
    val recentMemories: List<Diary> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val folderRepository: FolderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            kotlinx.coroutines.flow.combine(
                folderRepository.getAllFolders(),
                diaryRepository.getAllDiaries()
            ) { folders, diaries ->
                folders.map { folder ->
                    val count = diaries.count { it.folderId == folder.id }
                    FolderUiModel(folder, count)
                }
            }.collect { folderUiModels ->
                _uiState.update { it.copy(folders = folderUiModels) }
            }
        }

        viewModelScope.launch {
            diaryRepository.getAllDiaries().collect { diaries ->
                // Sort by date descending for recent memories
                val recent = diaries.sortedByDescending { it.createdAt }.take(5)
                _uiState.update { it.copy(recentMemories = recent) }
            }
        }

        viewModelScope.launch {
            val formatter = SimpleDateFormat("MM-dd", Locale.getDefault())
            val todayStr = formatter.format(Date())
            diaryRepository.getDiariesByMonthDay(todayStr).collect { onThisDay ->
                // Filter out diaries from the current year to ensure they are from "previous years"
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                val cal = Calendar.getInstance()
                
                val pastDiaries = onThisDay.filter { diary ->
                    cal.timeInMillis = diary.createdAt
                    cal.get(Calendar.YEAR) < currentYear
                }
                
                _uiState.update { it.copy(onThisDayDiaries = pastDiaries) }
            }
        }
    }

    fun createFolder(name: String) {
        viewModelScope.launch {
            val newFolder = Folder(
                id = java.util.UUID.randomUUID().toString(),
                parentId = null,
                name = name,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            folderRepository.insertFolder(newFolder)
        }
    }

    fun renameFolder(folderId: String, newName: String) {
        viewModelScope.launch {
            val folder = folderRepository.getFolderById(folderId)
            if (folder != null) {
                folderRepository.updateFolder(folder.copy(name = newName, updatedAt = System.currentTimeMillis()))
            }
        }
    }

    fun deleteFolder(folderId: String) {
        viewModelScope.launch {
            // First, get all diaries in this folder and unbind them
            // Since there's no single function to fetch them directly without Flow in repository,
            // we will collect the flow once.
            // Wait, DiaryRepository doesn't have a direct suspend fun to get all by folder id.
            // Let's get all diaries and filter manually for simplicity, as this is local DB.
            val allDiariesFlow = diaryRepository.getAllDiaries()
            // We can collect once
            val diariesToUpdate = allDiariesFlow.first().filter { it.folderId == folderId }
            diariesToUpdate.forEach { diary ->
                diaryRepository.updateDiary(diary.copy(folderId = null, updatedAt = System.currentTimeMillis()))
            }
            // Then delete the folder
            folderRepository.deleteFolder(folderId)
        }
    }
}
