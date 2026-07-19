package com.jian.tracemind.feature.editor.ui.components.bloomcolorpicker

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

// ══════════════════════════════════════════════════════════════════════
// Default 18-color palette (12 outer + 6 inner pastel)
// ══════════════════════════════════════════════════════════════════════

private val DefaultOuterColors = listOf(
    Color(0xFFF7C13F), // Yellow-orange
    Color(0xFFF58A3F), // Orange
    Color(0xFFEF4D3D), // Red
    Color(0xFFE03D72), // Pink
    Color(0xFF9E36B3), // Purple
    Color(0xFF5D4BB8), // Violet-blue
    Color(0xFF3B7BBF), // Blue
    Color(0xFF38A1C5), // Sky blue
    Color(0xFF3BBFA7), // Turquoise
    Color(0xFF4DBB5A), // Green
    Color(0xFF89C63C), // Light green
    Color(0xFFCBD63C), // Lime
)

private fun deriveInnerColors(outerColors: List<Color>): List<Color> {
    return List(6) { i ->
        val baseColor = outerColors[(i * 2) % outerColors.size]
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(baseColor.toArgb(), hsl)
        hsl[2] = (hsl[2] + 0.25f).coerceIn(0.1f, 0.95f)
        Color(ColorUtils.HSLToColor(hsl))
    }
}

private fun extractLightness(color: Color): Float {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(color.toArgb(), hsl)
    return hsl[2]
}

// ══════════════════════════════════════════════════════════════════════
// Main Composable
// ══════════════════════════════════════════════════════════════════════

/**
 * A premium color picker with a "Bloom" expansion effect, dual-ring color wheel,
 * and a curved arc lightness slider.
 *
 * Port of Flutter's BloomColorPicker from portal_labs.
 *
 * When [autoOpen] is true, the picker automatically opens with the bloom animation
 * on first composition. Call [onDismiss] to remove the picker from the composition tree.
 *
 * @param initialColor The initially selected color.
 * @param onColorChanged Called whenever the selected color changes.
 * @param onDismiss Called after the close animation completes.
 * @param style Visual configuration.
 * @param colors Optional preset color list for the outer wheel. Defaults to 12 curated colors.
 * @param autoOpen Whether to auto-open with bloom animation on first appearance.
 */
