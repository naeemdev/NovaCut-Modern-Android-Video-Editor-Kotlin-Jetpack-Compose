package com.novacut.editor.presentation.ui.editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.novacut.editor.data.engine.PlaybackEngine
import com.novacut.editor.domain.model.CanvasAspectRatio
import com.novacut.editor.domain.model.FaceTrackingEffect
import com.novacut.editor.domain.model.FilterType
import com.novacut.editor.domain.model.Project
import com.novacut.editor.domain.model.Resolution
import com.novacut.editor.domain.model.VideoClip
import com.novacut.editor.presentation.ui.components.MediaPickerSheet
import com.novacut.editor.presentation.ui.editor.components.PlaybackControls
import com.novacut.editor.presentation.ui.editor.components.TopNavigationBar
import com.novacut.editor.presentation.ui.editor.components.VideoPreviewCanvas
import com.novacut.editor.presentation.ui.editor.components.timeline.MultiTrackTimeline
import com.novacut.editor.presentation.ui.editor.components.toolbars.AdjustPanel
import com.novacut.editor.presentation.ui.editor.components.toolbars.AudioPanel
import com.novacut.editor.presentation.ui.editor.components.toolbars.ClipAnimationPanel
import com.novacut.editor.presentation.ui.editor.components.toolbars.CropPanel
import com.novacut.editor.presentation.ui.editor.components.toolbars.EditSubToolbar
import com.novacut.editor.presentation.ui.editor.components.toolbars.FaceEffectsPanel
import com.novacut.editor.presentation.ui.editor.components.toolbars.FiltersPanel
import com.novacut.editor.presentation.ui.editor.components.toolbars.FormatPanel
import com.novacut.editor.presentation.ui.editor.components.toolbars.LabelsPanel
import com.novacut.editor.presentation.ui.editor.components.toolbars.MainBottomBar
import com.novacut.editor.presentation.ui.editor.components.toolbars.SpeedPanel
import com.novacut.editor.presentation.ui.editor.components.toolbars.TextEditorSheet
import com.novacut.editor.presentation.ui.editor.components.toolbars.AudioSubToolbar
import com.novacut.editor.presentation.ui.editor.components.toolbars.TextSubToolbar
import com.novacut.editor.presentation.ui.editor.components.toolbars.VolumePanel
import com.novacut.editor.presentation.viewmodel.EditorPanel
import com.novacut.editor.presentation.viewmodel.EditorUiState
import com.novacut.editor.presentation.viewmodel.EditorViewModel
import com.novacut.editor.theme.NovaCutTheme
import com.novacut.editor.theme.StudioDarkBg

