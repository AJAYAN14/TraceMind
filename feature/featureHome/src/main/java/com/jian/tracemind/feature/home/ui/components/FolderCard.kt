package com.jian.tracemind.feature.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jian.tracemind.core.domain.model.Folder

@Composable
fun FolderCard(folder: Folder, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .width(84.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(text = "📁", fontSize = 20.sp) // Default icon for now
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = folder.name,
            color = Color(0xFF1A1C1E),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "0 篇", // Need to query diary count for folder if we want this, or omit it
            color = Color(0xFF9CA3AF),
            fontSize = 10.sp
        )
    }
}
