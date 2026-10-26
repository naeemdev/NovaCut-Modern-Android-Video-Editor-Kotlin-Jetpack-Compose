package com.novacut.editor.core.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object SampleVideoProvider {

    fun getSampleVideoFile(context: Context, filename: String = "sample_video.mp4"): File {
        val targetFile = File(context.cacheDir, filename)
        if (!targetFile.exists() || targetFile.length() == 0L) {
            try {
                context.assets.open(filename).use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                // If asset cannot be opened, create fallback placeholder file
                if (!targetFile.exists()) {
                    targetFile.createNewFile()
                }
            }
        }
        return targetFile
    }

    fun getSampleVideoUri(context: Context, filename: String = "sample_video.mp4"): String {
        val file = getSampleVideoFile(context, filename)
        return Uri.fromFile(file).toString()
    }

    fun resolvePlayableUri(context: Context, rawUri: String): String {
        if (rawUri.isBlank()) {
            return getSampleVideoUri(context, "sample_video.mp4")
        }

        // Check if rawUri is a direct file path or file URI
        val filePath = when {
            rawUri.startsWith("file://") -> Uri.parse(rawUri).path
            rawUri.startsWith("/") -> rawUri
            else -> null
        }

        if (filePath != null) {
            val file = File(filePath)
            if (file.exists() && file.length() > 0L) {
                // Real recorded or imported local video file exists! Play it directly!
                return Uri.fromFile(file).toString()
            } else {
                // Local file missing or 0 bytes: fallback to bundled asset
                return getSampleVideoUri(context, "sample_video.mp4")
            }
        }

        // Check if content URI can be opened
        if (rawUri.startsWith("content://")) {
            return try {
                context.contentResolver.openFileDescriptor(Uri.parse(rawUri), "r")?.use { pfd ->
                    if (pfd.statSize > 0L) rawUri else getSampleVideoUri(context, "sample_video.mp4")
                } ?: getSampleVideoUri(context, "sample_video.mp4")
            } catch (_: Exception) {
                getSampleVideoUri(context, "sample_video.mp4")
            }
        }

        // Handle dead external sample buckets
        if (rawUri.contains("commondatastorage.googleapis.com")) {
            return getSampleVideoUri(context, "sample_video.mp4")
        }

        return rawUri
    }
}
