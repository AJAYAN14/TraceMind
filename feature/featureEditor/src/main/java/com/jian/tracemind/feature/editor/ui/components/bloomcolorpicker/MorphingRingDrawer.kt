package com.jian.tracemind.feature.editor.ui.components.bloomcolorpicker

import android.graphics.BlurMaskFilter
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.util.lerp

/**
 * Draws a morphing ring that transitions from a filled circle (closed state)
 * to a thin ring (open state) with concentric peeling.
 *
 * Port of Flutter's MorphingRingPainter.
 *
 * All parameters are in pixels.
 */
internal fun DrawScope.drawMorphingRing(
    progress: Float,
    color: Color,
    closedRadiusPx: Float,
    targetOuterRadiusPx: Float,
    endBorderWidthPx: Float,
) {
    val center = this.center

    // 1. Current outer radius — smooth linear interpolation.
    val outerRadius = lerp(closedRadiusPx, targetOuterRadiusPx, progress)

    // 2. Inner radius (the hole). Starts at 0 and peels open with ease-in-cubic,
    //    ensuring the disc starts filled and the hole grows outward.
    val innerProgress = EaseInCubic.transform(progress.coerceIn(0f, 1f))
    val targetInnerRadius = targetOuterRadiusPx - endBorderWidthPx
    val innerRadius = lerp(0f, targetInnerRadius, innerProgress)

    // 3. Black drop shadow — matches closed button's visual. Fades out in first 50%.
    if (progress < 0.5f) {
        val shadowOpacity = (1f - progress * 2f).coerceIn(0f, 1f)
        if (shadowOpacity > 0.01f) {
            drawIntoCanvas { canvas ->
                val paint = android.graphics.Paint().apply {
                    this.color = Color.Black.copy(alpha = 0.08f * shadowOpacity).toArgb()
                    maskFilter = BlurMaskFilter(8f * density, BlurMaskFilter.Blur.NORMAL)
                    style = android.graphics.Paint.Style.FILL
                    isAntiAlias = true
                }
                canvas.nativeCanvas.drawCircle(
                    center.x,
                    center.y + 4f * density,
                    outerRadius,
                    paint,
                )
            }
        }
    }

    // 4. Colored glow — radiates outward and fades as the ring opens.
    val glowAlpha = lerp(0.45f, 0f, progress.coerceIn(0f, 1f))
    if (glowAlpha > 0.01f) {
        val glowBlurRadius = lerp(20f * density, 0f, progress.coerceIn(0f, 1f))
        val glowSpread = lerp(6f * density, 0f, progress.coerceIn(0f, 1f))
        if (glowBlurRadius > 0.1f) {
            drawIntoCanvas { canvas ->
                val paint = android.graphics.Paint().apply {
                    this.color = color.copy(alpha = glowAlpha).toArgb()
                    maskFilter = BlurMaskFilter(glowBlurRadius, BlurMaskFilter.Blur.NORMAL)
                    style = android.graphics.Paint.Style.FILL
                    isAntiAlias = true
                }
                canvas.nativeCanvas.drawCircle(
                    center.x,
                    center.y,
                    outerRadius + glowSpread,
                    paint,
                )
            }
        }
    }

    // 5. Colored disc / ring. Solid circle when innerRadius ≈ 0, hollow ring otherwise.
    if (innerRadius <= 0.05f) {
        drawCircle(color = color, radius = outerRadius, center = center)
    } else {
        val outerPath = Path().apply {
            addOval(
                Rect(
                    center.x - outerRadius,
                    center.y - outerRadius,
                    center.x + outerRadius,
                    center.y + outerRadius,
                )
            )
        }
        val innerPath = Path().apply {
            addOval(
                Rect(
                    center.x - innerRadius,
                    center.y - innerRadius,
                    center.x + innerRadius,
                    center.y + innerRadius,
                )
            )
        }
        val ringPath = Path()
        ringPath.op(outerPath, innerPath, PathOperation.Difference)
        drawPath(ringPath, color = color)
    }

    // 6. White border — matches closed button's white border. Fades out in first 50%.
    if (progress < 0.5f) {
        val borderOpacity = (1f - progress * 2f).coerceIn(0f, 1f)
        if (borderOpacity > 0.01f) {
            drawCircle(
                color = Color.White.copy(alpha = borderOpacity),
                radius = outerRadius - 1.5f * density,
                center = center,
                style = Stroke(width = 3f * density),
            )
        }
    }
}
