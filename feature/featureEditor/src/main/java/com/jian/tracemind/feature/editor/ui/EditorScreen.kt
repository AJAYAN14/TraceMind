package com.jian.tracemind.feature.editor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EditorScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues = PaddingValues(),
    viewModel: EditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onBack()
        }
    }

    val formatter = remember { SimpleDateFormat("EEEE，M月d日", Locale.CHINESE) }
    val dateStr = formatter.format(Date(uiState.createdAt))

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .imePadding()
    ) {
        // App Bar
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        EditorTopBar(onBack = onBack, dateStr = dateStr, onSave = { viewModel.saveDiary() })

        // Metadata chips
        MetadataChipsRow()

        // Writing canvas
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            BasicTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                textStyle = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E)
                ),
                cursorBrush = SolidColor(Color(0xFF1A1C1E)),
                decorationBox = { innerTextField ->
                    if (uiState.title.isEmpty()) {
                        Text(
                            text = "日记标题...",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC8CACC)
                        )
                    }
                    innerTextField()
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Body paragraphs
            BasicTextField(
                value = uiState.content,
                onValueChange = viewModel::onContentChange,
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = Color(0xFF1A1C1E),
                    lineHeight = 24.sp
                ),
                cursorBrush = SolidColor(Color(0xFF1A1C1E)),
                decorationBox = { innerTextField ->
                    if (uiState.content.isEmpty()) {
                        Text(
                            text = "开始记录...",
                            fontSize = 14.sp,
                            color = Color(0xFFC8CACC),
                            lineHeight = 24.sp
                        )
                    }
                    innerTextField()
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Image block
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF3F4F6))
            ) {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1528360983277-13d401cdc186?w=350&h=210&fit=crop&auto=format",
                    contentDescription = "Kyoto bamboo grove",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Audio block
            AudioBlock()

            // Continuation text
            Text(
                text = "我按下录音键，就那样说了一会儿。有时候，你需要听见自己的声音，才能明白自己真正在想什么。",
                fontSize = 14.sp,
                color = Color(0xFF1A1C1E),
                lineHeight = 24.sp
            )
        }

        // Bottom toolbar
        EditorBottomToolbar(bottomPadding = innerPadding.calculateBottomPadding())
    }
}

@Composable
private fun EditorTopBar(onBack: () -> Unit, dateStr: String, onSave: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF1A1C1E),
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = dateStr,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1C1E)
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF1A1C1E))
                .clickable { onSave() }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "保存",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun MetadataChipsRow() {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(EditorMockData.chips.size) { index ->
            val chip = EditorMockData.chips[index]
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (chip.on) Color(0xFF1A1C1E) else Color.White)
                    .border(
                        width = 1.dp,
                        color = if (chip.on) Color(0xFF1A1C1E) else Color(0xFFE5E7EB),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = chip.label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (chip.on) Color.White else Color(0xFF6B7280)
                )
            }
        }
    }
}

@Composable
private fun AudioBlock() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFF1A1C1E)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "语音备忘 · 2:14",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1C1E)
            )
            Text(
                text = "岚山 · 下午 6:42",
                fontSize = 10.sp,
                color = Color(0xFF9CA3AF)
            )
        }
        Text(
            text = "0:47",
            fontSize = 11.sp,
            color = Color(0xFF9CA3AF)
        )
    }
    // Waveform
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .padding(horizontal = 16.dp)
            .offset(y = (-8).dp), // pull up slightly towards audio info
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EditorMockData.waveforms.forEach { bar ->
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(bar.h.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (bar.played) Color(0xFF5552E4) else Color(0xFFE5E7EB))
            )
        }
    }
}

@Composable
private fun EditorBottomToolbar(bottomPadding: androidx.compose.ui.unit.Dp) {
    val items = listOf(
        Pair(Icons.Default.Image, "相册"),
        Pair(Icons.Default.CameraAlt, "拍摄"),
        Pair(Icons.Default.Mic, "录音"),
        Pair(Icons.Default.Palette, "主题")
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = bottomPadding + 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { (icon, label) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { /* TODO */ }
                    .padding(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color(0xFF6B7280),
                    modifier = Modifier.size(19.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    fontSize = 9.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
        }
    }
}
