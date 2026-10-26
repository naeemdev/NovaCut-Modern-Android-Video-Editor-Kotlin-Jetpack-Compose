package com.novacut.editor.domain.usecase.editor

import com.novacut.editor.domain.model.AudioClip
import com.novacut.editor.domain.model.CanvasAspectRatio
import com.novacut.editor.domain.model.FilterType
import com.novacut.editor.domain.model.Project
import com.novacut.editor.domain.model.TextOverlay
import com.novacut.editor.domain.model.VideoClip

class SplitClipUseCase {
    operator fun invoke(project: Project, timelinePositionMs: Long): Project {
        val result = project.findClipAtTimelineTime(timelinePositionMs) ?: return project
        val (clipIndex, clip, _) = result

        // Calculate offset relative to this clip's timeline
        var accumulated = 0L
        for (i in 0 until clipIndex) {
            accumulated += project.videoClips[i].timelineDurationMs
        }
        val clipLocalTimelineMs = timelinePositionMs - accumulated
        val splitOffsetSourceMs = (clipLocalTimelineMs * clip.speed).toLong()

        // Minimum 200ms clip slice to prevent micro-glitches
        if (splitOffsetSourceMs < 200L || (clip.trimmedDurationMs - splitOffsetSourceMs) < 200L) {
            return project
        }

        val (first, second) = clip.copyWithSplit(splitOffsetSourceMs)
        val updatedClips = project.videoClips.toMutableList().apply {
            removeAt(clipIndex)
            add(clipIndex, second)
            add(clipIndex, first)
        }
        return project.copy(videoClips = updatedClips)
    }
}

class TrimClipUseCase {
    operator fun invoke(
        project: Project,
        clipId: String,
        newTrimStartMs: Long? = null,
        newTrimEndMs: Long? = null
    ): Project {
        val updatedClips = project.videoClips.map { clip ->
            if (clip.id == clipId) {
                var start = newTrimStartMs ?: clip.trimStartMs
                var end = newTrimEndMs ?: clip.trimEndMs
                if (end - start < 200L) {
                    if (newTrimStartMs != null) start = end - 200L
                    if (newTrimEndMs != null) end = start + 200L
                }
                clip.copy(
                    trimStartMs = start.coerceIn(0L, clip.sourceDurationMs),
                    trimEndMs = end.coerceIn(0L, clip.sourceDurationMs)
                )
            } else {
                clip
            }
        }
        return project.copy(videoClips = updatedClips)
    }
}

class ApplyFilterUseCase {
    operator fun invoke(
        project: Project,
        clipId: String,
        filterType: FilterType,
        brightness: Float? = null,
        contrast: Float? = null,
        saturation: Float? = null
    ): Project {
        val updatedClips = project.videoClips.map { clip ->
            if (clip.id == clipId) {
                clip.copy(
                    filterType = filterType,
                    brightness = brightness ?: clip.brightness,
                    contrast = contrast ?: clip.contrast,
                    saturation = saturation ?: clip.saturation
                )
            } else {
                clip
            }
        }
        return project.copy(videoClips = updatedClips)
    }
}

class AdjustSpeedUseCase {
    operator fun invoke(project: Project, clipId: String, speed: Float): Project {
        val clampedSpeed = speed.coerceIn(0.1f, 10.0f)
        val updatedClips = project.videoClips.map { clip ->
            if (clip.id == clipId) {
                clip.copy(speed = clampedSpeed)
            } else {
                clip
            }
        }
        return project.copy(videoClips = updatedClips)
    }
}

class AddTextOverlayUseCase {
    operator fun invoke(project: Project, overlay: TextOverlay): Project {
        return project.copy(textOverlays = project.textOverlays + overlay)
    }
}

class UpdateTextOverlayUseCase {
    operator fun invoke(project: Project, updatedOverlay: TextOverlay): Project {
        val updated = project.textOverlays.map {
            if (it.id == updatedOverlay.id) updatedOverlay else it
        }
        return project.copy(textOverlays = updated)
    }
}

class DeleteTextOverlayUseCase {
    operator fun invoke(project: Project, overlayId: String): Project {
        return project.copy(textOverlays = project.textOverlays.filterNot { it.id == overlayId })
    }
}

class AddAudioTrackUseCase {
    operator fun invoke(project: Project, audioClip: AudioClip): Project {
        return project.copy(audioClips = project.audioClips + audioClip)
    }
}

class SetAspectRatioUseCase {
    operator fun invoke(project: Project, aspectRatio: CanvasAspectRatio): Project {
        return project.copy(aspectRatio = aspectRatio)
    }
}

class ApplyFaceEffectUseCase {
    operator fun invoke(
        project: Project,
        clipId: String,
        faceEffect: com.novacut.editor.domain.model.FaceTrackingEffect
    ): Project {
        val updatedClips = project.videoClips.map { clip ->
            if (clip.id == clipId) {
                clip.copy(faceEffect = faceEffect)
            } else {
                clip
            }
        }
        return project.copy(videoClips = updatedClips)
    }
}

class DeleteAudioTrackUseCase {
    operator fun invoke(project: Project, audioId: String): Project {
        return project.copy(audioClips = project.audioClips.filterNot { it.id == audioId })
    }
}

class MoveAudioTrackUseCase {
    operator fun invoke(project: Project, audioId: String, newStartTimelineMs: Long): Project {
        val updated = project.audioClips.map {
            if (it.id == audioId) it.copy(startTimelineMs = newStartTimelineMs.coerceAtLeast(0L)) else it
        }
        return project.copy(audioClips = updated)
    }
}

