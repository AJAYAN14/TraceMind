package com.jian.tracemind.feature.editor.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jian.tracemind.core.domain.model.Diary
import com.jian.tracemind.core.domain.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class EditorUiState(
    val diaryId: String = "",
    val folderId: String? = null,
    val title: String = "",
    val content: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val mood: String? = null,
    val weather: String? = null,
    val tags: List<String> = emptyList(),
    val coverImage: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val diaryRepository: DiaryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    init {
        val diaryId = savedStateHandle.get<String>("diaryId")
        val folderId = savedStateHandle.get<String>("folderId")
        
        if (!diaryId.isNullOrEmpty()) {
            loadDiary(diaryId)
        } else {
            _uiState.update { 
                it.copy(
                    diaryId = UUID.randomUUID().toString(),
                    folderId = if (folderId.isNullOrEmpty()) null else folderId,
                    createdAt = System.currentTimeMillis()
                ) 
            }
        }
    }

    private fun loadDiary(id: String) {
        viewModelScope.launch {
            val diary = diaryRepository.getDiaryById(id)
            if (diary != null) {
                _uiState.update {
                    it.copy(
                        diaryId = diary.id,
                        folderId = diary.folderId,
                        title = diary.title,
                        content = diary.content,
                        createdAt = diary.createdAt,
                        mood = diary.mood,
                        weather = diary.weather,
                        tags = diary.tags,
                        coverImage = diary.coverImage
                    )
                }
            }
        }
    }

    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun onContentChange(content: String) {
        _uiState.update { it.copy(content = content) }
    }

    fun saveDiary() {
        val currentState = _uiState.value
        // Basic validation: skip saving if both title and content are blank
        if (currentState.title.isBlank() && currentState.content.isBlank()) {
            _uiState.update { it.copy(saveSuccess = true) }
            return
        }
        
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val isNew = diaryRepository.getDiaryById(currentState.diaryId) == null
            
            val diary = Diary(
                id = currentState.diaryId,
                folderId = currentState.folderId,
                title = currentState.title.ifBlank { "无标题" },
                content = currentState.content,
                createdAt = currentState.createdAt,
                updatedAt = System.currentTimeMillis(),
                mood = currentState.mood,
                weather = currentState.weather,
                tags = currentState.tags,
                coverImage = currentState.coverImage
            )
            
            if (isNew) {
                diaryRepository.insertDiary(diary)
            } else {
                diaryRepository.updateDiary(diary)
            }
            
            _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
        }
    }
}
