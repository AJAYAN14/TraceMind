package com.jian.tracemind.feature.folder.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.jian.tracemind.core.ui.extensions.traceShadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jian.tracemind.core.domain.model.Folder
import com.jian.tracemind.core.ui.components.LiquidAppBar

@Composable
fun FolderListScreen(
    innerPadding: PaddingValues,
    onNavigateToFolder: (String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FolderListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
            
            LiquidAppBar(
                title = {
                    Text(
                        text = "所有文件夹",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                centerTitle = true
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 16.dp,
                    bottom = innerPadding.calculateBottomPadding() + 80.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Top Banner: All Diaries
                item(span = { GridItemSpan(maxLineSpan) }) {
                    AllDiariesBanner(onClick = { onNavigateToFolder(null) })
                }
                
                // Folders Grid
                items(uiState.folders) { folder ->
                    FolderGridItem(
                        folder = folder,
                        onClick = { onNavigateToFolder(folder.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun AllDiariesBanner(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Column {
            Text(text = "📚", fontSize = 32.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "全部日记",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "查看所有未分类和已分类的内容",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun FolderGridItem(folder: Folder, onClick: () -> Unit) {
    androidx.compose.material3.Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth().height(120.dp).traceShadow(borderRadius = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(text = "📁", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = folder.name,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}
