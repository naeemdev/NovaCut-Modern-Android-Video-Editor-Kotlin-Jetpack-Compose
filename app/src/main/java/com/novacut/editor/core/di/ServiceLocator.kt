package com.novacut.editor.core.di

import android.content.Context
import androidx.media3.common.util.UnstableApi
import com.novacut.editor.core.dispatchers.AppDispatchers
import com.novacut.editor.core.dispatchers.DefaultAppDispatchers
import com.novacut.editor.data.datasource.MediaLocalDataSource
import com.novacut.editor.data.datasource.ProjectLocalDataSource
import com.novacut.editor.data.engine.ExportEngine
import com.novacut.editor.data.engine.ThumbnailExtractor
import com.novacut.editor.data.repository.ExportRepositoryImpl
import com.novacut.editor.data.repository.MediaRepositoryImpl
import com.novacut.editor.data.repository.ProjectRepositoryImpl
import com.novacut.editor.data.repository.TemplateRepositoryImpl
import com.novacut.editor.domain.repository.ExportRepository
import com.novacut.editor.domain.repository.MediaRepository
import com.novacut.editor.domain.repository.ProjectRepository
import com.novacut.editor.domain.repository.TemplateRepository
import com.novacut.editor.domain.usecase.editor.AddAudioTrackUseCase
import com.novacut.editor.domain.usecase.editor.AddTextOverlayUseCase
import com.novacut.editor.domain.usecase.editor.AdjustSpeedUseCase
import com.novacut.editor.domain.usecase.editor.ApplyFilterUseCase
import com.novacut.editor.domain.usecase.editor.DeleteTextOverlayUseCase
import com.novacut.editor.domain.usecase.editor.SetAspectRatioUseCase
import com.novacut.editor.domain.usecase.editor.SplitClipUseCase
import com.novacut.editor.domain.usecase.editor.TrimClipUseCase
import com.novacut.editor.domain.usecase.editor.UpdateTextOverlayUseCase
import com.novacut.editor.domain.usecase.export.CancelExportUseCase
import com.novacut.editor.domain.usecase.export.ExportVideoUseCase
import com.novacut.editor.domain.usecase.project.CreateProjectUseCase
import com.novacut.editor.domain.usecase.project.DeleteProjectUseCase
import com.novacut.editor.domain.usecase.project.DuplicateProjectUseCase
import com.novacut.editor.domain.usecase.project.GetProjectByIdUseCase
import com.novacut.editor.domain.usecase.project.GetProjectsUseCase
import com.novacut.editor.domain.usecase.project.SaveProjectUseCase

@UnstableApi
object ServiceLocator {
    private lateinit var appContext: Context

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    val context: Context
        get() = appContext

    val dispatchers: AppDispatchers by lazy { DefaultAppDispatchers() }

    // Data Sources
    val projectDataSource: ProjectLocalDataSource by lazy {
        ProjectLocalDataSource(appContext)
    }

    val mediaDataSource: MediaLocalDataSource by lazy {
        MediaLocalDataSource(appContext)
    }

    // Engines
    val exportEngine: ExportEngine by lazy {
        ExportEngine(appContext)
    }

    val thumbnailExtractor: ThumbnailExtractor by lazy {
        ThumbnailExtractor(appContext)
    }

    // Repositories
    val projectRepository: ProjectRepository by lazy {
        ProjectRepositoryImpl(projectDataSource)
    }

    val mediaRepository: MediaRepository by lazy {
        MediaRepositoryImpl(mediaDataSource)
    }

    val templateRepository: TemplateRepository by lazy {
        TemplateRepositoryImpl()
    }

    val exportRepository: ExportRepository by lazy {
        ExportRepositoryImpl(exportEngine)
    }

    // Project Use Cases
    val getProjectsUseCase by lazy { GetProjectsUseCase(projectRepository) }
    val getProjectByIdUseCase by lazy { GetProjectByIdUseCase(projectRepository) }
    val createProjectUseCase by lazy { CreateProjectUseCase(projectRepository) }
    val saveProjectUseCase by lazy { SaveProjectUseCase(projectRepository) }
    val deleteProjectUseCase by lazy { DeleteProjectUseCase(projectRepository) }
    val duplicateProjectUseCase by lazy { DuplicateProjectUseCase(projectRepository) }

    // Editor Use Cases
    val splitClipUseCase by lazy { SplitClipUseCase() }
    val trimClipUseCase by lazy { TrimClipUseCase() }
    val applyFilterUseCase by lazy { ApplyFilterUseCase() }
    val adjustSpeedUseCase by lazy { AdjustSpeedUseCase() }
    val addTextOverlayUseCase by lazy { AddTextOverlayUseCase() }
    val updateTextOverlayUseCase by lazy { UpdateTextOverlayUseCase() }
    val deleteTextOverlayUseCase by lazy { DeleteTextOverlayUseCase() }
    val addAudioTrackUseCase by lazy { AddAudioTrackUseCase() }
    val deleteAudioTrackUseCase by lazy { com.novacut.editor.domain.usecase.editor.DeleteAudioTrackUseCase() }
    val moveAudioTrackUseCase by lazy { com.novacut.editor.domain.usecase.editor.MoveAudioTrackUseCase() }
    val setAspectRatioUseCase by lazy { SetAspectRatioUseCase() }
    val applyFaceEffectUseCase by lazy { com.novacut.editor.domain.usecase.editor.ApplyFaceEffectUseCase() }

    // Export Use Cases
    val exportVideoUseCase by lazy { ExportVideoUseCase(exportRepository) }
    val cancelExportUseCase by lazy { CancelExportUseCase(exportRepository) }
}
