package com.novacut.editor.domain.model

sealed interface ExportState {
    data object Idle : ExportState
    data object Preparing : ExportState
    data class Rendering(
        val progress: Float, // 0.0 to 1.0
        val etaSeconds: Int = 0,
        val percentageText: String = "${(progress * 100).toInt()}%"
    ) : ExportState
    data class Success(
        val outputPath: String,
        val fileSizeFormatted: String,
        val durationMs: Long
    ) : ExportState
    data class Error(val message: String) : ExportState
}

data class ExportConfig(
    val resolution: Resolution = Resolution.FHD_1080P,
    val frameRate: Int = 30,
    val customBitrateMbps: Int = 14,
    val includeWatermark: Boolean = false,
    val outputFormat: String = "MP4 (H.264 / AAC)"
)
