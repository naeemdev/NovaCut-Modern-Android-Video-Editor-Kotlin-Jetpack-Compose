package com.novacut.editor.core.util

import java.util.Locale

object TimeFormatter {
    /**
     * Formats milliseconds to "MM:SS.ms" (e.g., 01:23.45)
     */
    fun formatTimecode(timeMs: Long): String {
        val safeTime = if (timeMs < 0) 0L else timeMs
        val minutes = (safeTime / 1000) / 60
        val seconds = (safeTime / 1000) % 60
        val hundredths = (safeTime % 1000) / 10
        return String.format(Locale.US, "%02d:%02d.%02d", minutes, seconds, hundredths)
    }

    /**
     * Formats milliseconds to "MM:SS" (e.g., 01:23)
     */
    fun formatShortTime(timeMs: Long): String {
        val safeTime = if (timeMs < 0) 0L else timeMs
        val minutes = (safeTime / 1000) / 60
        val seconds = (safeTime / 1000) % 60
        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }
}
