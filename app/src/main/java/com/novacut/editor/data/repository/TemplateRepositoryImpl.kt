package com.novacut.editor.data.repository

import com.novacut.editor.domain.model.TemplateSlot
import com.novacut.editor.domain.model.VideoTemplate
import com.novacut.editor.domain.repository.TemplateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TemplateRepositoryImpl : TemplateRepository {

    private val presetTemplates = listOf(
        VideoTemplate(
            id = "tpl_velocity_tiktok",
            title = "Viral Velocity Slow-Mo",
            creator = "ApexVisuals",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&h=100&fit=crop",
            previewVideoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400&h=700&fit=crop",
            category = "Velocity",
            usesCount = "3.8M",
            likesCount = "420K",
            aspectRatio = "9:16",
            durationMs = 12000L,
            requiredClipsCount = 3,
            musicTitle = "Phonk Drift Anthem (Remix)",
            slots = listOf(
                TemplateSlot("slot_1", "Intro Beat", 2400L),
                TemplateSlot("slot_2", "Drop Build", 3600L),
                TemplateSlot("slot_3", "Velocity Impact", 6000L)
            ),
            beatMarkers = listOf(2400L, 3600L, 4800L, 6000L, 7200L, 9600L),
            defaultText = "NO LIMITS"
        ),
        VideoTemplate(
            id = "tpl_cinematic_travel",
            title = "Cinematic Film 35mm",
            creator = "WanderlustFilms",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop",
            previewVideoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=400&h=700&fit=crop",
            category = "Cinematic",
            usesCount = "1.9M",
            likesCount = "285K",
            aspectRatio = "9:16",
            durationMs = 14000L,
            requiredClipsCount = 4,
            musicTitle = "Golden Hour Atmosphere",
            slots = listOf(
                TemplateSlot("slot_1", "Sunrise Glance", 3000L),
                TemplateSlot("slot_2", "Landscape Panorama", 3500L),
                TemplateSlot("slot_3", "City Vibes", 3500L),
                TemplateSlot("slot_4", "Sunset Outro", 4000L)
            ),
            beatMarkers = listOf(3000L, 6500L, 10000L, 14000L),
            defaultText = "MEMORIES IN 35MM"
        ),
        VideoTemplate(
            id = "tpl_cyberpunk_neon",
            title = "Cyberpunk Beat Drop",
            creator = "NeonStudio",
            avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=100&h=100&fit=crop",
            previewVideoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?w=400&h=700&fit=crop",
            category = "Beats",
            usesCount = "4.5M",
            likesCount = "680K",
            aspectRatio = "9:16",
            durationMs = 9000L,
            requiredClipsCount = 2,
            musicTitle = "Synthwave Overdrive",
            slots = listOf(
                TemplateSlot("slot_1", "Setup Clip", 3000L),
                TemplateSlot("slot_2", "Cyber Glitch Drop", 6000L)
            ),
            beatMarkers = listOf(1500L, 3000L, 4500L, 6000L, 7500L),
            defaultText = "CYBERPUNK 2088"
        ),
        VideoTemplate(
            id = "tpl_vlog_dayinlife",
            title = "Clean Aesthetic Daily Vlog",
            creator = "ChloeEdits",
            avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&h=100&fit=crop",
            previewVideoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400&h=700&fit=crop",
            category = "Vlog",
            usesCount = "2.1M",
            likesCount = "310K",
            aspectRatio = "9:16",
            durationMs = 15000L,
            requiredClipsCount = 5,
            musicTitle = "Sunday Morning Coffee & Lo-Fi",
            slots = listOf(
                TemplateSlot("slot_1", "Morning Routine", 2500L),
                TemplateSlot("slot_2", "Outfit of the Day", 2500L),
                TemplateSlot("slot_3", "Coffee Stop", 3000L),
                TemplateSlot("slot_4", "Work & Study", 3500L),
                TemplateSlot("slot_5", "Evening Walk", 3500L)
            ),
            beatMarkers = listOf(2500L, 5000L, 8000L, 11500L, 15000L),
            defaultText = "A DAY IN MY LIFE ✨"
        )
    )

    override fun getTemplates(category: String?): Flow<List<VideoTemplate>> = flow {
        if (category == null || category == "All") {
            emit(presetTemplates)
        } else {
            emit(presetTemplates.filter { it.category.equals(category, ignoreCase = true) })
        }
    }.flowOn(Dispatchers.IO)

    override fun getTemplateById(templateId: String): Flow<VideoTemplate?> = flow {
        emit(presetTemplates.find { it.id == templateId })
    }.flowOn(Dispatchers.IO)
}
