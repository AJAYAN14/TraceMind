package com.jian.tracemind.feature.editor.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jian.tracemind.core.ui.components.LiquidAppBar
import com.jian.tracemind.core.ui.components.LiquidIconButton
import com.jian.tracemind.core.ui.components.LiquidTextButton
import com.kyant.backdrop.backdrops.LayerBackdrop

@Composable
fun EditorTopBar(
    dateStr: String,
    onBack: () -> Unit,
    onSave: () -> Unit,
    backdrop: LayerBackdrop
) {
    LiquidAppBar(
        centerTitle = true,
        navigationIcon = {
            LiquidIconButton(
                onClick = onBack,
                backdrop = backdrop,
                surfaceColor = Color(0xFF00C4B5),
                tint = Color(0xFF00C4B5)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        title = {
            Text(
                text = dateStr,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1C1E)
            )
        },
        actions = {
            LiquidTextButton(
                onClick = onSave,
                backdrop = backdrop,
                surfaceColor = Color(0xFF00C4B5),
                tint = Color(0xFF00C4B5)
            ) {
                Text(
                    text = "保存",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}
