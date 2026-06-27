package com.jian.tracemind.feature.folder.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jian.tracemind.core.domain.model.Diary
import com.jian.tracemind.core.domain.model.Folder
import com.jian.tracemind.core.domain.repository.DiaryRepository
import com.jian.tracemind.core.domain.repository.FolderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FolderUiState(
    val folderId: String? = null,
    val currentFolder: Folder? = null,
    val diaries: List<Diary> = emptyList(),
    val folders: List<Folder> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val activeChip: String = "全部"
)

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val folderRepository: FolderRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val folderId: String? = savedStateHandle.get<String>("folderId")

    private val _uiState = MutableStateFlow(FolderUiState(folderId = folderId, isLoading = true))
    val uiState: StateFlow<FolderUiState> = _uiState.asStateFlow()

    private val searchQueryFlow = MutableStateFlow("")

    init {
        // Observe search query or folder changes to load diaries
        viewModelScope.launch {
            searchQueryFlow.flatMapLatest { query ->
                _uiState.update { it.copy(isLoading = true, searchQuery = query) }
                if (query.isBlank()) {
                    diaryRepository.getDiariesByFolderId(folderId)
                } else {
                    // Note: If folder specific search is needed, we should adjust repository query.
                    // For now, searchDiaries is global, but we can filter it locally if needed,
                    // or just show global search results. We'll filter locally to match folder:
                    diaryRepository.searchDiaries(query).map { list ->
                        if (folderId != null) list.filter { it.folderId == folderId } else list.filter { it.folderId == null }
                    }
                }
            }.collect { diaries ->
                _uiState.update { it.copy(diaries = diaries, isLoading = false) }
            }
        }

        viewModelScope.launch {
            folderRepository.getAllFolders().collect { folders ->
                _uiState.update { it.copy(folders = folders) }
            }
        }
        
        if (folderId != null) {
            viewModelScope.launch {
                val folder = folderRepository.getFolderById(folderId)
                _uiState.update { it.copy(currentFolder = folder) }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQueryFlow.value = query
    }

    fun onChipChanged(chip: String) {
        _uiState.update { it.copy(activeChip = chip) }
    }

    fun deleteDiary(diaryId: String) {
        viewModelScope.launch {
            diaryRepository.deleteDiary(diaryId)
        }
    }

    fun moveDiary(diaryId: String, targetFolderId: String?) {
        viewModelScope.launch {
            val diary = diaryRepository.getDiaryById(diaryId)
            if (diary != null) {
                diaryRepository.updateDiary(diary.copy(folderId = targetFolderId))
            }
        }
    }
}
