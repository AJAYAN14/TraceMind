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

data class MoodData(val emoji: String, val mood: String, val value: Int, val color: Long)
data class TagData(val label: String, val count: Int)

data class InsightsUiState(
    val heatmapData: Map<String, Int> = emptyMap(),
    val totalDiaries: Int = 0,
    val totalWords: Int = 0,
    val streakDays: Int = 0,
    val moodDataList: List<MoodData> = emptyList(),
    val tagCloudData: List<TagData> = emptyList(),
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
            diaryRepository.getAllDiaries().collect { diaries ->
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val heatmap = mutableMapOf<String, Int>()
                
                var totalWords = 0
                val moodCounts = mutableMapOf<String, Int>()
                val tagCounts = mutableMapOf<String, Int>()
                
                diaries.forEach { diary ->
                    val dateStr = formatter.format(Date(diary.createdAt))
                    heatmap[dateStr] = heatmap.getOrDefault(dateStr, 0) + 1
                    
                    totalWords += diary.content.length
                    
                    if (diary.mood != null) {
                        moodCounts[diary.mood!!] = moodCounts.getOrDefault(diary.mood!!, 0) + 1
                    }
                    
                    diary.tags.forEach { tag ->
                        tagCounts[tag] = tagCounts.getOrDefault(tag, 0) + 1
                    }
                }
                
                val moodColorPalette = listOf(0xFFB5C4F5, 0xFFA8D5BA, 0xFFFFD580, 0xFFF2B8A0, 0xFFC4B5F5)
                val moodDataList = moodCounts.entries
                    .sortedByDescending { it.value }
                    .take(5)
                    .mapIndexed { index, entry ->
                        val parts = entry.key.split(" ")
                        val emoji = if (parts.size > 1) parts[0] else ""
                        val text = if (parts.size > 1) parts[1] else entry.key
                        MoodData(
                            emoji = emoji,
                            mood = text,
                            value = entry.value,
                            color = moodColorPalette[index % moodColorPalette.size]
                        )
                    }
                    
                val tagCloudData = tagCounts.entries
                    .sortedByDescending { it.value }
                    .take(15)
                    .map { TagData(label = it.key, count = it.value) }
                
                _uiState.update { 
                    it.copy(
                        heatmapData = heatmap,
                        totalDiaries = diaries.size,
                        totalWords = totalWords,
                        moodDataList = moodDataList,
                        tagCloudData = tagCloudData,
                        isLoading = false
                    ) 
                }
            }
        }
    }
}
