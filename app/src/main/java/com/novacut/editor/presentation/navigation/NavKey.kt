package com.novacut.editor.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface NavKey {
    @Serializable
    data object Home : NavKey

    @Serializable
    data class Editor(val projectId: String) : NavKey

    @Serializable
    data class Export(val projectId: String) : NavKey

    @Serializable
    data object Settings : NavKey

    @Serializable
    data object Camera : NavKey

    @Serializable
    data object AutoCut : NavKey

    @Serializable
    data object Captions : NavKey

    @Serializable
    data object AudioExtract : NavKey
}
