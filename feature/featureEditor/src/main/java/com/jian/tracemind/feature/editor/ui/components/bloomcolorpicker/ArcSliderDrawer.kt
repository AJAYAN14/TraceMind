package com.jian.tracemind.feature.editor.ui.components.bloomcolorpicker

import android.graphics.BlurMaskFilter
import android.graphics.SweepGradient
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Draws a curved arc lightness slider with a draggable thumb.
 *
 * The arc sits to the right of the color wheel and spans ±[arcAngle] from 3-o'clock.
 * A gradient from light → dark runs along the arc, and a round thumb shows the current
 * lightness position.
 *
 * Port of Flutter's ArcSliderPainter.
 *
 * All parameters are in pixels (except [arcAngle] which is in radians).
 */
internal fun DrawScope.drawArcSlider(
    currentColor: Color,
    lightness: Float,
    radiusPx: Float,
    strokeWidthPx: Float,
    arcAngle: Float,
    thumbScale: Float,
) {
    val cx = size.width / 2f
    val cy = size.height / 2f

    drawIntoCanvas { canvas ->
        val nativeCanvas = canvas.nativeCanvas

        // ── HSL color variants for the gradient ─────────────────────────
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(currentColor.toArgb(), hsl)

        val topColorInt = ColorUtils.HSLToColor(floatArrayOf(hsl[0], hsl[1], 0.95f))
        val midColorInt = currentColor.toArgb()
        val bottomColorInt = ColorUtils.HSLToColor(floatArrayOf(hsl[0], hsl[1], 0.05f))

        // ── Rotation: align arc to ±arcAngle centered on 3-o'clock ─────
        val rotationDeg = Math.toDegrees((-arcAngle - 0.2).toDouble()).toFloat()
        val arcStartDeg = Math.toDegrees(0.2.toDouble()).toFloat()
        val arcSweepDeg = Math.toDegrees((2.0 * arcAngle).toDouble()).toFloat()

        nativeCanvas.save()
        nativeCanvas.translate(cx, cy)
        nativeCanvas.rotate(rotationDeg)

        // ── Gradient shader ─────────────────────────────────────────────
        // Map Flutter's endAngle-based stops to Android's full-sweep fractions.
        val stop0 = 0.2f / (2f * PI.toFloat())
        val stop1 = (arcAngle + 0.2f) / (2f * PI.toFloat())
        val stop2 = (2f * arcAngle + 0.2f) / (2f * PI.toFloat())

        val arcPaint = android.graphics.Paint().apply {
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = strokeWidthPx
            strokeCap = android.graphics.Paint.Cap.ROUND
            isAntiAlias = true
            shader = SweepGradient(
                0f, 0f,
                intArrayOf(topColorInt, midColorInt, bottomColorInt),
                floatArrayOf(stop0, stop1, stop2),
            )
        }

        val arcRect = android.graphics.RectF(
            -radiusPx, -radiusPx, radiusPx, radiusPx,
        )

        // ── Shadow (shifted down slightly) ──────────────────────────────
        val shadowPaint = android.graphics.Paint().apply {
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = strokeWidthPx
            strokeCap = android.graphics.Paint.Cap.ROUND
            isAntiAlias = true
            color = android.graphics.Color.argb((0.16f * 255).toInt(), 0, 0, 0)
            maskFilter = BlurMaskFilter(3.5f * density, BlurMaskFilter.Blur.NORMAL)
        }

        nativeCanvas.save()
        val undoRotDeg = Math.toDegrees((arcAngle + 0.2).toDouble()).toFloat()
        nativeCanvas.rotate(undoRotDeg)
        nativeCanvas.translate(0f, 3.5f * density)
        nativeCanvas.rotate(-undoRotDeg)
        nativeCanvas.drawArc(arcRect, arcStartDeg, arcSweepDeg, false, shadowPaint)
        nativeCanvas.restore()

        // ── Main arc ────────────────────────────────────────────────────
        nativeCanvas.drawArc(arcRect, arcStartDeg, arcSweepDeg, false, arcPaint)

        nativeCanvas.restore() // back to un-rotated, un-translated space

        // ── Thumb ───────────────────────────────────────────────────────
        val thumbTheta = -arcAngle + (1f - lightness) * (2f * arcAngle)
        val thumbCx = cx + radiusPx * cos(thumbTheta)
        val thumbCy = cy + radiusPx * sin(thumbTheta)
        val baseRadius = strokeWidthPx / 2f

        // Thumb shadow
        val thumbShadowPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.argb((0.15f * 255).toInt(), 0, 0, 0)
            maskFilter = BlurMaskFilter(4f * density, BlurMaskFilter.Blur.NORMAL)
            style = android.graphics.Paint.Style.FILL
            isAntiAlias = true
        }
        nativeCanvas.drawCircle(
            thumbCx, thumbCy,
            (baseRadius + 4f * density) * thumbScale,
            thumbShadowPaint,
        )

        // White border ring
        val borderPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            style = android.graphics.Paint.Style.FILL
            isAntiAlias = true
        }
        nativeCanvas.drawCircle(
            thumbCx, thumbCy,
            (baseRadius + 2f * density) * thumbScale,
            borderPaint,
        )

        // Inner fill (current lightness)
        val thumbHsl = FloatArray(3)
        ColorUtils.colorToHSL(currentColor.toArgb(), thumbHsl)
        thumbHsl[2] = lightness
        val innerPaint = android.graphics.Paint().apply {
            color = ColorUtils.HSLToColor(thumbHsl)
            style = android.graphics.Paint.Style.FILL
            isAntiAlias = true
        }
        nativeCanvas.drawCircle(
            thumbCx, thumbCy,
            (baseRadius - 1f * density) * thumbScale,
            innerPaint,
        )
    }
}

/**
 * Hit-tests a touch position against the arc slider region.
 *
 * @param position Touch position relative to the canvas top-left.
 * @param canvasCenter Center of the canvas.
 * @param sliderRadiusPx Radius of the slider arc (px).
 * @param strokeWidthPx Width of the arc stroke (px).
 * @param arcAngle Half sweep angle (radians).
 * @param paddingPx Extra hit-test padding around the arc (px).
 * @return true if the touch is within the arc slider region.
 */
internal fun isPositionOnArc(
    position: Offset,
    canvasCenter: Offset,
    sliderRadiusPx: Float,
    strokeWidthPx: Float,
    arcAngle: Float,
    paddingPx: Float,
): Boolean {
    val localOffset = position - canvasCenter
    val distance = localOffset.getDistance()
    val angle = kotlin.math.atan2(localOffset.y, localOffset.x)
    val isWithinRadius = kotlin.math.abs(distance - sliderRadiusPx) <= (strokeWidthPx / 2f + paddingPx)
    val isWithinAngle = angle in (-arcAngle - 0.15f)..(arcAngle + 0.15f)
    return isWithinRadius && isWithinAngle
}
