package com.jian.tracemind.feature.folder.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jian.tracemind.core.ui.extensions.traceShadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.jian.tracemind.core.domain.model.Diary
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FolderScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues = PaddingValues(),
    viewModel: FolderViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToFolder: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var isSearchExpanded by remember { mutableStateOf(false) }
    var diaryToMove by remember { mutableStateOf<String?>(null) }

    if (diaryToMove != null) {
        AlertDialog(
            onDismissRequest = { diaryToMove = null },
            title = { Text("移动到...") },
            text = {
                LazyColumn {
                    item {
                        Text(
                            text = "未分类",
                            modifier = Modifier.fillMaxWidth().clickable {
                                viewModel.moveDiary(diaryToMove!!, null)
                                diaryToMove = null
                            }.padding(vertical = 12.dp)
                        )
                    }
                    items(uiState.folders) { folder ->
                        Text(
                            text = folder.name,
                            modifier = Modifier.fillMaxWidth().clickable {
                                viewModel.moveDiary(diaryToMove!!, folder.id)
                                diaryToMove = null
                            }.padding(vertical = 12.dp)
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { diaryToMove = null }) {
                    Text("取消")
                }
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val backdrop = com.kyant.backdrop.backdrops.rememberLayerBackdrop()

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // App Bar
            Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
            com.jian.tracemind.core.ui.components.LiquidAppBar(
                centerTitle = !isSearchExpanded,
                navigationIcon = {
                    com.jian.tracemind.core.ui.components.LiquidIconButton(
                        onClick = onNavigateBack,
                        backdrop = backdrop,
                        surfaceColor = MaterialTheme.colorScheme.primary,
                        tint = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                title = {
                    if (isSearchExpanded) {
                        TextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChanged(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            placeholder = { Text("搜索日记...", fontSize = 14.sp) },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                    } else {
                        val title = uiState.currentFolder?.name ?: "全部日记"
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                },
                actions = {
                    com.jian.tracemind.core.ui.components.LiquidIconButton(
                        onClick = { isSearchExpanded = !isSearchExpanded },
                        backdrop = backdrop,
                        surfaceColor = MaterialTheme.colorScheme.primary,
                        tint = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = if (isSearchExpanded) Icons.Default.MoreVert else Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )

            // Filter chips
            val chips = listOf("全部", "图文", "纯文本")
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chips.size) { index ->
                    val chip = chips[index]
                    val isSelected = uiState.activeChip == chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surface)
                            .clickable { viewModel.onChipChanged(chip) }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = chip,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Text(
                text = "共 ${uiState.diaries.size} 篇",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
            )

            // Masonry 2-col grid
            val col1 = uiState.diaries.filterIndexed { index, _ -> index % 2 == 0 }
            val col2 = uiState.diaries.filterIndexed { index, _ -> index % 2 == 1 }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 0.dp,
                    bottom = innerPadding.calculateBottomPadding() + 80.dp
                )
            ) {
                if (uiState.subFolders.isNotEmpty()) {
                    item {
                        Text(
                            text = "文件夹",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                    
                    val folderCol1 = uiState.subFolders.filterIndexed { index, _ -> index % 2 == 0 }
                    val folderCol2 = uiState.subFolders.filterIndexed { index, _ -> index % 2 == 1 }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                folderCol1.forEach { folder ->
                                    com.jian.tracemind.feature.folder.ui.FolderGridItem(
                                        folder = folder,
                                        onClick = { onNavigateToFolder(folder.id) }
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                folderCol2.forEach { folder ->
                                    com.jian.tracemind.feature.folder.ui.FolderGridItem(
                                        folder = folder,
                                        onClick = { onNavigateToFolder(folder.id) }
                                    )
                                }
                            }
                        }
                    }
                }

                if (uiState.diaries.isNotEmpty()) {
                    item {
                        Text(
                            text = "日记",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            col1.forEach { diary ->
                                DiaryCardItem(
                                    diary = diary, 
                                    onDelete = { viewModel.deleteDiary(diary.id) },
                                    onMove = { diaryToMove = diary.id }
                                )
                            }
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            col2.forEach { diary ->
                                DiaryCardItem(
                                    diary = diary, 
                                    onDelete = { viewModel.deleteDiary(diary.id) },
                                    onMove = { diaryToMove = diary.id }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryCardItem(
    diary: Diary,
    onDelete: () -> Unit,
    onMove: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    val formatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val dateStr = formatter.format(Date(diary.createdAt))

    val imgH = if (diary.coverImage != null) 160 else 0

    Box {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().traceShadow(borderRadius = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .combinedClickable(
                        onClick = { /* TODO: Navigate to viewer/editor */ },
                        onLongClick = { expanded = true }
                    )
            ) {
                if (imgH > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(imgH.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        AsyncImage(
                            model = diary.coverImage,
                            contentDescription = diary.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                    Text(
                        text = diary.title.ifBlank { diary.content.take(20) }, // Use content snippet if title empty
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 16.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = dateStr,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        val weather = diary.weather
                        if (weather != null) {
                            Text(text = weather, fontSize = 10.sp)
                        }
                        
                        val mood = diary.mood
                        if (mood != null) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = mood, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("移动到...") },
                onClick = { 
                    expanded = false
                    onMove() 
                }
            )
            DropdownMenuItem(
                text = { Text("删除", color = Color.Red) },
                onClick = { 
                    expanded = false
                    onDelete() 
                }
            )
        }
    }
}
