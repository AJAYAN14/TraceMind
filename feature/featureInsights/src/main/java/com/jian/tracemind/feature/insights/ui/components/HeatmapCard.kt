package com.jian.tracemind.feature.insights.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HeatmapCard(heatmapData: Map<String, Int>, modifier: Modifier = Modifier) {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    val weeks = 20
    val grid = mutableListOf<List<Int>>()
    val monthLabels = mutableListOf<Pair<Int, String>>()
    
    val startCal = Calendar.getInstance()
    startCal.add(Calendar.WEEK_OF_YEAR, -weeks + 1)
    startCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    
    var lastMonth = -1
    
    for (w in 0 until weeks) {
        val weekCol = mutableListOf<Int>()
        var weekMonth = -1
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
            
            val currentMonth = startCal.get(Calendar.MONTH)
            if (d == 0) {
                weekMonth = currentMonth
            } else if (currentMonth != weekMonth && startCal.get(Calendar.DAY_OF_MONTH) == 1) {
                weekMonth = currentMonth
            }
            startCal.add(Calendar.DAY_OF_YEAR, 1)
        }
        grid.add(weekCol)
        if (weekMonth != lastMonth) {
            monthLabels.add(Pair(w, "${weekMonth + 1}月"))
            lastMonth = weekMonth
        }
    }

    val heatmapAlpha = listOf(0.05f, 0.3f, 0.5f, 0.7f, 1.0f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // Month Labels
        Box(
            modifier = Modifier
                .padding(start = 22.dp, bottom = 6.dp)
                .fillMaxWidth()
        ) {
            monthLabels.forEach { (weekIndex, label) ->
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(start = (weekIndex * 13).dp)
                )
            }
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp,
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
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
                    )
                }
            }
        }

        // Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "少", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
            Spacer(modifier = Modifier.width(6.dp))
            heatmapAlpha.forEach { alpha ->
                Box(
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(12.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
                )
            }
            Spacer(modifier = Modifier.width(2.dp))
            Text(text = "多", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
        }
    }
}
