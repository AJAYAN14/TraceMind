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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HeatmapCard(heatmapData: Map<String, Int>, modifier: Modifier = Modifier) {
    // Generate a simple layout for the last N weeks
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val cal = Calendar.getInstance()
    cal.time = Date()
    // Move to most recent Saturday (or Sunday) as end
    
    // For simplicity, we just convert the Map into the format InsightsMockData uses,
    // or just rely on a simple fixed grid.
    val weeks = 20
    val grid = mutableListOf<List<Int>>()
    
    // We will build the grid from past to present
    val startCal = Calendar.getInstance()
    startCal.add(Calendar.WEEK_OF_YEAR, -weeks + 1)
    startCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    
    for (w in 0 until weeks) {
        val weekCol = mutableListOf<Int>()
        for (d in 0..6) {
            val dateStr = formatter.format(startCal.time)
            val count = heatmapData[dateStr] ?: 0
            val intensity = when {
                count == 0 -> 0
                count == 1 -> 1
                count == 2 -> 2
                count == 3 -> 3
                else -> 4
            }
            weekCol.add(intensity)
            startCal.add(Calendar.DAY_OF_YEAR, 1)
        }
        grid.add(weekCol)
    }

    val heatmapAlpha = listOf(0.05f, 0.3f, 0.5f, 0.7f, 1.0f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(androidx.compose.material3.MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // Month Labels
        Row(
            modifier = Modifier
                .padding(start = 20.dp, bottom = 4.dp)
                .fillMaxWidth()
        ) {
            // Simplified labels
        }

        // Days Grid
        val dayLabels = listOf("日", "一", "二", "三", "四", "五", "六")
        dayLabels.forEachIndexed { dayIndex, dayLabel ->
            Row(
                modifier = Modifier.padding(bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Day Label
                Text(
                    text = if (dayIndex % 2 == 0) dayLabel else "",
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 8.sp,
                    modifier = Modifier.width(18.dp),
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.width(4.dp))
                
                // Blocks for this day across all weeks
                grid.forEach { weekData ->
                    val intensity = weekData.getOrElse(dayIndex) { 0 }
                    val alpha = heatmapAlpha.getOrElse(intensity) { 0f }
                    Box(
                        modifier = Modifier
                            .padding(end = 2.dp)
                            .size(11.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = alpha))
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
            Text(text = "少", color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 8.sp)
            Spacer(modifier = Modifier.width(4.dp))
            heatmapAlpha.forEach { alpha ->
                Box(
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(10.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = alpha))
                )
            }
            Text(text = "多", color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 8.sp)
        }
    }
}
