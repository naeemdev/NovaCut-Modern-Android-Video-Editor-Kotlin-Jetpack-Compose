package com.novacut.editor.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class TextOverlay(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val startTimelineMs: Long = 0L,
    val durationMs: Long = 5000L,
    val xNormalized: Float = 0.5f,
    val yNormalized: Float = 0.5f,
    val scale: Float = 1.0f,
    val rotationDegrees: Float = 0f,
    val textColorArgb: Long = 0xFFFFFFFF,
    val backgroundColorArgb: Long? = 0xAA000000,
    val fontSizeSp: Int = 22,
    val fontStyleName: String = "Display Bold",
    val hasShadow: Boolean = true,
    val animationType: String = "Fade",
    val labelStyle: String = "Dark Pill"
) {
    val endTimelineMs: Long
        get() = startTimelineMs + durationMs

    fun isVisibleAt(timeMs: Long): Boolean {
        return timeMs in startTimelineMs..endTimelineMs
    }
}
