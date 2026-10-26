package com.novacut.editor.domain.repository

import com.novacut.editor.domain.model.ExportConfig
import com.novacut.editor.domain.model.ExportState
import com.novacut.editor.domain.model.Project
import kotlinx.coroutines.flow.Flow

interface ExportRepository {
    fun exportVideo(project: Project, config: ExportConfig): Flow<ExportState>
    fun cancelExport()
}
