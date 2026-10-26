package com.novacut.editor.data.datasource

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.novacut.editor.domain.model.AudioClip
import com.novacut.editor.domain.model.VideoClip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.UUID

class MediaLocalDataSource(
    private val context: Context
) {
    fun queryDeviceVideos(): Flow<List<VideoClip>> = flow {
        val list = mutableListOf<VideoClip>()
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION
        )

        try {
            context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                "${MediaStore.Video.Media.DATE_ADDED} DESC"
            )?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val durCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val name = cursor.getString(nameCol) ?: "Video_$id"
                    val duration = cursor.getLong(durCol)
                    if (duration > 500L) {
                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            id
                        ).toString()

                        list.add(
                            VideoClip(
                                id = UUID.randomUUID().toString(),
                                uri = contentUri,
                                name = name,
                                sourceDurationMs = duration
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            // Permission not granted or query failed
        }

        emit(list)
    }.flowOn(Dispatchers.IO)

    fun queryDeviceAudio(): Flow<List<AudioClip>> = flow {
        val list = mutableListOf<AudioClip>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION
        )

        try {
            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                "${MediaStore.Audio.Media.DATE_ADDED} DESC"
            )?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val durCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val title = cursor.getString(titleCol) ?: "Audio_$id"
                    val duration = cursor.getLong(durCol)
                    if (duration > 500L) {
                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                        ).toString()

                        list.add(
                            AudioClip(
                                id = UUID.randomUUID().toString(),
                                uri = contentUri,
                                title = title,
                                sourceDurationMs = duration,
                                category = "Music"
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            // Permission or query exception
        }

        emit(list)
    }.flowOn(Dispatchers.IO)

    fun getBuiltinPresets(): List<AudioClip> {
        return listOf(
            AudioClip(
                id = "preset_cyber_pulse",
                title = "Cyber Pulse Beat",
                sourceDurationMs = 15000L,
                category = "Music"
            ),
            AudioClip(
                id = "preset_cinematic_rise",
                title = "Cinematic Rise & Impact",
                sourceDurationMs = 8000L,
                category = "SFX"
            ),
            AudioClip(
                id = "preset_ambient_glow",
                title = "Lofi Ambient Glow",
                sourceDurationMs = 24000L,
                category = "Music"
            ),
            AudioClip(
                id = "preset_whoosh_transition",
                title = "Quick Whoosh Transition",
                sourceDurationMs = 1200L,
                category = "SFX"
            ),
            AudioClip(
                id = "preset_pop_click",
                title = "Modern Pop Click",
                sourceDurationMs = 500L,
                category = "SFX"
            )
        )
    }
}
