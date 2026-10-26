package com.novacut.editor.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class FilterType(val displayName: String, val category: String) {
    NONE("Original", "Basic"),
    CINEMATIC("Cinematic", "Film"),
    CYBERPUNK("Cyberpunk", "Neon"),
    NOIR("Film Noir", "Retro"),
    VINTAGE("Vintage 90s", "Retro"),
    GOLDEN_HOUR("Golden Hour", "Warm"),
    MOODY_TEAL("Moody Teal", "Film"),
    VIBRANT("Vibrant Pop", "Color");

    /**
     * 4x5 Color matrix for real-time rendering in Compose ColorFilter.colorMatrix
     */
    fun getColorMatrix(): FloatArray {
        return when (this) {
            NONE -> floatArrayOf(
                1f, 0f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f, 0f,
                0f, 0f, 1f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
            CINEMATIC -> floatArrayOf(
                1.2f, 0f, 0f, 0f, 10f,
                0f, 1.05f, 0f, 0f, 0f,
                0f, 0f, 0.85f, 0f, -10f,
                0f, 0f, 0f, 1f, 0f
            )
            CYBERPUNK -> floatArrayOf(
                1.3f, 0f, 0.2f, 0f, 15f,
                0f, 0.85f, 0.1f, 0f, -10f,
                0.2f, 0.1f, 1.4f, 0f, 25f,
                0f, 0f, 0f, 1f, 0f
            )
            NOIR -> floatArrayOf(
                0.33f, 0.59f, 0.11f, 0f, 0f,
                0.33f, 0.59f, 0.11f, 0f, 0f,
                0.33f, 0.59f, 0.11f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
            VINTAGE -> floatArrayOf(
                0.9f, 0.1f, 0.1f, 0f, 20f,
                0.1f, 0.8f, 0.1f, 0f, 15f,
                0.1f, 0.1f, 0.6f, 0f, -10f,
                0f, 0f, 0f, 1f, 0f
            )
            GOLDEN_HOUR -> floatArrayOf(
                1.25f, 0.1f, 0f, 0f, 20f,
                0.05f, 1.1f, 0f, 0f, 10f,
                0f, 0f, 0.75f, 0f, -20f,
                0f, 0f, 0f, 1f, 0f
            )
            MOODY_TEAL -> floatArrayOf(
                0.85f, 0f, 0f, 0f, -10f,
                0f, 1.15f, 0.1f, 0f, 15f,
                0f, 0.1f, 1.25f, 0f, 20f,
                0f, 0f, 0f, 1f, 0f
            )
            VIBRANT -> floatArrayOf(
                1.3f, 0f, 0f, 0f, 0f,
                0f, 1.3f, 0f, 0f, 0f,
                0f, 0f, 1.3f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        }
    }
}
