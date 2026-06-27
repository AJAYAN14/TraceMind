package com.jian.tracemind.feature.insights.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jian.tracemind.core.domain.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class InsightsUiState(
    val heatmapData: Map<String, Int> = emptyMap(),
    val totalDiaries: Int = 0,
    val totalWords: Int = 0,
    val streakDays: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    init {
        loadInsightsData()
    }

    private fun loadInsightsData() {
        viewModelScope.launch {
            diaryRepository.getAllDiaryTimestamps().collect { timestamps ->
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val heatmap = mutableMapOf<String, Int>()
                
                timestamps.forEach { ts ->
                    val dateStr = formatter.format(Date(ts))
                    heatmap[dateStr] = heatmap.getOrDefault(dateStr, 0) + 1
                }
                
                _uiState.update { 
                    it.copy(
                        heatmapData = heatmap,
                        totalDiaries = timestamps.size,
                        // Note: For total words, we'd need to fetch actual diary contents.
                        // Or add a new DAO method. Keeping mock value for now if needed, or 0.
                        isLoading = false
                    ) 
                }
            }
        }
    }
}
