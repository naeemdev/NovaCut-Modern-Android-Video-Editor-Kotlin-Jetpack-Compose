package com.novacut.editor.data.repository

import androidx.media3.common.util.UnstableApi
import com.novacut.editor.data.engine.ExportEngine
import com.novacut.editor.domain.model.ExportConfig
import com.novacut.editor.domain.model.ExportState
import com.novacut.editor.domain.model.Project
import com.novacut.editor.domain.repository.ExportRepository
import kotlinx.coroutines.flow.Flow

@UnstableApi
class ExportRepositoryImpl(
    private val exportEngine: ExportEngine
) : ExportRepository {

    override fun exportVideo(project: Project, config: ExportConfig): Flow<ExportState> {
        return exportEngine.exportVideo(project, config)
    }

    override fun cancelExport() {
        exportEngine.cancel()
    }
}
