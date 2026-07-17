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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

import coil.compose.AsyncImage
import com.jian.tracemind.core.domain.model.Diary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar

@Composable
fun OnThisDayCard(diary: Diary, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    val formatter = SimpleDateFormat("yyyy年M月d日", Locale.getDefault())
    val dateStr = formatter.format(Date(diary.createdAt))
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val cal = Calendar.getInstance().apply { timeInMillis = diary.createdAt }
    val yearsAgo = currentYear - cal.get(Calendar.YEAR)
    val yearsAgoText = if (yearsAgo > 0) "${yearsAgo}年前" else "今天"
    androidx.compose.material3.Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        modifier = modifier.fillMaxWidth().traceShadow(borderRadius = 24.dp)
    ) {
        Column {
        Box {
            if (diary.coverImage != null) {
                AsyncImage(
                    model = diary.coverImage,
                    contentDescription = "On This Day Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(156.dp)
                        .background(Color(0xFFF3F4F6)),
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
                    color = Color(0xFF9CA3AF),
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            if (diary.title.isNotBlank()) {
                Text(
                    text = diary.title,
                    color = Color(0xFF1A1C1E),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = diary.content,
                color = Color(0xFF6B7280),
                fontSize = 13.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
        }
    }
    }
}
