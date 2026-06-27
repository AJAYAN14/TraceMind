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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar
import javax.inject.Inject

data class HomeUiState(
    val folders: List<Folder> = emptyList(),
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
            folderRepository.getAllFolders().collect { folders ->
                _uiState.update { it.copy(folders = folders) }
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
}