@Composable
fun EditorScreen(
    projectId: String,
    viewModel: EditorViewModel,
    onBackClick: () -> Unit,
    onExportClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val playbackEngine = remember { PlaybackEngine(context, scope) }

    LaunchedEffect(projectId) {
        viewModel.initialize(projectId, playbackEngine)
    }

    EditorContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onExportClick = { onExportClick(projectId) },
        onUndoClick = { viewModel.undo() },
        onRedoClick = { viewModel.redo() },
        onResolutionChange = { res -> viewModel.setResolution(res) },
        onTogglePlayPause = { viewModel.togglePlayPause() },
        onStepFrame = { forward -> viewModel.stepFrame(forward) },
        onSeekTo = { timeMs -> viewModel.seekTo(timeMs) },
        onClipClick = { clipId -> viewModel.selectClip(clipId) },
        onAudioClick = { audioId -> viewModel.selectAudio(audioId) },
        onAudioMove = { audioId, newStartMs -> viewModel.moveAudioTrack(audioId, newStartMs) },
        onAudioDelete = { audioId -> viewModel.deleteAudioTrack(audioId) },
        onTextClick = { overlay -> viewModel.selectText(overlay.id) },
        onEditTextClick = { overlay -> viewModel.setTextEditingModalOpen(true, overlay) },
        onDeleteTextClick = { overlayId -> viewModel.deleteTextOverlay(overlayId) },
        onDuplicateTextClick = { overlayId -> viewModel.duplicateTextOverlay(overlayId) },
        onTextTimelineMove = { overlayId, newStartMs -> viewModel.moveTextTimeline(overlayId, newStartMs) },
        onToolClick = { panel -> viewModel.setActivePanel(panel) },
        onSplitClick = { viewModel.splitClipAtPlayhead() },
        onSpeedClick = { viewModel.setActivePanel(EditorPanel.SPEED) },
        onCropClick = { viewModel.setActivePanel(EditorPanel.CROP) },
        onAnimationClick = { viewModel.setActivePanel(EditorPanel.ANIMATION) },
        onVolumeClick = { viewModel.setActivePanel(EditorPanel.VOLUME) },
        onDuplicateClick = { viewModel.duplicateSelectedClip() },
        onDeleteClick = { viewModel.deleteSelectedClip() },
        onFilterSelected = { filter -> viewModel.applyFilter(filter) },
        onFaceEffectSelected = { effect -> viewModel.applyFaceEffect(effect) },
        onAdjustChange = { b, c, s -> viewModel.adjustSelectedClip(b, c, s) },
        onRatioSelected = { ratio -> viewModel.setAspectRatio(ratio) },
        onSpeedChange = { speed -> viewModel.setSpeed(speed) },
        onCropChange = { scale, panX, panY, preset ->
            viewModel.setClipCrop(scale, panX, panY, preset)
        },
        onClipAnimationSelected = { anim ->
            viewModel.setClipAnimation(anim)
        },
        onVolumeChange = { vol -> viewModel.setClipVolume(vol) },
        onToggleClipMute = { viewModel.toggleClipMute() },
        onAddLabel = { label, style, anim ->
            viewModel.addLabelOverlay(label, style, anim)
        },
        onAddAudio = { audio -> viewModel.addAudioTrack(audio) },
        onRotateClick = { viewModel.rotateSelectedClip() },
        onTextPositionChange = { id, x, y -> viewModel.moveTextOverlay(id, x, y) },
        player = playbackEngine.exoPlayer,
        onConfirmText = { text, font, color, animation, labelStyle ->
            val existing = uiState.selectedTextOverlay
            val isAlreadyInProject = existing != null && uiState.project.textOverlays.any { it.id == existing.id }
            if (isAlreadyInProject && existing != null) {
                viewModel.updateTextOverlay(
                    existing.copy(
                        text = text,
                        fontStyleName = font,
                        textColorArgb = color,
                        animationType = animation,
                        labelStyle = labelStyle
                    )
                )
            } else {
                viewModel.addTextOverlay(
                    text = text,
                    fontStyleName = font,
                    colorArgb = color,
                    animationType = animation,
                    labelStyle = labelStyle
                )
            }
            viewModel.setTextEditingModalOpen(false, null)
        },
        onCloseSubToolbar = { viewModel.setActivePanel(EditorPanel.NONE) },
        onCloseTextSheet = { viewModel.setTextEditingModalOpen(false, null) },
        onAddClip = { uri, name, duration ->
            viewModel.addVideoClipFromPicker(uri, name, duration)
        },
        onOpenMediaPicker = { viewModel.setMediaPickerOpen(true) },
        onCloseMediaPicker = { viewModel.setMediaPickerOpen(false) }
    )
}

