package com.jian.tracemind.feature.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import com.jian.tracemind.core.ui.extensions.traceShadow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
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
    androidx.compose.material3.Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.size(96.dp).traceShadow(borderRadius = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = { expanded = true }
                )
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(text = "📁", fontSize = 20.sp) // Default icon for now
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = model.folder.name,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${model.diaryCount} 篇",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
}
