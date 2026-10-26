package com.novacut.editor.domain.repository

import com.novacut.editor.domain.model.AudioClip
import com.novacut.editor.domain.model.VideoClip
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun getLocalVideos(): Flow<List<VideoClip>>
    fun getLocalAudios(): Flow<List<AudioClip>>
    fun getPresetSoundtracks(): List<AudioClip>
}
