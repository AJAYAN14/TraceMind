package com.jian.tracemind.feature.editor.ui.components.bloomcolorpicker

import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin

/**
 * Maps [value] from the [start, end] interval to [0, 1], applies [curve], and returns the result.
 * Values outside the range are clamped.
 */
internal fun intervalTransform(
    value: Float,
    start: Float,
    end: Float,
    curve: (Float) -> Float,
): Float {
    val t = ((value - start) / (end - start)).coerceIn(0f, 1f)
    return curve(t)
}

/**
 * BloomOvershootCurve: liquid-burst expansion that overshoots to 1.15 then settles to 1.0.
 * Port of Flutter's BloomOvershootCurve.
 */
internal object BloomOvershootEasing {
    fun transform(t: Float): Float {
        return if (t < 0.55f) {
            val x = t / 0.55f
            val easeOutVal = 1f - (1f - x).pow(3)
            easeOutVal * 1.15f
        } else {
            val x = (t - 0.55f) / 0.45f
            val easeInOutVal = if (x < 0.5f) {
                4f * x * x * x
            } else {
                1f - (-2f * x + 2f).pow(3) / 2f
            }
            1.15f - (easeInOutVal * 0.15f)
        }
    }
}

/**
 * BloomSpringCurve: damped harmonic motion with slight overshoot.
 * Port of Flutter's BloomSpringCurve.
 */
internal object BloomSpringEasing {
    fun transform(t: Float): Float {
        if (t >= 0.99f) return 1f
        val raw = 1f - exp(-3f * t) * (cos(5f * t) + 0.6f * sin(5f * t))
        val endVal = 1f - exp(-3f) * (cos(5f) + 0.6f * sin(5f))
        return (raw / endVal).toFloat()
    }
}

/** Cubic ease-in: slow start, accelerating. */
internal object EaseInCubic {
    fun transform(t: Float): Float = t * t * t
}

/** Cubic ease-out: fast start, decelerating. */
internal object EaseOutCubic {
    fun transform(t: Float): Float = 1f - (1f - t).pow(3)
}

/** Quadratic ease-out. */
internal object EaseOut {
    fun transform(t: Float): Float = 1f - (1f - t) * (1f - t)
}
