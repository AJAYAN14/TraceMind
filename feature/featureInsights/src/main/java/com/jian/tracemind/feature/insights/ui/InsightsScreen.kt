package com.jian.tracemind.feature.insights.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jian.tracemind.core.ui.components.LiquidAppBar
import com.jian.tracemind.core.ui.components.LiquidTextButton
import com.jian.tracemind.core.ui.components.SectionLabel
import com.jian.tracemind.feature.insights.ui.components.HeatmapCard
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.jian.tracemind.feature.insights.ui.components.MoodTrendsCard
import com.jian.tracemind.feature.insights.ui.components.TagCloudCard

@Composable
fun InsightsScreen(
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val localBackdrop = rememberLayerBackdrop()
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
            LiquidAppBar(
                title = {
                    Column {
                        Text(
                            text = "我的洞察",
                            color = Color(0xFF1A1C1E),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "共 ${uiState.totalDiaries} 篇，总字数 ${uiState.totalWords}",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                },
                actions = {
                    val date = java.text.SimpleDateFormat("yyyy年M月", java.util.Locale.getDefault()).format(java.util.Date())
                    LiquidTextButton(
                        onClick = { /* TODO: date picker */ },
                        backdrop = localBackdrop
                    ) {
                        Text(
                            text = date,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            )
            
            Box(Modifier.weight(1f).background(Color(0xFFF8F9FA)).layerBackdrop(localBackdrop)) {
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
}
