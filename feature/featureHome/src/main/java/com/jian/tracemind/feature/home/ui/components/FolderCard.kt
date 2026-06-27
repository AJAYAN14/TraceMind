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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jian.tracemind.feature.home.ui.FolderUiModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FolderCard(
    model: FolderUiModel,
    onClick: () -> Unit,
    onRenameClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .width(84.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .combinedClickable(
                onClick = onClick,
                onLongClick = { expanded = true }
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(text = "📁", fontSize = 20.sp) // Default icon for now
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = model.folder.name,
            color = Color(0xFF1A1C1E),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "${model.diaryCount} 篇",
            color = Color(0xFF9CA3AF),
            fontSize = 10.sp
        )
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("重命名") },
                onClick = { 
                    expanded = false
                    onRenameClick()
                }
            )
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
