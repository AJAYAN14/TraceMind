package com.jian.tracemind.feature.editor.ui.components.bloomcolorpicker

import androidx.compose.ui.graphics.Color

/**
 * Layout alignment options for the BloomColorPicker.
 */
enum class BloomColorPickerAlignment {
    /** Color circle on the left, hex pill on the right. */
    CircleLeft,

    /** Color circle on the right, hex pill on the left. */
    CircleRight,
}

/**
 * Visual styling and layout properties for the BloomColorPicker.
 * Port of Flutter's BloomColorPickerStyle from portal_labs.
 */
data class BloomColorPickerStyle(
    /** Radius of the color indicator in the closed state (dp). */
    val closedRadius: Float = 24f,
    /** Maximum radius of the bloom effect in the open state (dp). Defaults to closedRadius * 5. */
    val bloomRadius: Float = closedRadius * 5f,
    /** Width of the lightness slider (dp). */
    val sliderWidth: Float = 24f,
    /** Background color of the hex code pill. */
    val pillBackgroundColor: Color = Color(0xFFFFFFFF),
    /** Text color of the hex code in the pill. */
    val pillTextColor: Color = Color(0xFF1A1A1A),
    /** Icon color in the pill. */
    val iconColor: Color = Color(0xFF8A8A8A),
    /** Duration of state transition animations in ms. */
    val animationDurationMs: Int = 500,
    /** Whether to trigger haptic feedback on interactions. */
    val hapticFeedback: Boolean = true,
    /** Whether to show the hex code pill in the closed state. */
    val showHexPill: Boolean = true,
    /** Alignment of the closed picker components. */
    val alignment: BloomColorPickerAlignment = BloomColorPickerAlignment.CircleLeft,
)
