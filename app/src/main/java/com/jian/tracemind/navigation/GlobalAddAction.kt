package com.jian.tracemind.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jian.tracemind.core.domain.model.Folder
import com.jian.tracemind.core.domain.repository.FolderRepository
import com.jian.tracemind.core.ui.components.LiquidIconButton
import com.kyant.backdrop.Backdrop
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalAddViewModel @Inject constructor(
    private val folderRepository: FolderRepository
) : ViewModel() {

    fun createFolder(name: String, parentId: String? = null) {
        viewModelScope.launch {
            val newFolder = Folder(
                id = java.util.UUID.randomUUID().toString(),
                parentId = parentId,
                name = name,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            folderRepository.insertFolder(newFolder)
        }
    }
}

@Composable
fun GlobalAddButton(
    backdrop: Backdrop,
    onNavigateToEditor: () -> Unit,
    currentFolderId: String? = null,
    viewModel: GlobalAddViewModel = hiltViewModel()
) {
    var showMenu by remember { mutableStateOf(false) }
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var folderName by remember { mutableStateOf("") }

    Box {
        LiquidIconButton(
            onClick = { showMenu = true },
            backdrop = backdrop,
            size = 64.dp,
            tint = Color(0xFF00C4B5)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            DropdownMenuItem(
                text = { Text("写日记", fontSize = 14.sp, color = Color(0xFF1A1C1E)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Diary",
                        tint = Color(0xFF1A1C1E),
                        modifier = Modifier.size(20.dp)
                    )
                },
                onClick = {
                    showMenu = false
                    onNavigateToEditor()
                }
            )
            DropdownMenuItem(
                text = { Text("新建文件夹", fontSize = 14.sp, color = Color(0xFF1A1C1E)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.CreateNewFolder,
                        contentDescription = "Folder",
                        tint = Color(0xFF1A1C1E),
                        modifier = Modifier.size(20.dp)
                    )
                },
                onClick = {
                    showMenu = false
                    showCreateFolderDialog = true
                }
            )
        }
    }

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
                        viewModel.createFolder(folderName, currentFolderId)
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
}
