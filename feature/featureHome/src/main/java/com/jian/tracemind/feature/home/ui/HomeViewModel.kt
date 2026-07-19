package com.jian.tracemind.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jian.tracemind.core.domain.model.Diary
import com.jian.tracemind.core.domain.model.Folder
import com.jian.tracemind.core.domain.repository.DiaryRepository
import com.jian.tracemind.core.domain.repository.FolderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
    val isLoading: Boolean = true,
    // Dialog states
    val renameFolderId: String? = null,
    val renameFolderName: String = "",
    val deleteFolderId: String? = null,
    val deleteFolderName: String = "",
    val deleteDiaryId: String? = null
)

sealed class HomeEvent {
    data class OnRenameFolderClick(val folderId: String, val folderName: String) : HomeEvent()
    data class OnRenameFolderNameChange(val newName: String) : HomeEvent()
    object OnRenameFolderConfirm : HomeEvent()
    object OnRenameFolderDismiss : HomeEvent()

    data class OnDeleteFolderClick(val folderId: String, val folderName: String) : HomeEvent()
    object OnDeleteFolderConfirm : HomeEvent()
    object OnDeleteFolderDismiss : HomeEvent()

    data class OnDeleteDiaryClick(val diaryId: String) : HomeEvent()
    object OnDeleteDiaryConfirm : HomeEvent()
    object OnDeleteDiaryDismiss : HomeEvent()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val folderRepository: FolderRepository
) : ViewModel() {

    private val _dialogState = MutableStateFlow(HomeUiState(isLoading = false))

    val uiState: StateFlow<HomeUiState> = combine(
        folderRepository.getAllFolders(),
        diaryRepository.getAllDiaries(),
        diaryRepository.getDiariesByMonthDay(SimpleDateFormat("MM-dd", Locale.getDefault()).format(Date())),
        _dialogState
    ) { folders, allDiaries, onThisDay, dialogState ->
        val folderUiModels = folders.map { folder ->
            FolderUiModel(folder, allDiaries.count { it.folderId == folder.id })
        }

        val recentMemories = allDiaries.sortedByDescending { it.createdAt }.take(5)

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val cal = Calendar.getInstance()
        val pastDiaries = onThisDay.filter { diary ->
            cal.timeInMillis = diary.createdAt
            cal.get(Calendar.YEAR) < currentYear
        }

        dialogState.copy(
            folders = folderUiModels,
            recentMemories = recentMemories,
            onThisDayDiaries = pastDiaries,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnRenameFolderClick -> {
                _dialogState.update { it.copy(renameFolderId = event.folderId, renameFolderName = event.folderName) }
            }
            is HomeEvent.OnRenameFolderNameChange -> {
                _dialogState.update { it.copy(renameFolderName = event.newName) }
            }
            is HomeEvent.OnRenameFolderConfirm -> {
                val state = _dialogState.value
                if (state.renameFolderId != null && state.renameFolderName.isNotBlank()) {
                    renameFolder(state.renameFolderId, state.renameFolderName)
                }
                _dialogState.update { it.copy(renameFolderId = null, renameFolderName = "") }
            }
            is HomeEvent.OnRenameFolderDismiss -> {
                _dialogState.update { it.copy(renameFolderId = null, renameFolderName = "") }
            }
            is HomeEvent.OnDeleteFolderClick -> {
                _dialogState.update { it.copy(deleteFolderId = event.folderId, deleteFolderName = event.folderName) }
            }
            is HomeEvent.OnDeleteFolderConfirm -> {
                val state = _dialogState.value
                if (state.deleteFolderId != null) {
                    deleteFolder(state.deleteFolderId)
                }
                _dialogState.update { it.copy(deleteFolderId = null, deleteFolderName = "") }
            }
            is HomeEvent.OnDeleteFolderDismiss -> {
                _dialogState.update { it.copy(deleteFolderId = null, deleteFolderName = "") }
            }
            is HomeEvent.OnDeleteDiaryClick -> {
                _dialogState.update { it.copy(deleteDiaryId = event.diaryId) }
            }
            is HomeEvent.OnDeleteDiaryConfirm -> {
                val state = _dialogState.value
                if (state.deleteDiaryId != null) {
                    deleteDiary(state.deleteDiaryId)
                }
                _dialogState.update { it.copy(deleteDiaryId = null) }
            }
            is HomeEvent.OnDeleteDiaryDismiss -> {
                _dialogState.update { it.copy(deleteDiaryId = null) }
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

    private fun renameFolder(folderId: String, newName: String) {
        viewModelScope.launch {
            val folder = folderRepository.getFolderById(folderId)
            if (folder != null) {
                folderRepository.updateFolder(folder.copy(name = newName, updatedAt = System.currentTimeMillis()))
            }
        }
    }

    private fun deleteFolder(folderId: String) {
        viewModelScope.launch {
            val allFolders = folderRepository.getAllFolders().first()
            val folderIdsToDelete = mutableSetOf<String>()
            
            fun collectDescendants(currentId: String) {
                folderIdsToDelete.add(currentId)
                allFolders.filter { it.parentId == currentId }.forEach { child ->
                    collectDescendants(child.id)
                }
            }
            collectDescendants(folderId)

            val allDiaries = diaryRepository.getAllDiaries().first()
            val diariesToDelete = allDiaries.filter { it.folderId in folderIdsToDelete }

            diariesToDelete.forEach { diary ->
                diaryRepository.deleteDiary(diary.id)
            }

            folderIdsToDelete.forEach { id ->
                folderRepository.deleteFolder(id)
            }
        }
    }

    private fun deleteDiary(diaryId: String) {
        viewModelScope.launch {
            diaryRepository.deleteDiary(diaryId)
        }
    }
}
