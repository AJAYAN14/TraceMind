package com.jian.tracemind.feature.insights.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jian.tracemind.feature.insights.ui.InsightsMockData

@Composable
fun HeatmapCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Month Labels
        Row(
            modifier = Modifier
                .padding(start = 20.dp, bottom = 4.dp)
                .fillMaxWidth()
        ) {
            InsightsMockData.heatmapData.indices.forEach { weekIndex ->
                Box(modifier = Modifier.width(13.dp)) {
                    val monthLabel = InsightsMockData.monthAtWeek[weekIndex]
                    if (monthLabel != null) {
                        Text(
                            text = monthLabel,
                            color = Color(0xFF9CA3AF),
                            fontSize = 8.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Days Grid
        InsightsMockData.dayLabels.forEachIndexed { dayIndex, dayLabel ->
            Row(
                modifier = Modifier.padding(bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Day Label
                Text(
                    text = if (dayIndex % 2 == 0) dayLabel else "",
                    color = Color(0xFF9CA3AF),
                    fontSize = 8.sp,
                    modifier = Modifier.width(18.dp),
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.width(4.dp))
                
                // Blocks for this day across all weeks
                InsightsMockData.heatmapData.forEach { weekData ->
                    val intensity = weekData.getOrElse(dayIndex) { 0 }
                    val alpha = InsightsMockData.heatmapAlpha.getOrElse(intensity) { 0f }
                    Box(
                        modifier = Modifier
                            .padding(end = 2.dp)
                            .size(11.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFF5552E4).copy(alpha = alpha))
                    )
                }
            }
        }

        // Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "少", color = Color(0xFF9CA3AF), fontSize = 8.sp)
            Spacer(modifier = Modifier.width(4.dp))
            InsightsMockData.heatmapAlpha.forEach { alpha ->
                Box(
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(10.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF5552E4).copy(alpha = alpha))
                )
            }
            Text(text = "多", color = Color(0xFF9CA3AF), fontSize = 8.sp)
        }
    }
}
