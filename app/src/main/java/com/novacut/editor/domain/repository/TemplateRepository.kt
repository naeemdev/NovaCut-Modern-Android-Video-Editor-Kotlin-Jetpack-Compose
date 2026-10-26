package com.novacut.editor.domain.repository

import com.novacut.editor.domain.model.VideoTemplate
import kotlinx.coroutines.flow.Flow

interface TemplateRepository {
    fun getTemplates(category: String? = null): Flow<List<VideoTemplate>>
    fun getTemplateById(templateId: String): Flow<VideoTemplate?>
}
