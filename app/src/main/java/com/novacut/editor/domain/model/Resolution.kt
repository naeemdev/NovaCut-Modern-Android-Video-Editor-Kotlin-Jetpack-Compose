package com.novacut.editor.domain.model

enum class Resolution(
    val label: String,
    val width: Int,
    val height: Int,
    val bitrateMbps: Int
) {
    SD_720P("720p HD", 1280, 720, 8),
    FHD_1080P("1080p FHD", 1920, 1080, 14),
    QHD_1440P("2K QHD", 2560, 1440, 22),
    UHD_4K("4K UHD", 3840, 2160, 35)
}
