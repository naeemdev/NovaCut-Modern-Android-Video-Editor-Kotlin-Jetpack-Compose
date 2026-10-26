package com.novacut.editor.domain.usecase.project

import com.novacut.editor.domain.model.Project
import com.novacut.editor.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow

class GetProjectsUseCase(
    private val repository: ProjectRepository
) {
    operator fun invoke(): Flow<List<Project>> = repository.getProjects()
}

class GetProjectByIdUseCase(
    private val repository: ProjectRepository
) {
    operator fun invoke(projectId: String): Flow<Project?> = repository.getProjectById(projectId)
}

class CreateProjectUseCase(
    private val repository: ProjectRepository
) {
    suspend operator fun invoke(initialTitle: String = "Nova Project"): Project {
        val newProject = Project(title = initialTitle)
        repository.saveProject(newProject)
        return newProject
    }
}

class SaveProjectUseCase(
    private val repository: ProjectRepository
) {
    suspend operator fun invoke(project: Project): Result<Unit> {
        val updated = project.copy(updatedAt = System.currentTimeMillis())
        return repository.saveProject(updated)
    }
}

class DeleteProjectUseCase(
    private val repository: ProjectRepository
) {
    suspend operator fun invoke(projectId: String): Result<Unit> = repository.deleteProject(projectId)
}

class DuplicateProjectUseCase(
    private val repository: ProjectRepository
) {
    suspend operator fun invoke(projectId: String): Result<Project> = repository.duplicateProject(projectId)
}
