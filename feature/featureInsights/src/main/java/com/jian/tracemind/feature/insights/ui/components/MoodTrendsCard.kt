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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jian.tracemind.feature.insights.ui.InsightsMockData

@Composable
fun MoodTrendsCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        val maxVal = InsightsMockData.moodDataList.maxOfOrNull { it.value } ?: 1
        val chartHeight = 150.dp
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Bottom
        ) {
            InsightsMockData.moodDataList.forEach { moodItem ->
                val barHeightFrac = moodItem.value.toFloat() / maxVal
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.height(chartHeight)
                ) {
                    // Bar
                    Box(
                        modifier = Modifier
                            .width(30.dp)
                            .height(chartHeight * barHeightFrac)
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(Color(moodItem.color))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Emoji label
                    Text(text = moodItem.emoji, fontSize = 15.sp)
                }
            }
        }

        // Legend row
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            InsightsMockData.moodDataList.forEach { moodItem ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(moodItem.color))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = moodItem.mood, color = Color(0xFF6B7280), fontSize = 10.sp)
                }
            }
        }
    }
}
