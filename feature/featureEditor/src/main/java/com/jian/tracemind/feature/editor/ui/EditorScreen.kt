package com.jian.tracemind.feature.editor.ui

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.jian.tracemind.feature.editor.utils.AudioRecorderHelper
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
    val context = LocalContext.current
    
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                viewModel.onImagesSelected(context, uris)
            }
        }
    )

    var isRecording by remember { mutableStateOf(false) }
    val audioRecorderHelper = remember { AudioRecorderHelper(context) }
    
    DisposableEffect(Unit) {
        onDispose { audioRecorderHelper.release() }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val path = audioRecorderHelper.startRecording()
            if (path != null) {
                isRecording = true
                viewModel.onAudioRecorded(path)
            } else {
                Toast.makeText(context, "无法启动录音", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "需要麦克风权限", Toast.LENGTH_SHORT).show()
        }
    }

    fun handleRecordClick() {
        if (isRecording) {
            audioRecorderHelper.stopRecording()
            isRecording = false
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

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

        var showMoodSheet by remember { mutableStateOf(false) }
        var showWeatherSheet by remember { mutableStateOf(false) }

        // Metadata chips
        MetadataChipsRow(
            mood = uiState.mood,
            weather = uiState.weather,
            onMoodClick = { showMoodSheet = true },
            onWeatherClick = { showWeatherSheet = true }
        )

        if (showMoodSheet) {
            MoodSelectionSheet(
                onDismiss = { showMoodSheet = false },
                onSelect = { mood ->
                    viewModel.onMoodChange(mood)
                    showMoodSheet = false
                }
            )
        }

        if (showWeatherSheet) {
            WeatherSelectionSheet(
                onDismiss = { showWeatherSheet = false },
                onSelect = { weather ->
                    viewModel.onWeatherChange(weather)
                    showWeatherSheet = false
                }
            )
        }

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
            if (uiState.images.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.images) { imgPath ->
                        Box(
                            modifier = Modifier
                                .width(280.dp)
                                .height(210.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF3F4F6))
                        ) {
                            AsyncImage(
                                model = imgPath,
                                contentDescription = "Diary Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            // Audio block
            if (uiState.audioPath != null) {
                AudioBlock(
                    isRecording = isRecording,
                    onPlayClick = {
                        if (!isRecording) {
                            audioRecorderHelper.playAudio(uiState.audioPath!!)
                        }
                    }
                )
            }

            // Continuation text
            Text(
                text = "我按下录音键，就那样说了一会儿。有时候，你需要听见自己的声音，才能明白自己真正在想什么。",
                fontSize = 14.sp,
                color = Color(0xFF1A1C1E),
                lineHeight = 24.sp
            )
        }

        // Bottom toolbar
        EditorBottomToolbar(
            bottomPadding = innerPadding.calculateBottomPadding(),
            onPickImage = {
                photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            onRecordAudioClick = { handleRecordClick() },
            isRecording = isRecording
        )
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
private fun MetadataChipsRow(
    mood: String?,
    weather: String?,
    onMoodClick: () -> Unit,
    onWeatherClick: () -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            ChipItem(label = mood ?: "+ 心情", on = mood != null, onClick = onMoodClick)
        }
        item {
            ChipItem(label = weather ?: "+ 天气", on = weather != null, onClick = onWeatherClick)
        }
        item {
            ChipItem(label = "+ 标签", on = false, onClick = {}) // Placeholder for tags
        }
    }
}

@Composable
private fun ChipItem(label: String, on: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (on) Color(0xFF1A1C1E) else Color.White)
            .border(
                width = 1.dp,
                color = if (on) Color(0xFF1A1C1E) else Color(0xFFE5E7EB),
                shape = RoundedCornerShape(50)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (on) Color.White else Color(0xFF6B7280)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoodSelectionSheet(onDismiss: () -> Unit, onSelect: (String) -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("记录此刻心情", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
            val moods = listOf("😌 平静", "😊 开心", "😔 难过", "😡 生气", "🤯 崩溃", "🤩 惊喜")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(moods) { mood ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onSelect(mood) }.padding(8.dp)
                    ) {
                        Text(text = mood.split(" ")[0], fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = mood.split(" ")[1], fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeatherSelectionSheet(onDismiss: () -> Unit, onSelect: (String) -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("今天天气如何", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
            val weathers = listOf("☀️ 晴天", "☁️ 多云", "🌧️ 雨天", "❄️ 雪天", "💨 刮风", "🌩️ 雷阵雨")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(weathers) { weather ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onSelect(weather) }.padding(8.dp)
                    ) {
                        Text(text = weather.split(" ")[0], fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = weather.split(" ")[1], fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
private fun AudioBlock(isRecording: Boolean, onPlayClick: () -> Unit) {
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
                .background(if (isRecording) Color.Red else Color(0xFF1A1C1E))
                .clickable { onPlayClick() },
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
                text = if (isRecording) "正在录音..." else "语音备忘",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isRecording) Color.Red else Color(0xFF1A1C1E)
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
private fun EditorBottomToolbar(
    bottomPadding: androidx.compose.ui.unit.Dp,
    onPickImage: () -> Unit,
    onRecordAudioClick: () -> Unit,
    isRecording: Boolean
) {
    val items = listOf(
        Pair(Icons.Default.Image, "相册"),
        Pair(Icons.Default.Mic, if (isRecording) "停止" else "录音"),
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
                    .clickable {
                        when (label) {
                            "相册" -> onPickImage()
                            "录音", "停止" -> onRecordAudioClick()
                        }
                    }
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
