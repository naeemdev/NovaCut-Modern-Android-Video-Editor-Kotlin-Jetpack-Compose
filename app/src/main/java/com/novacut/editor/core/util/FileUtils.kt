package com.novacut.editor.core.util

import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileUtils {
    /**
     * Creates an output MP4 file in the public Movies / NovaCut directory or app-specific external files.
     */
    fun createExportOutputFile(context: Context, projectName: String): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val sanitizedName = projectName.replace("[^a-zA-Z0-9_-]".toRegex(), "_")
        val fileName = "${sanitizedName}_$timestamp.mp4"

        val exportDir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_MOVIES),
            "Exports"
        ).apply {
            if (!exists()) mkdirs()
        }

        return File(exportDir, fileName)
    }

    /**
     * Formats bytes to human-readable MB / KB string
     */
    fun formatFileSize(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 * 1024 -> String.format(Locale.US, "%.2f GB", bytes / (1024f * 1024f * 1024f))
            bytes >= 1024 * 1024 -> String.format(Locale.US, "%.1f MB", bytes / (1024f * 1024f))
            bytes >= 1024 -> String.format(Locale.US, "%.1f KB", bytes / 1024f)
            else -> "$bytes B"
        }
    }

    /**
     * Copies content URI or any streamable URI to local cacheDir for reliable offline playback & transformation
     */
    fun copyUriToLocalCache(context: Context, uri: Uri, prefix: String = "imported_media"): String {
        try {
            val extension = try {
                context.contentResolver.getType(uri)?.substringAfterLast('/') ?: "mp4"
            } catch (_: Exception) {
                "mp4"
            }
            val targetFile = File(context.cacheDir, "${prefix}_${System.currentTimeMillis()}.$extension")
            context.contentResolver.openInputStream(uri)?.use { input ->
                java.io.FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }
            if (targetFile.exists() && targetFile.length() > 0L) {
                return Uri.fromFile(targetFile).toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return uri.toString()
    }

    /**
     * Extracts exact media duration in milliseconds using MediaMetadataRetriever
     */
    fun getVideoDurationMs(context: Context, uriString: String): Long {
        var retriever: android.media.MediaMetadataRetriever? = null
        try {
            retriever = android.media.MediaMetadataRetriever()
            if (uriString.startsWith("file://")) {
                val path = Uri.parse(uriString).path
                if (path != null) {
                    retriever.setDataSource(path)
                } else {
                    retriever.setDataSource(context, Uri.parse(uriString))
                }
            } else {
                retriever.setDataSource(context, Uri.parse(uriString))
            }
            val durationStr = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)
            val dur = durationStr?.toLongOrNull()
            if (dur != null && dur > 0L) {
                return dur
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                retriever?.release()
            } catch (_: Exception) {}
        }
        return 8000L
    }
}
