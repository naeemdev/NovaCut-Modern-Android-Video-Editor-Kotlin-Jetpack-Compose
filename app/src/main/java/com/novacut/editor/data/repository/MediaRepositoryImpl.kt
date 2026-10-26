package com.novacut.editor.data.repository

import com.novacut.editor.data.datasource.MediaLocalDataSource
import com.novacut.editor.domain.model.AudioClip
import com.novacut.editor.domain.model.VideoClip
import com.novacut.editor.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow

class MediaRepositoryImpl(
    private val localDataSource: MediaLocalDataSource
) : MediaRepository {

    override fun getLocalVideos(): Flow<List<VideoClip>> {
        return localDataSource.queryDeviceVideos()
    }

    override fun getLocalAudios(): Flow<List<AudioClip>> {
        return localDataSource.queryDeviceAudio()
    }

    override fun getPresetSoundtracks(): List<AudioClip> {
        return localDataSource.getBuiltinPresets()
    }
}
