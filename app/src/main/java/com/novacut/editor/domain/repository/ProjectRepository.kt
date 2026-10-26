package com.novacut.editor.domain.repository

import com.novacut.editor.domain.model.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun getProjects(): Flow<List<Project>>
    fun getProjectById(id: String): Flow<Project?>
    suspend fun saveProject(project: Project): Result<Unit>
    suspend fun deleteProject(id: String): Result<Unit>
    suspend fun duplicateProject(id: String): Result<Project>
}
