package com.jian.tracemind.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun TraceMindConfirmDialog(
    title: String,
    text: String,
    confirmText: String = "确定",
    confirmTextColor: Color = Color.Red,
    dismissText: String = "取消",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
        text = { Text(text) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText, color = confirmTextColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText, color = Color(0xFF9CA3AF))
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun TraceMindInputDialog(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    confirmText: String = "保存",
    dismissText: String = "取消",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
        text = {
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText, color = Color(0xFF1A1C1E))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText, color = Color(0xFF9CA3AF))
            }
        },
        containerColor = Color.White
    )
}
