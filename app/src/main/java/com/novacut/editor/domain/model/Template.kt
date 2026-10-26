package com.novacut.editor.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TemplateSlot(
    val id: String,
    val label: String,
    val durationMs: Long,
    val recommendedType: String = "Video"
)

@Serializable
data class VideoTemplate(
    val id: String,
    val title: String,
    val creator: String,
    val avatarUrl: String,
    val previewVideoUrl: String,
    val thumbnailUrl: String,
    val category: String,
    val usesCount: String,
    val likesCount: String,
    val aspectRatio: String = "9:16",
    val durationMs: Long = 12000L,
    val requiredClipsCount: Int = 3,
    val musicTitle: String,
    val slots: List<TemplateSlot> = emptyList(),
    val beatMarkers: List<Long> = emptyList(),
    val defaultText: String = ""
)
