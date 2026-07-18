package com.jian.tracemind.feature.editor.ui

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jian.tracemind.core.domain.model.Diary
import com.jian.tracemind.core.domain.repository.DiaryRepository
import com.jian.tracemind.feature.editor.ui.theme.NoteColorPalette
import com.jian.tracemind.feature.editor.utils.MediaHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val savedStateHandle: SavedStateHandle,
    private val application: Application
) : ViewModel() {

    private val _noteTitle = mutableStateOf(
        NoteTextFieldState(
            text = savedStateHandle.get<String>("title") ?: "",
            hint = "标题..."
        )
    )
    val noteTitle: State<NoteTextFieldState> = _noteTitle

    private val _noteContent = mutableStateOf(
        NoteTextFieldState(
            text = savedStateHandle.get<String>("content") ?: "",
            hint = "内容..."
        )
    )
    val noteContent: State<NoteTextFieldState> = _noteContent

    private val _noteColor = mutableIntStateOf(
        savedStateHandle.get<Int>("color") ?: NoteColorPalette.Light.first().toArgb()
    )
    val noteColor: State<Int> = _noteColor

    private val _noteTimestamp = mutableStateOf<Long?>(null)
    val noteTimestamp: State<Long?> = _noteTimestamp

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentDiaryId: String? = null
    private var folderId: String? = null
    private var autoSaveJob: Job? = null
    private var createdAt: Long = System.currentTimeMillis()

    init {
        val diaryId = savedStateHandle.get<String>("diaryId")
        folderId = savedStateHandle.get<String>("folderId")
        
        if (!diaryId.isNullOrEmpty()) {
            viewModelScope.launch {
                val diary = diaryRepository.getDiaryById(diaryId)
                if (diary != null) {
                    currentDiaryId = diary.id
                    folderId = diary.folderId
                    createdAt = diary.createdAt
                    _noteTimestamp.value = diary.updatedAt
                    
                    if (savedStateHandle.get<String>("title") == null) {
                        _noteTitle.value = noteTitle.value.copy(
                            text = diary.title,
                            isHintVisible = false
                        )
                        _noteContent.value = _noteContent.value.copy(
                            text = diary.content,
                            isHintVisible = false
                        )
                    }
                }
            }
        } else {
            currentDiaryId = UUID.randomUUID().toString()
        }
    }

    private fun triggerAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(1000L)
            saveNoteInternal()
        }
    }

    private suspend fun saveNoteInternal(): String? {
        val title = noteTitle.value.text
        val content = noteContent.value.text
        
        if (title.isBlank() && content.isBlank()) {
            return null
        }
        
        try {
            val isNew = diaryRepository.getDiaryById(currentDiaryId!!) == null
            val diary = Diary(
                id = currentDiaryId!!,
                folderId = folderId,
                title = title.ifBlank { "无标题" },
                content = content,
                createdAt = createdAt,
                updatedAt = System.currentTimeMillis(),
                mood = null,
                weather = null,
                tags = emptyList(),
                images = emptyList(),
                audioPath = null,
                coverImage = null
            )
            
            if (isNew) {
                diaryRepository.insertDiary(diary)
            } else {
                diaryRepository.updateDiary(diary)
            }
            return currentDiaryId
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun applyDefaultColor(isDarkTheme: Boolean) {
        if (_noteColor.intValue == NoteColorPalette.Light.first().toArgb()) {
            _noteColor.intValue = if (isDarkTheme) {
                NoteColorPalette.Dark.first().toArgb()
            } else {
                NoteColorPalette.Light.first().toArgb()
            }
        }
    }

    fun onEvent(event: EditorEvent) {
        when (event) {
            is EditorEvent.EnteredTitle -> {
                _noteTitle.value = noteTitle.value.copy(text = event.value)
                savedStateHandle["title"] = event.value
                triggerAutoSave()
            }
            is EditorEvent.ChangeTitleFocus -> {
                _noteTitle.value = noteTitle.value.copy(
                    isHintVisible = !event.focusState.isFocused && _noteTitle.value.text.isBlank()
                )
            }
            is EditorEvent.EnteredContent -> {
                _noteContent.value = noteContent.value.copy(text = event.value)
                savedStateHandle["content"] = event.value
                triggerAutoSave()
            }
            is EditorEvent.ChangeContentFocus -> {
                _noteContent.value = noteContent.value.copy(
                    isHintVisible = !event.focusState.isFocused && _noteContent.value.text.isBlank()
                )
            }
            is EditorEvent.ChangeColor -> {
                _noteColor.intValue = event.color
                savedStateHandle["color"] = event.color
            }
            is EditorEvent.InsertImage -> {
                viewModelScope.launch {
                    try {
                        val uri = Uri.parse(event.uriString)
                        val localPath = MediaHelper.copyImageToInternalStorage(application, uri)
                        if (localPath != null) {
                            _eventFlow.emit(UiEvent.ImageInserted(localPath))
                        } else {
                            _eventFlow.emit(UiEvent.ShowSnackbar("图片插入失败"))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _eventFlow.emit(UiEvent.ShowSnackbar("图片插入失败: ${e.message}"))
                    }
                }
            }
            is EditorEvent.SaveNote -> {
                autoSaveJob?.cancel()
                viewModelScope.launch {
                    if (noteTitle.value.text.isBlank() && noteContent.value.text.isBlank()) {
                        _eventFlow.emit(UiEvent.SavedNote)
                        return@launch
                    }
                    val resultId = saveNoteInternal()
                    if (resultId != null) {
                        _eventFlow.emit(UiEvent.SavedNote)
                    } else {
                        _eventFlow.emit(UiEvent.ShowSnackbar("保存失败"))
                    }
                }
            }
            is EditorEvent.SetReminder -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ShowSnackbar("不支持提醒功能"))
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SavedNote : UiEvent()
        data class ImageInserted(val path: String) : UiEvent()
    }
}
