package com.novacut.editor.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class FaceTrackingEffect(
    val title: String,
    val description: String,
    val landmarkCount: Int,
    val isAr3D: Boolean
) {
    NONE(
        title = "None",
        description = "Standard video playback without face tracking",
        landmarkCount = 0,
        isAr3D = false
    ),
    MESH_3D_WIREFRAME(
        title = "3D Face Mesh",
        description = "Real-time 468 vertex 3D facial topology wireframe",
        landmarkCount = 468,
        isAr3D = true
    ),
    CYBERPUNK_HUD(
        title = "Cyber Visor",
        description = "Futuristic neon AR HUD locked to facial orientation",
        landmarkCount = 468,
        isAr3D = true
    ),
    AR_SUNGLASSES(
        title = "3D Shades",
        description = "Perspective-accurate 3D sunglasses with depth occlusion",
        landmarkCount = 68,
        isAr3D = true
    ),
    BEAUTY_SMOOTH(
        title = "AI Retouch",
        description = "Real-time facial skin smoothing and eye brightening",
        landmarkCount = 468,
        isAr3D = false
    ),
    GOLDEN_CROWN(
        title = "3D Crown",
        description = "Head pose estimation 3D tracked golden crown",
        landmarkCount = 468,
        isAr3D = true
    ),
    ANIME_EXPRESSION(
        title = "Blendshapes",
        description = "52 real-time facial blendshapes for animated avatar syncing",
        landmarkCount = 52,
        isAr3D = true
    )
}
