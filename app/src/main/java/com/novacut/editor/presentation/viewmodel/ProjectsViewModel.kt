package com.novacut.editor.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novacut.editor.core.di.ServiceLocator
import com.novacut.editor.domain.model.CanvasAspectRatio
import com.novacut.editor.domain.model.Project
import com.novacut.editor.domain.model.VideoClip
import com.novacut.editor.domain.usecase.project.CreateProjectUseCase
import com.novacut.editor.domain.usecase.project.DeleteProjectUseCase
import com.novacut.editor.domain.usecase.project.DuplicateProjectUseCase
import com.novacut.editor.domain.usecase.project.GetProjectsUseCase
import com.novacut.editor.domain.usecase.project.SaveProjectUseCase
import com.novacut.editor.core.util.SampleVideoProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class ProjectsUiState(
    val projects: List<Project> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ProjectsViewModel(
    private val getProjectsUseCase: GetProjectsUseCase = ServiceLocator.getProjectsUseCase,
    private val createProjectUseCase: CreateProjectUseCase = ServiceLocator.createProjectUseCase,
    private val saveProjectUseCase: SaveProjectUseCase = ServiceLocator.saveProjectUseCase,
    private val deleteProjectUseCase: DeleteProjectUseCase = ServiceLocator.deleteProjectUseCase,
    private val duplicateProjectUseCase: DuplicateProjectUseCase = ServiceLocator.duplicateProjectUseCase
) : ViewModel() {

    val uiState: StateFlow<List<Project>> = getProjectsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isCreating = MutableStateFlow(false)
    val isCreating = _isCreating.asStateFlow()

    init {
        // If projects list is completely empty, create a starter welcome demo project
        viewModelScope.launch {
            getProjectsUseCase().collect { list ->
                if (list.isEmpty()) {
                    createDemoStarterProject()
                }
            }
        }
    }

    private suspend fun createDemoStarterProject() {
        val context = ServiceLocator.context
        val demoProject = Project(
            id = UUID.randomUUID().toString(),
            title = "Cinematic Nova Edit",
            aspectRatio = CanvasAspectRatio.RATIO_9_16,
            videoClips = listOf(
                VideoClip(
                    id = UUID.randomUUID().toString(),
                    uri = SampleVideoProvider.getSampleVideoUri(context, "sample_video.mp4"),
                    name = "Clip 01 - Intro",
                    sourceDurationMs = 8000L,
                    trimStartMs = 0L,
                    trimEndMs = 5000L
                ),
                VideoClip(
                    id = UUID.randomUUID().toString(),
                    uri = SampleVideoProvider.getSampleVideoUri(context, "sample_clip_2.mp4"),
                    name = "Clip 02 - Action",
                    sourceDurationMs = 10000L,
                    trimStartMs = 0L,
                    trimEndMs = 6000L
                )
            )
        )
        saveProjectUseCase(demoProject)
    }

    fun createNewProject(onCreated: (String) -> Unit) {
        viewModelScope.launch {
            _isCreating.value = true
            val project = createProjectUseCase("Project ${System.currentTimeMillis() % 10000}")
            _isCreating.value = false
            onCreated(project.id)
        }
    }

    fun createProjectWithVideo(uri: String, name: String, durationMs: Long, onCreated: (String) -> Unit) {
        viewModelScope.launch {
            _isCreating.value = true
            val project = createProjectUseCase("Camera Take ${System.currentTimeMillis() % 1000}")
            val clip = VideoClip(
                id = UUID.randomUUID().toString(),
                uri = uri,
                name = name,
                sourceDurationMs = durationMs
            )
            val updated = project.copy(videoClips = listOf(clip))
            saveProjectUseCase(updated)
            _isCreating.value = false
            onCreated(project.id)
        }
    }

    fun createProjectWithTemplate(templateName: String, onCreated: (String) -> Unit) {
        viewModelScope.launch {
            _isCreating.value = true
            val context = ServiceLocator.context
            val project = createProjectUseCase(templateName)
            val starterClips = listOf(
                VideoClip(
                    id = UUID.randomUUID().toString(),
                    uri = SampleVideoProvider.getSampleVideoUri(context, "sample_video.mp4"),
                    name = "Beat Scene 01",
                    sourceDurationMs = 8000L,
                    trimEndMs = 4000L
                ),
                VideoClip(
                    id = UUID.randomUUID().toString(),
                    uri = SampleVideoProvider.getSampleVideoUri(context, "sample_clip_2.mp4"),
                    name = "Beat Scene 02",
                    sourceDurationMs = 10000L,
                    trimEndMs = 5000L
                )
            )
            val updated = project.copy(videoClips = starterClips)
            saveProjectUseCase(updated)
            _isCreating.value = false
            onCreated(project.id)
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            deleteProjectUseCase(projectId)
        }
    }

    fun duplicateProject(projectId: String) {
        viewModelScope.launch {
            duplicateProjectUseCase(projectId)
        }
    }
}
