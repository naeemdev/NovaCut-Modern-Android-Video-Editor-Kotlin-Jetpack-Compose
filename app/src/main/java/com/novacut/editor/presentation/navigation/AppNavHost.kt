package com.novacut.editor.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.novacut.editor.presentation.ui.audioextract.AudioExtractScreen
import com.novacut.editor.presentation.ui.autocut.AutoCutScreen
import com.novacut.editor.presentation.ui.camera.CameraScreen
import com.novacut.editor.presentation.ui.captions.CaptionsScreen
import com.novacut.editor.presentation.ui.editor.EditorScreen
import com.novacut.editor.presentation.ui.export.ExportScreen
import com.novacut.editor.presentation.ui.home.HomeScreen
import com.novacut.editor.presentation.ui.settings.SettingsScreen
import com.novacut.editor.presentation.viewmodel.EditorViewModel
import com.novacut.editor.presentation.viewmodel.ExportViewModel
import com.novacut.editor.presentation.viewmodel.ProjectsViewModel

@Composable
fun AppNavHost(
    initialKey: NavKey = NavKey.Home,
    modifier: Modifier = Modifier
) {
    val backStack = remember { mutableStateListOf<NavKey>(initialKey) }
    val currentKey = backStack.lastOrNull() ?: NavKey.Home
    BackHandler(enabled = backStack.size > 1) {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    AnimatedContent(
        targetState = currentKey,
        transitionSpec = {
            if (targetState is NavKey.Home) {
                (slideInHorizontally { -it } + fadeIn()) togetherWith (slideOutHorizontally { it } + fadeOut())
            } else {
                (slideInHorizontally { it } + fadeIn()) togetherWith (slideOutHorizontally { -it } + fadeOut())
            }
        },
        label = "Nav3Display",
        modifier = modifier.fillMaxSize()
    ) { key ->
        when (key) {
            is NavKey.Home -> {
                val projectsViewModel: ProjectsViewModel = viewModel()
                HomeScreen(
                    viewModel = projectsViewModel,
                    onNavigateToEditor = { projectId ->
                        backStack.add(NavKey.Editor(projectId))
                    },
                    onNavigateToSettings = {
                        backStack.add(NavKey.Settings)
                    },
                    onNavigateToCamera = {
                        backStack.add(NavKey.Camera)
                    },
                    onNavigateToAutoCut = {
                        backStack.add(NavKey.AutoCut)
                    },
                    onNavigateToCaptions = {
                        backStack.add(NavKey.Captions)
                    },
                    onNavigateToAudioExtract = {
                        backStack.add(NavKey.AudioExtract)
                    }
                )
            }

            is NavKey.Settings -> {
                SettingsScreen(
                    onBackClick = {
                        if (backStack.size > 1) {
                            backStack.removeAt(backStack.lastIndex)
                        }
                    }
                )
            }

            is NavKey.Camera -> {
                val projectsViewModel: ProjectsViewModel = viewModel()
                CameraScreen(
                    onClose = {
                        if (backStack.size > 1) {
                            backStack.removeAt(backStack.lastIndex)
                        }
                    },
                    onRecordingFinished = { videoUri, durationMs ->
                        projectsViewModel.createProjectWithVideo(
                            uri = videoUri,
                            name = "Camera Clip",
                            durationMs = durationMs
                        ) { newProjectId ->
                            if (backStack.size > 1) {
                                backStack.removeAt(backStack.lastIndex)
                            }
                            backStack.add(NavKey.Editor(newProjectId))
                        }
                    }
                )
            }

            is NavKey.AutoCut -> {
                val projectsViewModel: ProjectsViewModel = viewModel()
                AutoCutScreen(
                    onBackClick = {
                        if (backStack.size > 1) {
                            backStack.removeAt(backStack.lastIndex)
                        }
                    },
                    onOpenInEditor = {
                        projectsViewModel.createProjectWithTemplate("AutoCut Sync") { newProjectId ->
                            if (backStack.size > 1) {
                                backStack.removeAt(backStack.lastIndex)
                            }
                            backStack.add(NavKey.Editor(newProjectId))
                        }
                    }
                )
            }

            is NavKey.Captions -> {
                val projectsViewModel: ProjectsViewModel = viewModel()
                CaptionsScreen(
                    onBackClick = {
                        if (backStack.size > 1) {
                            backStack.removeAt(backStack.lastIndex)
                        }
                    },
                    onCaptionsGenerated = {
                        projectsViewModel.createProjectWithTemplate("AI Auto Captions") { newProjectId ->
                            if (backStack.size > 1) {
                                backStack.removeAt(backStack.lastIndex)
                            }
                            backStack.add(NavKey.Editor(newProjectId))
                        }
                    }
                )
            }

            is NavKey.AudioExtract -> {
                val projectsViewModel: ProjectsViewModel = viewModel()
                AudioExtractScreen(
                    onBackClick = {
                        if (backStack.size > 1) {
                            backStack.removeAt(backStack.lastIndex)
                        }
                    },
                    onAudioExtracted = { name, durationMs ->
                        projectsViewModel.createNewProject { newProjectId ->
                            if (backStack.size > 1) {
                                backStack.removeAt(backStack.lastIndex)
                            }
                            backStack.add(NavKey.Editor(newProjectId))
                        }
                    }
                )
            }

            is NavKey.Editor -> {
                val editorViewModel: EditorViewModel = viewModel()
                EditorScreen(
                    projectId = key.projectId,
                    viewModel = editorViewModel,
                    onBackClick = {
                        if (backStack.size > 1) {
                            backStack.removeAt(backStack.lastIndex)
                        }
                    },
                    onExportClick = { projectId ->
                        backStack.add(NavKey.Export(projectId))
                    }
                )
            }

            is NavKey.Export -> {
                val exportViewModel: ExportViewModel = viewModel()
                ExportScreen(
                    projectId = key.projectId,
                    viewModel = exportViewModel,
                    onBackClick = {
                        if (backStack.size > 1) {
                            backStack.removeAt(backStack.lastIndex)
                        }
                    }
                )
            }
        }
    }
}