@Composable
fun EditorContent(
    uiState: EditorUiState,
    onBackClick: () -> Unit,
    onExportClick: () -> Unit,
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    onResolutionChange: (Resolution) -> Unit,
    onTogglePlayPause: () -> Unit,
    onStepFrame: (Boolean) -> Unit,
    onSeekTo: (Long) -> Unit,
    onClipClick: (String) -> Unit,
    onAudioClick: (String) -> Unit,
    onAudioMove: (String, Long) -> Unit = { _, _ -> },
    onAudioDelete: (String) -> Unit = {},
    onTextClick: (com.novacut.editor.domain.model.TextOverlay) -> Unit,
    onEditTextClick: (com.novacut.editor.domain.model.TextOverlay) -> Unit = {},
    onDeleteTextClick: (String) -> Unit = {},
    onDuplicateTextClick: (String) -> Unit = {},
    onTextTimelineMove: (String, Long) -> Unit = { _, _ -> },
    onToolClick: (EditorPanel) -> Unit,
    onSplitClick: () -> Unit,
    onSpeedClick: () -> Unit,
    onCropClick: () -> Unit,
    onAnimationClick: () -> Unit,
    onVolumeClick: () -> Unit,
    onRotateClick: () -> Unit,
    onDuplicateClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onFilterSelected: (FilterType) -> Unit,
    onFaceEffectSelected: (FaceTrackingEffect) -> Unit,
    onAdjustChange: (Float, Float, Float) -> Unit,
    onRatioSelected: (CanvasAspectRatio) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onCropChange: (scale: Float, panX: Float, panY: Float, preset: String) -> Unit,
    onClipAnimationSelected: (String) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onToggleClipMute: () -> Unit,
    onAddLabel: (String, String, String) -> Unit,
    onAddAudio: (com.novacut.editor.domain.model.AudioClip) -> Unit,
    onTextPositionChange: (String, Float, Float) -> Unit,
    player: androidx.media3.common.Player? = null,
    onConfirmText: (String, String, Long, String, String) -> Unit,
    onCloseSubToolbar: () -> Unit,
    onCloseTextSheet: () -> Unit,
    onAddClip: (String, String, Long) -> Unit,
    onOpenMediaPicker: () -> Unit,
    onCloseMediaPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedClip = remember(uiState.project.videoClips, uiState.timelineState.selectedClipId) {
        uiState.project.videoClips.find { it.id == uiState.timelineState.selectedClipId }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Navigation Bar
            TopNavigationBar(
                title = uiState.project.title,
                currentResolution = uiState.project.resolution,
                canUndo = uiState.canUndo,
                canRedo = uiState.canRedo,
                onBackClick = onBackClick,
                onUndoClick = onUndoClick,
                onRedoClick = onRedoClick,
                onResolutionChange = onResolutionChange,
                onExportClick = onExportClick
            )

            // 2. Video Preview Canvas (Responsive framed viewport with real Media3 player)
            VideoPreviewCanvas(
                project = uiState.project,
                currentPositionMs = uiState.timelineState.currentPositionMs,
                isPlaying = uiState.timelineState.isPlaying,
                player = player,
                selectedClipId = uiState.timelineState.selectedClipId,
                selectedTextId = uiState.timelineState.selectedTextId,
                onTextOverlayClick = onTextClick,
                onEditTextClick = onEditTextClick,
                onDeleteTextClick = onDeleteTextClick,
                onTextPositionChange = onTextPositionChange,
                onAddClipClick = onOpenMediaPicker,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            // 3. Playback Controls Bar
            PlaybackControls(
                currentPositionMs = uiState.timelineState.currentPositionMs,
                totalDurationMs = uiState.project.totalDurationMs,
                isPlaying = uiState.timelineState.isPlaying,
                onTogglePlayPause = onTogglePlayPause,
                onStepFrame = onStepFrame
            )

            // 4. Multi-Track Timeline (Ruler, filmstrip, text track, audio track, playhead)
            MultiTrackTimeline(
                project = uiState.project,
                currentPositionMs = uiState.timelineState.currentPositionMs,
                zoomLevel = uiState.timelineState.zoomLevel,
                selectedClipId = uiState.timelineState.selectedClipId,
                selectedAudioId = uiState.timelineState.selectedAudioId,
                selectedTextId = uiState.timelineState.selectedTextId,
                onSeekTo = onSeekTo,
                onClipClick = onClipClick,
                onAudioClick = onAudioClick,
                onAudioMove = onAudioMove,
                onAudioDelete = onAudioDelete,
                onTextClick = onTextClick,
                onTextTimelineMove = onTextTimelineMove,
                onTextDelete = onDeleteTextClick,
                onAddClipClick = onOpenMediaPicker,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            )

            // 5. Active Panel Drawer (Opens on tool click)
            AnimatedVisibility(
                visible = uiState.activePanel != EditorPanel.NONE,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                when (uiState.activePanel) {
                    EditorPanel.EDIT -> EditSubToolbar(
                        onSplitClick = onSplitClick,
                        onSpeedClick = onSpeedClick,
                        onCropClick = onCropClick,
                        onAnimationClick = onAnimationClick,
                        onVolumeClick = onVolumeClick,
                        onRotateClick = onRotateClick,
                        onDuplicateClick = onDuplicateClick,
                        onDeleteClick = onDeleteClick,
                        onClose = onCloseSubToolbar
                    )
                    EditorPanel.TEXT_EDIT -> {
                        val textItem = uiState.selectedTextOverlay
                            ?: uiState.project.textOverlays.find { it.id == uiState.timelineState.selectedTextId }
                            ?: uiState.project.textOverlays.lastOrNull()
                        val textIdToDelete = textItem?.id ?: uiState.timelineState.selectedTextId ?: ""
                        TextSubToolbar(
                            textPreview = textItem?.text ?: "Text",
                            onEditClick = {
                                if (textItem != null) {
                                    onEditTextClick(textItem)
                                }
                            },
                            onDuplicateClick = {
                                if (textItem != null) {
                                    onDuplicateTextClick(textItem.id)
                                }
                            },
                            onDeleteClick = {
                                onDeleteTextClick(textIdToDelete)
                            },
                            onClose = onCloseSubToolbar
                        )
                    }
                    EditorPanel.AUDIO_EDIT -> {
                        val audioItem = remember(uiState.project.audioClips, uiState.timelineState.selectedAudioId) {
                            uiState.project.audioClips.find { it.id == uiState.timelineState.selectedAudioId }
                        } ?: uiState.project.audioClips.lastOrNull()
                        val audioIdToDelete = audioItem?.id ?: uiState.timelineState.selectedAudioId ?: ""
                        AudioSubToolbar(
                            audioTitle = audioItem?.title ?: "Audio",
                            onDeleteClick = {
                                onAudioDelete(audioIdToDelete)
                            },
                            onVolumeClick = {
                                onVolumeClick()
                            },
                            onClose = onCloseSubToolbar
                        )
                    }
                    EditorPanel.CROP -> CropPanel(
                        initialScale = selectedClip?.scale ?: 1.0f,
                        initialPanX = selectedClip?.panOffsetX ?: 0f,
                        initialPanY = selectedClip?.panOffsetY ?: 0f,
                        initialPreset = selectedClip?.cropPreset ?: "Free",
                        onCropChange = onCropChange,
                        onClose = onCloseSubToolbar
                    )
                    EditorPanel.ANIMATION -> ClipAnimationPanel(
                        selectedAnimation = selectedClip?.animation ?: "None",
                        onAnimationSelected = onClipAnimationSelected,
                        onClose = onCloseSubToolbar
                    )
                    EditorPanel.VOLUME -> VolumePanel(
                        initialVolume = selectedClip?.volume ?: 1.0f,
                        isMuted = selectedClip?.isMuted ?: false,
                        onVolumeChange = onVolumeChange,
                        onToggleMute = onToggleClipMute,
                        onClose = onCloseSubToolbar
                    )
                    EditorPanel.LABELS -> LabelsPanel(
                        onAddLabel = onAddLabel,
                        onClose = onCloseSubToolbar
                    )
                    EditorPanel.FILTERS -> FiltersPanel(
                        selectedFilter = selectedClip?.filterType ?: FilterType.NONE,
                        onFilterSelected = onFilterSelected,
                        onClose = onCloseSubToolbar
                    )
                    EditorPanel.FACE_EFFECTS -> FaceEffectsPanel(
                        selectedEffect = selectedClip?.faceEffect ?: FaceTrackingEffect.NONE,
                        onSelectEffect = onFaceEffectSelected,
                        onClose = onCloseSubToolbar
                    )
                    EditorPanel.ADJUST -> AdjustPanel(
                        initialBrightness = selectedClip?.brightness ?: 0f,
                        initialContrast = selectedClip?.contrast ?: 1f,
                        initialSaturation = selectedClip?.saturation ?: 1f,
                        onAdjustChange = onAdjustChange,
                        onClose = onCloseSubToolbar
                    )
                    EditorPanel.FORMAT -> FormatPanel(
                        selectedRatio = uiState.project.aspectRatio,
                        onRatioSelected = onRatioSelected,
                        onClose = onCloseSubToolbar
                    )
                    EditorPanel.SPEED -> SpeedPanel(
                        currentSpeed = selectedClip?.speed ?: 1.0f,
                        onSpeedChange = onSpeedChange,
                        onClose = onCloseSubToolbar
                    )
                    EditorPanel.AUDIO -> AudioPanel(
                        availableAudios = uiState.availableAudios,
                        onAddAudio = onAddAudio,
                        onClose = onCloseSubToolbar
                    )
                    else -> Unit
                }
            }

            // 6. Main Bottom Tool Bar Dock
            MainBottomBar(
                activePanel = uiState.activePanel,
                onToolClick = { panel ->
                    if (panel == EditorPanel.TEXT) {
                        onEditTextClick(com.novacut.editor.domain.model.TextOverlay(text = ""))
                    } else {
                        onToolClick(panel)
                    }
                }
            )
        }

        // Modal Sheets: Text Editor & Media Picker
        if (uiState.isEditingText) {
            Box(modifier = Modifier.fillMaxSize()) {
                TextEditorSheet(
                    existingOverlay = uiState.selectedTextOverlay,
                    onConfirm = onConfirmText,
                    onClose = onCloseTextSheet,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (uiState.isMediaPickerOpen) {
            Box(modifier = Modifier.fillMaxSize()) {
                MediaPickerSheet(
                    onClipSelected = onAddClip,
                    onClose = onCloseMediaPicker,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(name = "Editor Screen Studio Preview", showBackground = true, widthDp = 380, heightDp = 750)
@Composable
fun EditorScreenPreview() {
    NovaCutTheme {
        EditorContent(
            uiState = EditorUiState(
                project = Project(
                    title = "Cinematic Edit",
                    videoClips = listOf(
                        VideoClip(id = "1", uri = "", name = "Intro", sourceDurationMs = 5000L),
                        VideoClip(id = "2", uri = "", name = "Sunset Walk", sourceDurationMs = 7000L)
                    )
                ),
                activePanel = EditorPanel.NONE
            ),
            onBackClick = {},
            onExportClick = {},
            onUndoClick = {},
            onRedoClick = {},
            onResolutionChange = {},
            onTogglePlayPause = {},
            onStepFrame = {},
            onSeekTo = {},
            onClipClick = {},
            onAudioClick = {},
            onTextClick = {},
            onToolClick = {},
            onSplitClick = {},
            onSpeedClick = {},
            onCropClick = {},
            onAnimationClick = {},
            onVolumeClick = {},
            onRotateClick = {},
            onDuplicateClick = {},
            onDeleteClick = {},
            onFilterSelected = {},
            onFaceEffectSelected = {},
            onAdjustChange = { _, _, _ -> },
            onRatioSelected = {},
            onSpeedChange = {},
            onCropChange = { _, _, _, _ -> },
            onClipAnimationSelected = {},
            onVolumeChange = {},
            onToggleClipMute = {},
            onAddLabel = { _, _, _ -> },
            onAddAudio = {},
            onTextPositionChange = { _, _, _ -> },
            player = null,
            onConfirmText = { _, _, _, _, _ -> },
            onCloseSubToolbar = {},
            onCloseTextSheet = {},
            onAddClip = { _, _, _ -> },
            onOpenMediaPicker = {},
            onCloseMediaPicker = {}
        )
    }
}
