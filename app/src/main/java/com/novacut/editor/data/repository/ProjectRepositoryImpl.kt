package com.novacut.editor.data.repository

import com.novacut.editor.data.datasource.ProjectLocalDataSource
import com.novacut.editor.domain.model.Project
import com.novacut.editor.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class ProjectRepositoryImpl(
    private val localDataSource: ProjectLocalDataSource
) : ProjectRepository {

    override fun getProjects(): Flow<List<Project>> {
        return localDataSource.projectsFlow
    }

    override fun getProjectById(id: String): Flow<Project?> {
        return localDataSource.projectsFlow.map { list ->
            list.find { it.id == id }
        }
    }

    override suspend fun saveProject(project: Project): Result<Unit> {
        return try {
            localDataSource.saveProject(project)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProject(id: String): Result<Unit> {
        return try {
            localDataSource.deleteProject(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun duplicateProject(id: String): Result<Project> {
        val existing = localDataSource.getProjectById(id)
            ?: return Result.failure(IllegalArgumentException("Project not found: $id"))

        val duplicated = existing.copy(
            id = UUID.randomUUID().toString(),
            title = "${existing.title} (Copy)",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return try {
            localDataSource.saveProject(duplicated)
            Result.success(duplicated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
