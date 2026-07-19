package com.jian.tracemind.feature.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import com.jian.tracemind.core.ui.extensions.traceShadow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import coil.compose.AsyncImage

import coil.compose.AsyncImage
import com.jian.tracemind.core.domain.model.Diary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnThisDayCard(diary: Diary, onClick: () -> Unit = {}, onDeleteClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    var expanded by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    val formatter = SimpleDateFormat("yyyy年M月d日", Locale.getDefault())
    val dateStr = formatter.format(Date(diary.createdAt))
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val cal = Calendar.getInstance().apply { timeInMillis = diary.createdAt }
    val yearsAgo = currentYear - cal.get(Calendar.YEAR)
    val yearsAgoText = if (yearsAgo > 0) "${yearsAgo}年前" else "今天"
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
        Column {
        Box {
            if (diary.coverImage != null) {
                AsyncImage(
                    model = diary.coverImage,
                    contentDescription = "On This Day Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(156.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
            }
            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xEE5552E4))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = yearsAgoText,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val subtitle = buildString {
                    append(dateStr)
                    if (diary.mood != null) append(" · ${diary.mood}")
                    if (diary.weather != null) append(" · ${diary.weather}")
                }
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            if (diary.title.isNotBlank()) {
                Text(
                    text = diary.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = diary.content,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
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
