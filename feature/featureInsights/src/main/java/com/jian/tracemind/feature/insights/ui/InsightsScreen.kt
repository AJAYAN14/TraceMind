package com.jian.tracemind.feature.insights.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jian.tracemind.core.ui.components.SectionLabel
import com.jian.tracemind.feature.insights.ui.components.HeatmapCard
import com.jian.tracemind.feature.insights.ui.components.MoodTrendsCard
import com.jian.tracemind.feature.insights.ui.components.TagCloudCard

@Composable
fun InsightsScreen(
    innerPadding: PaddingValues, 
    modifier: Modifier = Modifier,
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
            InsightsTopBar(totalDiaries = uiState.totalDiaries, totalWords = uiState.totalWords)
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp, 
                    end = 20.dp, 
                    top = 8.dp, 
                    bottom = innerPadding.calculateBottomPadding() + 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Column {
                        SectionLabel("写作热力图")
                        HeatmapCard(heatmapData = uiState.heatmapData)
                    }
                }
                
                item {
                    Column {
                        SectionLabel("情绪趋势 · 历史")
                        MoodTrendsCard(moodDataList = uiState.moodDataList)
                    }
                }
                
                item {
                    Column {
                        SectionLabel("标签云")
                        TagCloudCard(tagCloudData = uiState.tagCloudData)
                    }
                }
            }
        }
    }
}

@Composable
fun InsightsTopBar(totalDiaries: Int, totalWords: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "我的洞察",
                color = Color(0xFF1A1C1E),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "共 $totalDiaries 篇，总字数 $totalWords",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
        
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            val date = java.text.SimpleDateFormat("yyyy年M月", java.util.Locale.getDefault()).format(java.util.Date())
            Text(
                text = date,
                color = Color(0xFF6B7280),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
