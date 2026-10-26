package com.novacut.editor.data.datasource

import android.content.Context
import com.novacut.editor.domain.model.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class ProjectLocalDataSource(
    private val context: Context
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val projectsDir: File by lazy {
        File(context.filesDir, "projects").apply {
            if (!exists()) mkdirs()
        }
    }

    private val _projectsFlow = MutableStateFlow<List<Project>>(emptyList())
    val projectsFlow: Flow<List<Project>> = _projectsFlow.asStateFlow()

    init {
        loadAllProjectsSync()
    }

    private fun loadAllProjectsSync() {
        val files = projectsDir.listFiles { _, name -> name.endsWith(".json") } ?: emptyArray()
        val loaded = files.mapNotNull { file ->
            try {
                val content = file.readText()
                json.decodeFromString<Project>(content)
            } catch (e: Exception) {
                null
            }
        }.sortedByDescending { it.updatedAt }
        _projectsFlow.value = loaded
    }

    suspend fun saveProject(project: Project) = withContext(Dispatchers.IO) {
        val file = File(projectsDir, "${project.id}.json")
        val content = json.encodeToString(project)
        file.writeText(content)

        val current = _projectsFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == project.id }
        if (index >= 0) {
            current[index] = project
        } else {
            current.add(0, project)
        }
        _projectsFlow.value = current.sortedByDescending { it.updatedAt }
    }

    suspend fun deleteProject(projectId: String) = withContext(Dispatchers.IO) {
        val file = File(projectsDir, "$projectId.json")
        if (file.exists()) file.delete()
        _projectsFlow.value = _projectsFlow.value.filterNot { it.id == projectId }
    }

    fun getProjectById(projectId: String): Project? {
        return _projectsFlow.value.find { it.id == projectId }
    }
}
