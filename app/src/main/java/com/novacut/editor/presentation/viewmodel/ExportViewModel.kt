package com.novacut.editor.presentation.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novacut.editor.core.di.ServiceLocator
import com.novacut.editor.domain.model.ExportConfig
import com.novacut.editor.domain.model.ExportState
import com.novacut.editor.domain.model.Project
import com.novacut.editor.domain.usecase.export.CancelExportUseCase
import com.novacut.editor.domain.usecase.export.ExportVideoUseCase
import com.novacut.editor.domain.usecase.project.GetProjectByIdUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class ExportUiState(
    val project: Project? = null,
    val config: ExportConfig = ExportConfig(),
    val exportState: ExportState = ExportState.Idle,
    val isExporting: Boolean = false
)

class ExportViewModel(
    private val getProjectByIdUseCase: GetProjectByIdUseCase = ServiceLocator.getProjectByIdUseCase,
    private val exportVideoUseCase: ExportVideoUseCase = ServiceLocator.exportVideoUseCase,
    private val cancelExportUseCase: CancelExportUseCase = ServiceLocator.cancelExportUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExportUiState())
    val uiState = _uiState.asStateFlow()

    private var exportJob: Job? = null

    fun loadProject(projectId: String) {
        viewModelScope.launch {
            val proj = getProjectByIdUseCase(projectId).firstOrNull()
            _uiState.update { it.copy(project = proj) }
        }
    }

    fun updateConfig(config: ExportConfig) {
        _uiState.update { it.copy(config = config) }
    }

    fun startExport() {
        val proj = _uiState.value.project ?: return
        val cfg = _uiState.value.config

        exportJob?.cancel()
        exportJob = viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true) }
            exportVideoUseCase(proj, cfg).collect { state ->
                _uiState.update {
                    it.copy(
                        exportState = state,
                        isExporting = state is ExportState.Rendering || state is ExportState.Preparing
                    )
                }
            }
        }
    }

    fun cancelExport() {
        exportJob?.cancel()
        cancelExportUseCase()
        _uiState.update {
            it.copy(
                exportState = ExportState.Idle,
                isExporting = false
            )
        }
    }

    fun shareExportedVideo(context: Context, filePath: String) {
        try {
            val file = File(filePath)
            if (!file.exists()) return

            val uri: Uri = try {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            } catch (e: Exception) {
                Uri.fromFile(file)
            }

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "video/mp4"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Video"))
        } catch (e: Exception) {
            // Ignore
        }
    }
}
