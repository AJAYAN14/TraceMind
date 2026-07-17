package com.jian.tracemind.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.jian.tracemind.feature.home.R
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jian.tracemind.core.ui.components.LiquidAppBar
import com.jian.tracemind.core.ui.components.LiquidIconButton
import com.jian.tracemind.core.ui.components.SectionLabel
import com.jian.tracemind.feature.home.ui.components.FolderCard
import com.jian.tracemind.core.ui.components.MemoryCard
import com.jian.tracemind.feature.home.ui.components.OnThisDayCard
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

@Composable
fun HomeScreen(
    innerPadding: PaddingValues,
    onDiaryClick: (String) -> Unit = {},
    onFolderClick: (String) -> Unit = {},
    onSearchClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val localBackdrop = rememberLayerBackdrop()
    
    var showRenameDialog by remember { mutableStateOf(false) }
    var renameFolderId by remember { mutableStateOf("") }
    var renameFolderName by remember { mutableStateOf("") }
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteFolderId by remember { mutableStateOf("") }
    var deleteFolderName by remember { mutableStateOf("") }
    
    var showDeleteDiaryDialog by remember { mutableStateOf(false) }
    var deleteDiaryId by remember { mutableStateOf("") }



    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
            LiquidAppBar(
                navigationIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1A1C1E)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "A",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "早上好",
                                color = Color(0xFF9CA3AF),
                                fontSize = 10.sp
                            )
                            Text(
                                text = "TraceMind",
                                color = Color(0xFF1A1C1E),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                actions = {
                    LiquidIconButton(
                        onClick = onSearchClick,
                        backdrop = localBackdrop
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            val isEmpty = uiState.onThisDayDiaries.isEmpty() && uiState.folders.isEmpty() && uiState.recentMemories.isEmpty()
            
            Box(Modifier.weight(1f).layerBackdrop(localBackdrop)) {
                if (isEmpty) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ghost_empty_state))
                        val progress by animateLottieCompositionAsState(
                            composition,
                            iterations = LottieConstants.IterateForever
                        )
                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier.size(200.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "没有记录，一片空白~",
                            color = Color(0xFF6B7280),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 20.dp, 
                        end = 20.dp, 
                        top = 8.dp, 
                        bottom = innerPadding.calculateBottomPadding() + 80.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    if (uiState.onThisDayDiaries.isNotEmpty()) {
                    item {
                        Column {
                            SectionLabel("往日今天")
                            // Pass the first one for now, or build a pager
                            val firstDiary = uiState.onThisDayDiaries.first()
                            OnThisDayCard(
                                diary = firstDiary,
                                onClick = { onDiaryClick(firstDiary.id) },
                                onDeleteClick = {
                                    deleteDiaryId = firstDiary.id
                                    showDeleteDiaryDialog = true
                                }
                            )
                        }
                    }
                }

                if (uiState.folders.isNotEmpty()) {
                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SectionLabel("我的文件夹", modifier = Modifier.padding(bottom = 0.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "查看全部",
                                        color = Color(0xFF5552E4),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.clickable { onFolderClick("") }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(uiState.folders) { model ->
                                    FolderCard(
                                        model = model,
                                        onClick = { onFolderClick(model.folder.id) },
                                        onRenameClick = {
                                            renameFolderId = model.folder.id
                                            renameFolderName = model.folder.name
                                            showRenameDialog = true
                                        },
                                        onDeleteClick = {
                                            deleteFolderId = model.folder.id
                                            deleteFolderName = model.folder.name
                                            showDeleteDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                if (uiState.recentMemories.isNotEmpty()) {
                    item {
                        Column {
                            SectionLabel("最近的记忆")
                            Spacer(modifier = Modifier.height(2.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                uiState.recentMemories.forEach { memory ->
                                    MemoryCard(
                                        diary = memory,
                                        onClick = { onDiaryClick(memory.id) },
                                        onDeleteClick = {
                                            deleteDiaryId = memory.id
                                            showDeleteDiaryDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                }
            }
            }
        }

        com.jian.tracemind.core.ui.components.LiquidInputDialog(
            visible = showRenameDialog,
            title = "重命名文件夹",
            value = renameFolderName,
            onValueChange = { renameFolderName = it },
            placeholder = "新文件夹名称",
            confirmText = "保存",
            onConfirm = {
                if (renameFolderName.isNotBlank()) {
                    viewModel.renameFolder(renameFolderId, renameFolderName)
                    showRenameDialog = false
                }
            },
            onDismiss = { showRenameDialog = false },
            backdrop = localBackdrop
        )
        com.jian.tracemind.core.ui.components.LiquidConfirmDialog(
            visible = showDeleteDialog,
            title = "删除文件夹",
            text = "确定要删除“$deleteFolderName”吗？\n这将永久删除该文件夹及其内部所有的子文件夹和日记！该操作不可恢复。",
            confirmText = "删除",
            onConfirm = {
                viewModel.deleteFolder(deleteFolderId)
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false },
            backdrop = localBackdrop
        )
        com.jian.tracemind.core.ui.components.LiquidConfirmDialog(
            visible = showDeleteDiaryDialog,
            title = "删除日记",
            text = "确定要永久删除这篇日记吗？该操作不可恢复。",
            confirmText = "删除",
            onConfirm = {
                viewModel.deleteDiary(deleteDiaryId)
                showDeleteDiaryDialog = false
            },
            onDismiss = { showDeleteDiaryDialog = false },
            backdrop = localBackdrop
        )
    }
}
