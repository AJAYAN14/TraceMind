package com.jian.tracemind.feature.editor.ui

data class ChipData(val label: String, val on: Boolean)
data class WaveformBar(val h: Int, val played: Boolean)

object EditorMockData {
    val chips = listOf(
        ChipData("😌 平静", true),
        ChipData("☀️ 晴天", false),
        ChipData("📍 京都", true),
        ChipData("+ 标签", false)
    )

    val waveformHeights = listOf(
        4, 18, 8, 26, 14, 30, 10, 22, 6, 28, 16, 32, 8, 20, 12, 28, 18, 10, 24, 30,
        26, 14, 32, 8, 22, 18, 6, 28, 12, 24, 16, 30, 10, 26, 14, 20, 8, 32, 18, 6
    )
    
    val waveforms = waveformHeights.mapIndexed { index, h ->
        WaveformBar(h, played = index < 22)
    }
}