@Composable
fun BloomColorPicker(
    initialColor: Color,
    onColorChanged: (Color) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    style: BloomColorPickerStyle = BloomColorPickerStyle(),
    colors: List<Color>? = null,
    autoOpen: Boolean = false,
) {
    // ── State ────────────────────────────────────────────────────────
    var currentColor by remember { mutableStateOf(initialColor) }
    var lightness by remember { mutableFloatStateOf(extractLightness(initialColor)) }
    var isDraggingSlider by remember { mutableStateOf(false) }
    var isReversing by remember { mutableStateOf(false) }
    var isClosing by remember { mutableStateOf(false) }

    val animProgress = remember { Animatable(0f) }
    val rawProgress = animProgress.value

    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    val surfaceColor = MaterialTheme.colorScheme.surface

    // ── Pixel conversions ────────────────────────────────────────────
    val closedRadiusPx = with(density) { style.closedRadius.dp.toPx() }
    val bloomRadiusPx = with(density) { style.bloomRadius.dp.toPx() }
    val sliderWidthPx = with(density) { style.sliderWidth.dp.toPx() }
    val sliderRadiusPx = with(density) { (style.bloomRadius + 12f).dp.toPx() }
    val targetOuterRadiusPx = bloomRadiusPx - (closedRadiusPx - with(density) { 2f.dp.toPx() })
    val endBorderWidthPx = with(density) { 12f.dp.toPx() }

    // ── Derived animation values ─────────────────────────────────────
    val bloomProgress = if (!isReversing) {
        intervalTransform(rawProgress, 0f, 0.85f, BloomOvershootEasing::transform)
    } else {
        intervalTransform(rawProgress, 0.15f, 1.0f, EaseInCubic::transform)
    }

    val contentOpacity = if (!isReversing) {
        intervalTransform(rawProgress, 0.10f, 0.85f, EaseOut::transform)
    } else {
        intervalTransform(rawProgress, 0.15f, 1.0f, EaseInCubic::transform)
    }

    // ── Colors ───────────────────────────────────────────────────────
    val outerColors = remember(colors) {
        if (!colors.isNullOrEmpty()) List(12) { colors[it % colors.size] }
        else DefaultOuterColors
    }
    val innerColors = remember(outerColors) { deriveInnerColors(outerColors) }

    // ── Layout constants (dp) ────────────────────────────────────────
    val circleSize = style.closedRadius * 2f - 8f
    val outerRadiusOffset = style.bloomRadius * 0.48f
    val innerRadiusOffset = style.bloomRadius * 0.25f
    val bloomSize = style.bloomRadius * 2f
    val contentAreaWidth = (style.bloomRadius + 12f + style.sliderWidth / 2f + 6f) * 2f
    val contentAreaHeight = bloomSize + 24f
    val arcAngle = PI.toFloat() / 6f

    // ── Pixel conversions for dot positioning ────────────────────────
    val outerRadiusOffsetPx = with(density) { outerRadiusOffset.dp.toPx() }
    val innerRadiusOffsetPx = with(density) { innerRadiusOffset.dp.toPx() }

    // ── Thumb scale animation ────────────────────────────────────────
    val thumbScale by animateFloatAsState(
        targetValue = if (isDraggingSlider) 1.15f else 1f,
        animationSpec = tween(150),
        label = "thumbScale",
    )

    // ── Close function ───────────────────────────────────────────────
    fun close() {
        if (isClosing) return
        isClosing = true
        onColorChanged(currentColor)
        coroutineScope.launch {
            isReversing = true
            animProgress.animateTo(
                0f,
                tween(style.animationDurationMs, easing = LinearEasing),
            )
            onDismiss()
        }
    }

    // ── Select color ─────────────────────────────────────────────────
    fun selectColor(color: Color) {
        if (style.hapticFeedback) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(color.toArgb(), hsl)
        lightness = hsl[2]
        currentColor = color
    }

    // ── Arc drag handler ─────────────────────────────────────────────
    fun handleArcDrag(position: Offset, canvasCenter: Offset) {
        val localOffset = position - canvasCenter
        val angle = atan2(localOffset.y, localOffset.x).coerceIn(-arcAngle, arcAngle)
        val pct = (angle - (-arcAngle)) / (2f * arcAngle)
        val targetLightness = (1f - pct).coerceIn(0.05f, 0.95f)

        lightness = targetLightness
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(currentColor.toArgb(), hsl)
        hsl[2] = lightness
        currentColor = Color(ColorUtils.HSLToColor(hsl))
    }

    // ── Auto-open ────────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        if (autoOpen) {
            delay(50) // allow one frame for layout
            isReversing = false
            animProgress.animateTo(
                1f,
                tween(style.animationDurationMs, easing = LinearEasing),
            )
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // RENDERING
    // ══════════════════════════════════════════════════════════════════

    Box(modifier = modifier.fillMaxSize()) {

        // ── Scrim ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = rawProgress * 0.4f }
                .background(Color.Black)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { close() },
        )

        // ── Bloom content area ───────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(
                    width = contentAreaWidth.dp,
                    height = contentAreaHeight.dp,
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (rawProgress > 0f) {
                // ── 1. Bloom background circle ───────────────────────
                val progressClamped = bloomProgress.coerceIn(0f, 1f)
                val bgOpacity = progressClamped * 0.92f
                val tintOpacity = progressClamped * 0.06f
                val bgScale = lerp(0.3f, 1f, bloomProgress.coerceIn(0f, 1.15f) / 1.15f)
                val targetBgRadius = targetOuterRadiusPx

                Canvas(
                    modifier = Modifier
                        .size(((targetOuterRadiusPx / with(density) { 1.dp.toPx() }) * 2f).dp)
                        .graphicsLayer {
                            scaleX = bgScale
                            scaleY = bgScale
                        },
                ) {
                    // Shadow glow
                    drawIntoCanvas { canvas ->
                        val shadowPaint = android.graphics.Paint().apply {
                            color = currentColor.copy(alpha = progressClamped * 0.15f).toArgb()
                            maskFilter = BlurMaskFilter(with(density) { 16.dp.toPx() }, BlurMaskFilter.Blur.NORMAL)
                            setStyle(android.graphics.Paint.Style.FILL)
                            isAntiAlias = true
                        }
                        canvas.nativeCanvas.drawCircle(
                            center.x, center.y,
                            size.minDimension / 2f + with(density) { 2.dp.toPx() },
                            shadowPaint,
                        )
                    }
                    // Surface background
                    drawCircle(
                        color = surfaceColor.copy(alpha = bgOpacity),
                        radius = size.minDimension / 2f,
                    )
                    // Color tint
                    drawCircle(
                        color = currentColor.copy(alpha = tintOpacity),
                        radius = size.minDimension / 2f,
                    )
                }

                // ── 2. Morphing ring ─────────────────────────────────
                Canvas(modifier = Modifier.size(bloomSize.dp)) {
                    drawMorphingRing(
                        progress = bloomProgress,
                        color = currentColor,
                        closedRadiusPx = closedRadiusPx,
                        targetOuterRadiusPx = targetOuterRadiusPx,
                        endBorderWidthPx = endBorderWidthPx,
                    )
                }

                // ── 3. Arc slider (behind dots) ──────────────────────
                val sliderScale = if (!isReversing) {
                    intervalTransform(rawProgress, 0.10f, 0.85f, EaseOutCubic::transform)
                } else {
                    intervalTransform(rawProgress, 0.55f, 1.0f, EaseInCubic::transform)
                }
                val sliderSpringProg = if (!isReversing) {
                    intervalTransform(rawProgress, 0.10f, 0.85f, BloomSpringEasing::transform)
                } else {
                    sliderScale
                }
                val sliderVisualScale = lerp(0.85f, 1.0f, sliderScale)
                val sliderTranslationXPx = (sliderSpringProg - 1f) * sliderRadiusPx

                Canvas(
                    modifier = Modifier
                        .size(
                            width = contentAreaWidth.dp,
                            height = contentAreaHeight.dp,
                        )
                        .graphicsLayer {
                            alpha = sliderScale * contentOpacity
                            translationX = sliderTranslationXPx
                            scaleX = sliderVisualScale
                            scaleY = sliderVisualScale
                        }
                        .pointerInput(Unit) {
                            val canvasCenter = Offset(size.width / 2f, size.height / 2f)
                            val sRadiusPx = (style.bloomRadius + 12f).dp.toPx()
                            val sWidthPx = style.sliderWidth.dp.toPx()
                            val paddingPx = 24f.dp.toPx()

                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent(PointerEventPass.Main)
                                    val down = event.changes.firstOrNull {
                                        it.changedToDown()
                                    } ?: continue

                                    if (!isPositionOnArc(
                                            down.position,
                                            canvasCenter,
                                            sRadiusPx,
                                            sWidthPx,
                                            arcAngle,
                                            paddingPx,
                                        )
                                    ) continue

                                    // Consume and start drag tracking
                                    down.consume()
                                    isDraggingSlider = true
                                    if (style.hapticFeedback) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                    handleArcDrag(down.position, canvasCenter)

                                    // Track drag until release
                                    var dragActive = true
                                    while (dragActive) {
                                        val dragEvent = awaitPointerEvent()
                                        dragEvent.changes.forEach { change ->
                                            if (change.pressed) {
                                                change.consume()
                                                handleArcDrag(change.position, canvasCenter)
                                            } else {
                                                dragActive = false
                                            }
                                        }
                                    }

                                    isDraggingSlider = false
                                }
                            }
                        },
                ) {
                    drawArcSlider(
                        currentColor = currentColor,
                        lightness = lightness,
                        radiusPx = sliderRadiusPx,
                        strokeWidthPx = sliderWidthPx,
                        arcAngle = arcAngle,
                        thumbScale = thumbScale,
                    )
                }

                // ── 4. Outer ring — 12 color dots ────────────────────
                outerColors.forEachIndexed { index, color ->
                    val angle = (index / 12f) * 2f * PI.toFloat() - PI.toFloat() / 2f
                    val dx = cos(angle)
                    val dy = sin(angle)

                    val dotStart = 0.10f + (index / 12f) * 0.45f
                    val dotEnd = (dotStart + 0.30f).coerceIn(0f, 1f)
                    val dotScale = if (!isReversing) {
                        intervalTransform(rawProgress, dotStart, dotEnd, EaseOutCubic::transform)
                    } else {
                        val rStart = dotStart + 0.15f
                        val rEnd = (dotEnd + 0.15f).coerceIn(0f, 1f)
                        intervalTransform(rawProgress, rStart, rEnd, EaseInCubic::transform)
                    }

                    if (dotScale > 0.01f) {
                        val visualScale = if (isReversing) {
                            dotScale * bloomProgress.coerceIn(0f, 1f)
                        } else {
                            lerp(0.7f, 1f, dotScale)
                        }
                        val translationFactor = visualScale

                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset {
                                    IntOffset(
                                        (dx * translationFactor * outerRadiusOffsetPx).roundToInt(),
                                        (dy * translationFactor * outerRadiusOffsetPx).roundToInt(),
                                    )
                                }
                                .size(circleSize.dp)
                                .graphicsLayer {
                                    scaleX = visualScale
                                    scaleY = visualScale
                                    alpha = dotScale
                                }
                                .shadow(
                                    elevation = (8f * dotScale).dp,
                                    shape = CircleShape,
                                    ambientColor = color.copy(alpha = 0.30f),
                                    spotColor = color.copy(alpha = 0.30f),
                                )
                                .clip(CircleShape)
                                .background(color)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                ) { selectColor(color) },
                        )
                    }
                }

                // ── 5. Inner ring — 6 pastel dots ────────────────────
                innerColors.forEachIndexed { index, color ->
                    val angle = (index / 6f) * 2f * PI.toFloat() - PI.toFloat() / 2f
                    val dx = cos(angle)
                    val dy = sin(angle)

                    val dotStart = 0.20f + (index / 6f) * 0.35f
                    val dotEnd = (dotStart + 0.30f).coerceIn(0f, 1f)
                    val dotScale = if (!isReversing) {
                        intervalTransform(rawProgress, dotStart, dotEnd, EaseOutCubic::transform)
                    } else {
                        val rStart = dotStart + 0.15f
                        val rEnd = (dotEnd + 0.15f).coerceIn(0f, 1f)
                        intervalTransform(rawProgress, rStart, rEnd, EaseInCubic::transform)
                    }

                    if (dotScale > 0.01f) {
                        val visualScale = if (isReversing) {
                            dotScale * bloomProgress.coerceIn(0f, 1f)
                        } else {
                            lerp(0.7f, 1f, dotScale)
                        }
                        val translationFactor = visualScale

                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset {
                                    IntOffset(
                                        (dx * translationFactor * innerRadiusOffsetPx).roundToInt(),
                                        (dy * translationFactor * innerRadiusOffsetPx).roundToInt(),
                                    )
                                }
                                .size(circleSize.dp)
                                .graphicsLayer {
                                    scaleX = visualScale
                                    scaleY = visualScale
                                    alpha = dotScale
                                }
                                .shadow(
                                    elevation = (6f * dotScale).dp,
                                    shape = CircleShape,
                                    ambientColor = color.copy(alpha = 0.20f),
                                    spotColor = color.copy(alpha = 0.20f),
                                )
                                .clip(CircleShape)
                                .background(color)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                ) { selectColor(color) },
                        )
                    }
                }

                // ── 6. Center close / white button ───────────────────
                val centerStart = 0.10f
                val centerEnd = 0.80f
                val centerScale = if (!isReversing) {
                    intervalTransform(rawProgress, centerStart, centerEnd, EaseOutCubic::transform)
                } else {
                    intervalTransform(rawProgress, 0.25f, 0.95f, EaseInCubic::transform)
                }

                if (centerScale > 0.01f) {
                    val centerVisualScale = lerp(0.7f, 1f, centerScale)

                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(circleSize.dp)
                            .graphicsLayer {
                                scaleX = centerVisualScale
                                scaleY = centerVisualScale
                                alpha = centerScale
                            }
                            .shadow(
                                elevation = (8f * centerScale).dp,
                                shape = CircleShape,
                                ambientColor = Color.Black.copy(alpha = 0.10f),
                                spotColor = Color.Black.copy(alpha = 0.10f),
                            )
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { 
                                selectColor(Color.White)
                                close() 
                            },
                    )
                }
            }
        }
    }
}
