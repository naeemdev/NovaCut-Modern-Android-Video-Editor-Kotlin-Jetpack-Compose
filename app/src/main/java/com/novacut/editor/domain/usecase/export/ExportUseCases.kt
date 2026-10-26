package com.novacut.editor.domain.usecase.export

import com.novacut.editor.domain.model.ExportConfig
import com.novacut.editor.domain.model.ExportState
import com.novacut.editor.domain.model.Project
import com.novacut.editor.domain.repository.ExportRepository
import kotlinx.coroutines.flow.Flow

class ExportVideoUseCase(
    private val repository: ExportRepository
) {
    operator fun invoke(project: Project, config: ExportConfig): Flow<ExportState> {
        return repository.exportVideo(project, config)
    }
}

class CancelExportUseCase(
    private val repository: ExportRepository
) {
    operator fun invoke() {
        repository.cancelExport()
    }
}
