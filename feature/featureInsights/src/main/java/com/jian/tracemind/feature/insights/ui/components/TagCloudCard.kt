package com.jian.tracemind.feature.insights.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jian.tracemind.feature.insights.ui.TagData

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagCloudCard(tagCloudData: List<TagData>, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(androidx.compose.material3.MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (tagCloudData.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "暂无标签数据",
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                return@FlowRow
            }

            tagCloudData.forEach { tag ->
                val fontSize = when {
                    tag.count > 30 -> 14.sp
                    tag.count > 20 -> 13.sp
                    tag.count > 12 -> 12.sp
                    else -> 11.sp
                }
                val fontWeight = when {
                    tag.count > 30 -> FontWeight.Bold
                    tag.count > 15 -> FontWeight.SemiBold
                    else -> FontWeight.Medium
                }

                Row(
                    modifier = Modifier
                        .border(1.dp, androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tag.label,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                        fontSize = fontSize,
                        fontWeight = fontWeight
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tag.count.toString(),
                            color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
