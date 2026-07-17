package com.jian.tracemind.feature.folder.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jian.tracemind.core.domain.model.Folder
import com.jian.tracemind.core.domain.repository.FolderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FolderListUiState(
    val folders: List<Folder> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class FolderListViewModel @Inject constructor(
    private val folderRepository: FolderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FolderListUiState(isLoading = true))
    val uiState: StateFlow<FolderListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            folderRepository.getAllFolders().collect { folders ->
                _uiState.update { it.copy(folders = folders, isLoading = false) }
            }
        }
    }
}
