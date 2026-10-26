package com.novacut.editor.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Project(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Untitled Project",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val aspectRatio: CanvasAspectRatio = CanvasAspectRatio.RATIO_9_16,
    val resolution: Resolution = Resolution.FHD_1080P,
    val frameRate: Int = 30,
    val videoClips: List<VideoClip> = emptyList(),
    val audioClips: List<AudioClip> = emptyList(),
    val textOverlays: List<TextOverlay> = emptyList(),
    val canvasBackgroundColor: Long = 0xFF000000
) {
    /**
     * Total video duration is the sum of all trimmed and speed-adjusted video clips.
     */
    val totalVideoDurationMs: Long
        get() = videoClips.sumOf { it.timelineDurationMs }

    /**
     * Overall project duration considers video clips, audio tracks, and text overlays.
     */
    val totalDurationMs: Long
        get() {
            val videoDuration = totalVideoDurationMs
            val maxAudioEnd = audioClips.maxOfOrNull { it.endTimelineMs } ?: 0L
            val maxTextEnd = textOverlays.maxOfOrNull { it.endTimelineMs } ?: 0L
            return maxOf(videoDuration, maxAudioEnd, maxTextEnd).coerceAtLeast(1000L)
        }

    /**
     * Finds which video clip contains the given timeline timestamp.
     * Returns the clip index, the clip itself, and the offset within that clip.
     */
    fun findClipAtTimelineTime(timeMs: Long): Triple<Int, VideoClip, Long>? {
        var accumulated = 0L
        for ((index, clip) in videoClips.withIndex()) {
            val clipDuration = clip.timelineDurationMs
            if (timeMs in accumulated until (accumulated + clipDuration)) {
                val offsetInClipTimeline = timeMs - accumulated
                val offsetInClipSource = (offsetInClipTimeline * clip.speed).toLong() + clip.trimStartMs
                return Triple(index, clip, offsetInClipSource)
            }
            accumulated += clipDuration
        }
        // If at the very end
        if (videoClips.isNotEmpty() && timeMs >= accumulated) {
            val lastClip = videoClips.last()
            return Triple(videoClips.lastIndex, lastClip, lastClip.trimEndMs)
        }
        return null
    }
}
