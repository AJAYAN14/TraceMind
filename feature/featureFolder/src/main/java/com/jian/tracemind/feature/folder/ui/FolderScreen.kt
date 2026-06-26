package com.jian.tracemind.feature.folder.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun FolderScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues = PaddingValues()
) {
    var activeChip by remember { mutableStateOf("全部") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // App Bar
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { /* TODO: back */ }, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF1A1C1E),
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "学习日语",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1C1E),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            IconButton(onClick = { /* TODO: filter */ }, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Filter",
                    tint = Color(0xFF1A1C1E),
                    modifier = Modifier.size(17.dp)
                )
            }
        }

        // Filter chips
        androidx.compose.foundation.lazy.LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(FolderMockData.chips.size) { index ->
                val chip = FolderMockData.chips[index]
                val isSelected = activeChip == chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (isSelected) Color(0xFF1A1C1E) else Color.White)
                        .clickable { activeChip = chip }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = chip,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White else Color(0xFF6B7280)
                    )
                }
            }
        }

        Text(
            text = "共 27 篇",
            fontSize = 11.sp,
            color = Color(0xFF9CA3AF),
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
        )

        // Masonry 2-col grid
        val col1 = FolderMockData.folderCards.filterIndexed { index, _ -> index % 2 == 0 }
        val col2 = FolderMockData.folderCards.filterIndexed { index, _ -> index % 2 == 1 }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 0.dp,
                bottom = innerPadding.calculateBottomPadding() + 80.dp // leave space for bottom nav
            )
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Col 1
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        col1.forEach { card ->
                            FolderCardItem(card = card)
                        }
                    }
                    // Col 2
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 20.dp), // offset second column
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        col2.forEach { card ->
                            FolderCardItem(card = card)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FolderCardItem(card: FolderCardData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(card.imgH.dp)
                .background(Color(0xFFF3F4F6))
        ) {
            AsyncImage(
                model = card.img,
                contentDescription = card.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(
                text = card.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1C1E),
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = card.date,
                fontSize = 10.sp,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}
