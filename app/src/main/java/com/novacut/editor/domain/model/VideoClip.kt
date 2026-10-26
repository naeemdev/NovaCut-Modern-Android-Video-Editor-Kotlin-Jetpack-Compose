package com.novacut.editor.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class VideoClip(
    val id: String = UUID.randomUUID().toString(),
    val uri: String,
    val name: String,
    val sourceDurationMs: Long,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long = sourceDurationMs,
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val isMuted: Boolean = false,
    val filterType: FilterType = FilterType.NONE,
    val faceEffect: FaceTrackingEffect = FaceTrackingEffect.NONE,
    val brightness: Float = 0.0f, // -1.0 to 1.0
    val contrast: Float = 1.0f,   // 0.0 to 2.0
    val saturation: Float = 1.0f, // 0.0 to 2.0
    val rotationDegrees: Int = 0,
    val scale: Float = 1.0f,
    val panOffsetX: Float = 0.0f,
    val panOffsetY: Float = 0.0f,
    val cropPreset: String = "Free",
    val animation: String = "None"
) {
    /**
     * The trimmed source duration before speed modification.
     */
    val trimmedDurationMs: Long
        get() = (trimEndMs - trimStartMs).coerceAtLeast(100L)

    /**
     * Duration on the timeline after applying the speed modifier.
     */
    val timelineDurationMs: Long
        get() = (trimmedDurationMs / speed).toLong().coerceAtLeast(100L)

    fun copyWithSplit(splitAtTrimmedMs: Long): Pair<VideoClip, VideoClip> {
        val absoluteSplitMs = (trimStartMs + splitAtTrimmedMs).coerceIn(trimStartMs, trimEndMs)
        val firstClip = this.copy(
            id = UUID.randomUUID().toString(),
            trimEndMs = absoluteSplitMs
        )
        val secondClip = this.copy(
            id = UUID.randomUUID().toString(),
            trimStartMs = absoluteSplitMs
        )
        return Pair(firstClip, secondClip)
    }
}
