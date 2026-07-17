package com.jian.tracemind.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.colorControls
import com.kyant.backdrop.effects.lens

@Composable
fun LiquidConfirmDialog(
    visible: Boolean,
    title: String,
    text: String,
    confirmText: String = "确定",
    dismissText: String = "取消",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    backdrop: Backdrop? = null
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)),
        exit = fadeOut(tween(200))
    ) {
        val isLightTheme = !isSystemInDarkTheme()
        val contentColor = if (isLightTheme) Color.Black else Color.White
        val accentColor = Color(0xFF00C4B5)
        val containerColor = if (isLightTheme) Color(0xFFFAFAFA).copy(0.6f) else Color(0xFF121212).copy(0.4f)
        val dimColor = if (isLightTheme) Color(0xFF29293A).copy(0.23f) else Color(0xFF121212).copy(0.56f)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(100f)
                .background(dimColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = 0.65f,
                        stiffness = 400f
                    ),
                    initialScale = 0.8f
                ),
                exit = scaleOut(
                    animationSpec = tween(200),
                    targetScale = 0.9f
                )
            ) {
                Column(
                    Modifier
                        .padding(40.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {}
                        )
                        .then(
                            if (backdrop != null) {
                                Modifier.drawBackdrop(
                                    backdrop = backdrop,
                                    shape = { RoundedCornerShape(48.dp) },
                                    effects = {
                                        colorControls(
                                            brightness = if (isLightTheme) 0.2f else 0f,
                                            saturation = 1.5f
                                        )
                                        blur(if (isLightTheme) 16.dp.toPx() else 8.dp.toPx())
                                        lens(24.dp.toPx(), 48.dp.toPx(), depthEffect = true)
                                    },
                                    onDrawSurface = { drawRect(containerColor) }
                                )
                            } else {
                                Modifier
                                    .clip(RoundedCornerShape(48.dp))
                                    .background(containerColor)
                            }
                        )
                        .fillMaxWidth()
                ) {
                    BasicText(
                        text = title,
                        modifier = Modifier.padding(28.dp, 24.dp, 28.dp, 12.dp),
                        style = TextStyle(contentColor, 24.sp, FontWeight.Medium)
                    )

                    BasicText(
                        text = text,
                        modifier = Modifier
                            .then(
                                if (isLightTheme) {
                                    Modifier
                                } else {
                                    Modifier.graphicsLayer(blendMode = BlendMode.Plus)
                                }
                            )
                            .padding(24.dp, 12.dp, 24.dp, 12.dp),
                        style = TextStyle(contentColor.copy(0.68f), 15.sp),
                        maxLines = 5
                    )

                    Row(
                        Modifier
                            .padding(24.dp, 12.dp, 24.dp, 24.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            Modifier
                                .clip(CircleShape)
                                .background(containerColor.copy(0.2f))
                                .clickable(onClick = onDismiss)
                                .height(48.dp)
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicText(
                                text = dismissText,
                                style = TextStyle(contentColor, 16.sp)
                            )
                        }
                        Row(
                            Modifier
                                .clip(CircleShape)
                                .background(accentColor)
                                .clickable(onClick = onConfirm)
                                .height(48.dp)
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicText(
                                text = confirmText,
                                style = TextStyle(Color.White, 16.sp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiquidInputDialog(
    visible: Boolean,
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    confirmText: String = "保存",
    dismissText: String = "取消",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    backdrop: Backdrop? = null
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)),
        exit = fadeOut(tween(200))
    ) {
        val isLightTheme = !isSystemInDarkTheme()
        val contentColor = if (isLightTheme) Color.Black else Color.White
        val accentColor = Color(0xFF00C4B5)
        val containerColor = if (isLightTheme) Color(0xFFFAFAFA).copy(0.6f) else Color(0xFF121212).copy(0.4f)
        val dimColor = if (isLightTheme) Color(0xFF29293A).copy(0.23f) else Color(0xFF121212).copy(0.56f)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(100f)
                .background(dimColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = 0.65f,
                        stiffness = 400f
                    ),
                    initialScale = 0.8f
                ),
                exit = scaleOut(
                    animationSpec = tween(200),
                    targetScale = 0.9f
                )
            ) {
                Column(
                    Modifier
                        .padding(40.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {}
                        )
                        .then(
                            if (backdrop != null) {
                                Modifier.drawBackdrop(
                                    backdrop = backdrop,
                                    shape = { RoundedCornerShape(48.dp) },
                                    effects = {
                                        colorControls(
                                            brightness = if (isLightTheme) 0.2f else 0f,
                                            saturation = 1.5f
                                        )
                                        blur(if (isLightTheme) 16.dp.toPx() else 8.dp.toPx())
                                        lens(24.dp.toPx(), 48.dp.toPx(), depthEffect = true)
                                    },
                                    onDrawSurface = { drawRect(containerColor) }
                                )
                            } else {
                                Modifier
                                    .clip(RoundedCornerShape(48.dp))
                                    .background(containerColor)
                            }
                        )
                        .fillMaxWidth()
                ) {
                    BasicText(
                        text = title,
                        modifier = Modifier.padding(28.dp, 24.dp, 28.dp, 12.dp),
                        style = TextStyle(contentColor, 24.sp, FontWeight.Medium)
                    )

                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        textStyle = TextStyle(contentColor, 16.sp),
                        cursorBrush = SolidColor(accentColor),
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(containerColor.copy(alpha = 0.3f))
                            .padding(16.dp),
                        decorationBox = { innerTextField ->
                            if (value.isEmpty()) {
                                BasicText(
                                    text = placeholder,
                                    style = TextStyle(contentColor.copy(0.5f), 16.sp)
                                )
                            }
                            innerTextField()
                        },
                        singleLine = true
                    )

                    Row(
                        Modifier
                            .padding(24.dp, 12.dp, 24.dp, 24.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            Modifier
                                .clip(CircleShape)
                                .background(containerColor.copy(0.2f))
                                .clickable(onClick = onDismiss)
                                .height(48.dp)
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicText(
                                text = dismissText,
                                style = TextStyle(contentColor, 16.sp)
                            )
                        }
                        Row(
                            Modifier
                                .clip(CircleShape)
                                .background(accentColor)
                                .clickable(onClick = onConfirm)
                                .height(48.dp)
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicText(
                                text = confirmText,
                                style = TextStyle(Color.White, 16.sp)
                            )
                        }
                    }
                }
            }
        }
    }
}
