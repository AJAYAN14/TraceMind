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
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jian.tracemind.core.ui.components.SectionLabel
import com.jian.tracemind.feature.home.ui.components.FolderCard
import com.jian.tracemind.feature.home.ui.components.MemoryCard
import com.jian.tracemind.feature.home.ui.components.OnThisDayCard

@Composable
fun HomeScreen(
    innerPadding: PaddingValues,
    onAddClick: () -> Unit = {},
    onDiaryClick: (String) -> Unit = {},
    onFolderClick: (String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var folderName by remember { mutableStateOf("") }
    
    var showRenameDialog by remember { mutableStateOf(false) }
    var renameFolderId by remember { mutableStateOf("") }
    var renameFolderName by remember { mutableStateOf("") }
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteFolderId by remember { mutableStateOf("") }
    var deleteFolderName by remember { mutableStateOf("") }

    if (showCreateFolderDialog) {
        AlertDialog(
            onDismissRequest = { showCreateFolderDialog = false },
            title = { Text("新建文件夹", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            text = {
                TextField(
                    value = folderName,
                    onValueChange = { folderName = it },
                    placeholder = { Text("文件夹名称") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (folderName.isNotBlank()) {
                        viewModel.createFolder(folderName)
                        showCreateFolderDialog = false
                        folderName = ""
                    }
                }) {
                    Text("创建", color = Color(0xFF1A1C1E))
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showCreateFolderDialog = false 
                    folderName = ""
                }) {
                    Text("取消", color = Color(0xFF9CA3AF))
                }
            },
            containerColor = Color.White
        )
    }

    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("重命名文件夹", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            text = {
                TextField(
                    value = renameFolderName,
                    onValueChange = { renameFolderName = it },
                    placeholder = { Text("新文件夹名称") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (renameFolderName.isNotBlank()) {
                        viewModel.renameFolder(renameFolderId, renameFolderName)
                        showRenameDialog = false
                    }
                }) {
                    Text("保存", color = Color(0xFF1A1C1E))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("取消", color = Color(0xFF9CA3AF))
                }
            },
            containerColor = Color.White
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("删除文件夹", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            text = {
                Text("确定要删除“$deleteFolderName”吗？\n该文件夹下的日记不会被删除，它们将被移至“全部日记”。")
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteFolder(deleteFolderId)
                    showDeleteDialog = false
                }) {
                    Text("删除", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消", color = Color(0xFF9CA3AF))
                }
            },
            containerColor = Color.White
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
            HomeTopBar()
            
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
                                modifier = Modifier.clickable { onDiaryClick(firstDiary.id) }
                            )
                        }
                    }
                }

                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SectionLabel("我的文件夹", modifier = Modifier.padding(bottom = 0.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { showCreateFolderDialog = true },
                                    modifier = Modifier.size(24.dp).padding(end = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Create Folder",
                                        tint = Color(0xFF5552E4)
                                    )
                                }
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

                item {
                    Column {
                        SectionLabel("最近的记忆")
                        Spacer(modifier = Modifier.height(2.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            uiState.recentMemories.forEach { memory ->
                                MemoryCard(
                                    diary = memory,
                                    modifier = Modifier.clickable { onDiaryClick(memory.id) }
                                )
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    bottom = innerPadding.calculateBottomPadding() + 20.dp,
                    end = 20.dp
                ),
            containerColor = Color(0xFF1A1C1E),
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun HomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1A1C1E)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A",
                    color = Color.White,
                    fontSize = 13.sp,
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
        
        IconButton(
            onClick = { /* TODO */ },
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color(0xFF1A1C1E),
                modifier = Modifier.size(17.dp)
            )
        }
    }
}
