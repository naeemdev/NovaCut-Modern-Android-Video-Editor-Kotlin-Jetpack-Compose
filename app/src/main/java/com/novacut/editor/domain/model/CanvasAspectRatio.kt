package com.novacut.editor.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class CanvasAspectRatio(val label: String, val ratio: Float, val description: String) {
    RATIO_9_16("9:16", 9f / 16f, "TikTok / Reels / Shorts"),
    RATIO_16_9("16:9", 16f / 9f, "YouTube / Landscape"),
    RATIO_1_1("1:1", 1f, "Instagram Square"),
    RATIO_4_5("4:5", 4f / 5f, "Instagram Portrait"),
    RATIO_21_9("21:9", 21f / 9f, "Cinematic Ultra-wide");

    fun getDimensionsForResolution(resolution: Resolution): Pair<Int, Int> {
        val baseHeight = resolution.height
        return when (this) {
            RATIO_9_16 -> Pair((baseHeight * (9f / 16f)).toInt(), baseHeight)
            RATIO_16_9 -> Pair((baseHeight * (16f / 9f)).toInt(), baseHeight)
            RATIO_1_1 -> Pair(baseHeight, baseHeight)
            RATIO_4_5 -> Pair((baseHeight * (4f / 5f)).toInt(), baseHeight)
            RATIO_21_9 -> Pair((baseHeight * (21f / 9f)).toInt(), baseHeight)
        }
    }
}
