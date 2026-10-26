package com.novacut.editor

import androidx.compose.ui.graphics.Color

/**
 * Whitelabel Configuration
 *
 * Centralizes all branding, customization tokens, and feature toggles.
 * Allows easy rebranding and custom theming for client deployments.
 */
object BrandConfig {
    const val APP_NAME = "NovaCut"
    const val APP_SUBTITLE = "Pro Video Studio"
    const val BRAND_TAGLINE = "Create Cinema-Grade Videos on Mobile"
    const val VERSION_NAME = "1.0.0"

    // Whitelabel Color Palette
    val PrimaryAccent = Color(0xFF00E5FF)       // Electric Cyan
    val SecondaryAccent = Color(0xFFA855F7)     // Radiant Violet
    val AccentWarning = Color(0xFFFACC15)       // Trim Handles Yellow
    val AccentDanger = Color(0xFFEF4444)        // Delete / Record Red
    val AccentSuccess = Color(0xFF10B981)       // Completed Green

    // Backgrounds & Surfaces
    val CanvasBackground = Color(0xFF0A0B0E)
    val SurfaceDark = Color(0xFF14151B)
    val SurfaceElevated = Color(0xFF1F212A)
    val BorderSubtle = Color(0xFF2B2E3B)

    // Feature Flags
    const val ENABLE_4K_EXPORT = true
    const val ENABLE_WATERMARK_OPTION = true
    const val DEFAULT_FRAME_RATE = 30
    const val DEFAULT_BITRATE_MBPS = 12
}
