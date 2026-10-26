package com.novacut.editor.domain.model

enum class TrackType {
    VIDEO,
    AUDIO,
    TEXT,
    OVERLAY,
    FILTER,
    CANVAS
}

data class TimelineState(
    val currentPositionMs: Long = 0L,
    val isPlaying: Boolean = false,
    val zoomLevel: Float = 1.0f, // 1.0 = standard, > 1.0 = zoom in, < 1.0 = zoom out
    val selectedClipId: String? = null,
    val selectedAudioId: String? = null,
    val selectedTextId: String? = null,
    val activeTrackType: TrackType = TrackType.VIDEO,
    val isScrubbing: Boolean = false
) {
    /**
     * Converts a timeline timestamp (ms) to a pixel X offset based on zoom.
     */
    fun timeMsToPixels(timeMs: Long, basePxPerSec: Float = 100f): Float {
        return (timeMs / 1000f) * basePxPerSec * zoomLevel
    }

    /**
     * Converts a pixel X offset back to milliseconds based on zoom.
     */
    fun pixelsToTimeMs(pixels: Float, basePxPerSec: Float = 100f): Long {
        return ((pixels / (basePxPerSec * zoomLevel)) * 1000f).toLong().coerceAtLeast(0L)
    }
}
