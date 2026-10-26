package com.novacut.editor.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AudioClip(
    val id: String = UUID.randomUUID().toString(),
    val uri: String? = null,
    val title: String,
    val startTimelineMs: Long = 0L,
    val sourceDurationMs: Long,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long = sourceDurationMs,
    val volume: Float = 1.0f,
    val isFadeIn: Boolean = false,
    val isFadeOut: Boolean = false,
    val category: String = "Music" // Music, SFX, Voiceover
) {
    val durationMs: Long
        get() = (trimEndMs - trimStartMs).coerceAtLeast(100L)

    val endTimelineMs: Long
        get() = startTimelineMs + durationMs
}
