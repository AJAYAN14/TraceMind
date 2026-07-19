package com.jian.tracemind.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import com.jian.tracemind.core.ui.extensions.traceShadow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import coil.compose.AsyncImage
import com.jian.tracemind.core.domain.model.Diary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MemoryCard(diary: Diary, onClick: () -> Unit = {}, onDeleteClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    var expanded by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    val formatter = SimpleDateFormat("M月d日", Locale.getDefault())
    val dateStr = formatter.format(Date(diary.createdAt))
    androidx.compose.material3.Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxWidth().traceShadow(borderRadius = 24.dp)
            .clip(RoundedCornerShape(24.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = { expanded = true }
            )
    ) {
        androidx.compose.foundation.layout.Box {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = diary.title.ifBlank { diary.content.take(20) },
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = diary.content,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = dateStr, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                
                val mood = diary.mood
                if (mood != null) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = mood, fontSize = 12.sp)
                }
                
                val weather = diary.weather
                if (weather != null) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = weather,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        if (diary.coverImage != null) {
            AsyncImage(
                model = diary.coverImage,
                contentDescription = diary.title,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )
        }
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("删除", color = Color.Red) },
                onClick = { 
                    expanded = false
                    onDeleteClick()
                }
            )
        }
        }
    }
}
