package com.jian.tracemind.feature.editor.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Mood
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorMetadataRow(
    mood: String?,
    weather: String?,
    tags: List<String>,
    location: String?,
    onMoodClick: () -> Unit,
    onWeatherClick: () -> Unit,
    onTagsClick: () -> Unit,
    onLocationClick: () -> Unit,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val colors = AssistChipDefaults.assistChipColors(
            containerColor = contentColor.copy(alpha = 0.1f),
            labelColor = contentColor,
            leadingIconContentColor = contentColor
        )
        val border = AssistChipDefaults.assistChipBorder(
            borderColor = Color.Transparent,
            enabled = true
        )

        AssistChip(
            onClick = onMoodClick,
            label = { Text(mood ?: "心情") },
            leadingIcon = {
                Icon(Icons.Rounded.Mood, contentDescription = "Mood", modifier = Modifier.size(16.dp))
            },
            colors = colors,
            border = border
        )

        AssistChip(
            onClick = onWeatherClick,
            label = { Text(weather ?: "天气") },
            leadingIcon = {
                Icon(Icons.Rounded.Cloud, contentDescription = "Weather", modifier = Modifier.size(16.dp))
            },
            colors = colors,
            border = border
        )

        AssistChip(
            onClick = onTagsClick,
            label = { Text(if (tags.isEmpty()) "标签" else tags.joinToString(", ")) },
            leadingIcon = {
                Icon(Icons.Rounded.LocalOffer, contentDescription = "Tags", modifier = Modifier.size(16.dp))
            },
            colors = colors,
            border = border
        )

        AssistChip(
            onClick = onLocationClick,
            label = { Text(location ?: "添加位置") },
            leadingIcon = {
                Icon(Icons.Rounded.LocationOn, contentDescription = "Location", modifier = Modifier.size(16.dp))
            },
            colors = colors,
            border = border
        )
    }
}
