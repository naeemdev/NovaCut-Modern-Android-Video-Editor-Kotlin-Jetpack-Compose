package com.novacut.editor.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novacut.editor.core.di.ServiceLocator
import com.novacut.editor.data.engine.PlaybackEngine
import com.novacut.editor.domain.model.AudioClip
import com.novacut.editor.domain.model.CanvasAspectRatio
import com.novacut.editor.domain.model.FilterType
import com.novacut.editor.domain.model.Project
import com.novacut.editor.domain.model.Resolution
import com.novacut.editor.domain.model.TextOverlay
import com.novacut.editor.domain.model.TimelineState
import com.novacut.editor.domain.model.TrackType
import com.novacut.editor.domain.model.VideoClip
import com.novacut.editor.domain.usecase.editor.AddAudioTrackUseCase
import com.novacut.editor.domain.usecase.editor.AddTextOverlayUseCase
import com.novacut.editor.domain.usecase.editor.AdjustSpeedUseCase
import com.novacut.editor.domain.usecase.editor.ApplyFilterUseCase
import com.novacut.editor.domain.usecase.editor.DeleteTextOverlayUseCase
import com.novacut.editor.domain.usecase.editor.SetAspectRatioUseCase
import com.novacut.editor.domain.usecase.editor.SplitClipUseCase
import com.novacut.editor.domain.usecase.editor.TrimClipUseCase
import com.novacut.editor.domain.usecase.editor.UpdateTextOverlayUseCase
import com.novacut.editor.domain.usecase.project.GetProjectByIdUseCase
import com.novacut.editor.domain.usecase.project.SaveProjectUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import com.novacut.editor.domain.model.FaceTrackingEffect
import com.novacut.editor.domain.usecase.editor.ApplyFaceEffectUseCase
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

enum class EditorPanel {
    NONE,
    EDIT,
    CROP,
    ANIMATION,
    VOLUME,
    LABELS,
    FILTERS,
    FACE_EFFECTS,
    ADJUST,
    TEXT,
    TEXT_EDIT,
    AUDIO,
    AUDIO_EDIT,
    FORMAT,
    SPEED
}

data class EditorUiState(
    val project: Project = Project(),
    val timelineState: TimelineState = TimelineState(),
    val activePanel: EditorPanel = EditorPanel.NONE,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val availableAudios: List<AudioClip> = emptyList(),
    val isMediaPickerOpen: Boolean = false,
    val isEditingText: Boolean = false,
    val selectedTextOverlay: TextOverlay? = null
)

class EditorViewModel(
    private val getProjectByIdUseCase: GetProjectByIdUseCase = ServiceLocator.getProjectByIdUseCase,
    private val saveProjectUseCase: SaveProjectUseCase = ServiceLocator.saveProjectUseCase,
    private val splitClipUseCase: SplitClipUseCase = ServiceLocator.splitClipUseCase,
    private val trimClipUseCase: TrimClipUseCase = ServiceLocator.trimClipUseCase,
    private val applyFilterUseCase: ApplyFilterUseCase = ServiceLocator.applyFilterUseCase,
    private val adjustSpeedUseCase: AdjustSpeedUseCase = ServiceLocator.adjustSpeedUseCase,
    private val addTextOverlayUseCase: AddTextOverlayUseCase = ServiceLocator.addTextOverlayUseCase,
    private val updateTextOverlayUseCase: UpdateTextOverlayUseCase = ServiceLocator.updateTextOverlayUseCase,
    private val deleteTextOverlayUseCase: DeleteTextOverlayUseCase = ServiceLocator.deleteTextOverlayUseCase,
    private val addAudioTrackUseCase: AddAudioTrackUseCase = ServiceLocator.addAudioTrackUseCase,
    private val deleteAudioTrackUseCase: com.novacut.editor.domain.usecase.editor.DeleteAudioTrackUseCase = ServiceLocator.deleteAudioTrackUseCase,
    private val moveAudioTrackUseCase: com.novacut.editor.domain.usecase.editor.MoveAudioTrackUseCase = ServiceLocator.moveAudioTrackUseCase,
    private val setAspectRatioUseCase: SetAspectRatioUseCase = ServiceLocator.setAspectRatioUseCase,
    private val applyFaceEffectUseCase: ApplyFaceEffectUseCase = ServiceLocator.applyFaceEffectUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState = _uiState.asStateFlow()

    private val undoStack = mutableListOf<Project>()
    private val redoStack = mutableListOf<Project>()

    var playbackEngine: PlaybackEngine? = null
        private set

    fun initialize(projectId: String, engine: PlaybackEngine) {
        this.playbackEngine = engine
        engine.initialize()

        viewModelScope.launch {
            val loadedProject = getProjectByIdUseCase(projectId).firstOrNull() ?: Project(id = projectId)
            _uiState.update { it.copy(project = loadedProject) }
            engine.setProject(loadedProject)

            // Select first clip by default if available
            if (loadedProject.videoClips.isNotEmpty()) {
                selectClip(loadedProject.videoClips.first().id)
            }
        }

        // Listen to playback position changes from engine
        viewModelScope.launch {
            engine.currentPositionMs.collect { posMs ->
                _uiState.update {
                    it.copy(
                        timelineState = it.timelineState.copy(currentPositionMs = posMs)
                    )
                }
            }
        }

        viewModelScope.launch {
            engine.isPlaying.collect { isPlaying ->
                _uiState.update {
                    it.copy(
                        timelineState = it.timelineState.copy(isPlaying = isPlaying)
                    )
                }
            }
        }

        // Load preset audio effects
        val presets = ServiceLocator.mediaRepository.getPresetSoundtracks()
        _uiState.update { it.copy(availableAudios = presets) }
    }

    private fun pushUndoState(project: Project) {
        undoStack.add(project)
        redoStack.clear()
        updateUndoRedoStatus()
    }

    private fun updateUndoRedoStatus() {
        _uiState.update {
            it.copy(
                canUndo = undoStack.isNotEmpty(),
                canRedo = redoStack.isNotEmpty()
            )
        }
    }

    fun undo() {
        if (undoStack.isEmpty()) return
        val previous = undoStack.removeAt(undoStack.lastIndex)
        redoStack.add(_uiState.value.project)
        applyProjectState(previous, updateUndoStack = false)
        updateUndoRedoStatus()
    }

    fun redo() {
        if (redoStack.isEmpty()) return
        val next = redoStack.removeAt(redoStack.lastIndex)
        undoStack.add(_uiState.value.project)
        applyProjectState(next, updateUndoStack = false)
        updateUndoRedoStatus()
    }

    private fun applyProjectState(newProject: Project, updateUndoStack: Boolean = true) {
        if (updateUndoStack) {
            pushUndoState(_uiState.value.project)
        }
        _uiState.update { it.copy(project = newProject) }
        playbackEngine?.setProject(newProject)

        viewModelScope.launch {
            saveProjectUseCase(newProject)
        }
    }

    // Playback Actions
    fun togglePlayPause() {
        playbackEngine?.togglePlayPause()
    }

    fun seekTo(timeMs: Long) {
        playbackEngine?.seekTo(timeMs)
    }

    fun stepFrame(forward: Boolean) {
        playbackEngine?.stepFrame(forward)
    }

    fun setZoomLevel(zoom: Float) {
        val clamped = zoom.coerceIn(0.5f, 5.0f)
        _uiState.update {
            it.copy(
                timelineState = it.timelineState.copy(zoomLevel = clamped)
            )
        }
    }

    // Clip Selection & Management
    fun selectClip(clipId: String) {
        val currentProject = _uiState.value.project
        var startMs = 0L
        for (c in currentProject.videoClips) {
            if (c.id == clipId) break
            startMs += c.timelineDurationMs
        }
        _uiState.update {
            it.copy(
                activePanel = EditorPanel.EDIT,
                timelineState = it.timelineState.copy(
                    selectedClipId = clipId,
                    selectedAudioId = null,
                    selectedTextId = null,
                    activeTrackType = TrackType.VIDEO
                )
            )
        }
        seekTo(startMs)
    }

    fun deselectAll() {
        _uiState.update {
            it.copy(
                activePanel = EditorPanel.NONE,
                selectedTextOverlay = null,
                timelineState = it.timelineState.copy(
                    selectedClipId = null,
                    selectedAudioId = null,
                    selectedTextId = null
                )
            )
        }
    }

    fun splitClipAtPlayhead() {
        val currentProject = _uiState.value.project
        val playheadTime = _uiState.value.timelineState.currentPositionMs
        val updatedProject = splitClipUseCase(currentProject, playheadTime)
        applyProjectState(updatedProject)
    }

    fun trimClip(clipId: String, newTrimStartMs: Long? = null, newTrimEndMs: Long? = null) {
        val currentProject = _uiState.value.project
        val updated = trimClipUseCase(currentProject, clipId, newTrimStartMs, newTrimEndMs)
        applyProjectState(updated)
    }

    fun deleteSelectedClip() {
        val selectedId = _uiState.value.timelineState.selectedClipId ?: return
        val currentProject = _uiState.value.project
        val filtered = currentProject.videoClips.filterNot { it.id == selectedId }
        val updated = currentProject.copy(videoClips = filtered)
        applyProjectState(updated)
        _uiState.update {
            it.copy(
                timelineState = it.timelineState.copy(selectedClipId = filtered.firstOrNull()?.id)
            )
        }
    }

    fun duplicateSelectedClip() {
        val selectedId = _uiState.value.timelineState.selectedClipId ?: return
        val currentProject = _uiState.value.project
        val clip = currentProject.videoClips.find { it.id == selectedId } ?: return
        val copy = clip.copy(id = UUID.randomUUID().toString())
        val index = currentProject.videoClips.indexOf(clip)
        val updatedClips = currentProject.videoClips.toMutableList().apply {
            add(index + 1, copy)
        }
        applyProjectState(currentProject.copy(videoClips = updatedClips))
        selectClip(copy.id)
    }

    // Filter & Adjustments
    fun applyFilter(filterType: FilterType) {
        val selectedId = _uiState.value.timelineState.selectedClipId ?: return
        val currentProject = _uiState.value.project
        val updated = applyFilterUseCase(currentProject, selectedId, filterType)
        applyProjectState(updated)
    }

    fun applyFaceEffect(faceEffect: FaceTrackingEffect) {
        val selectedId = _uiState.value.timelineState.selectedClipId ?: return
        val currentProject = _uiState.value.project
        val updated = applyFaceEffectUseCase(currentProject, selectedId, faceEffect)
        applyProjectState(updated)
    }

    fun adjustSelectedClip(brightness: Float, contrast: Float, saturation: Float) {
        val selectedId = _uiState.value.timelineState.selectedClipId ?: return
        val currentProject = _uiState.value.project
        val clip = currentProject.videoClips.find { it.id == selectedId } ?: return
        val updated = applyFilterUseCase(
            currentProject,
            selectedId,
            clip.filterType,
            brightness,
            contrast,
            saturation
        )
        applyProjectState(updated)
    }

    // Speed Controls
    fun setSpeed(speed: Float) {
        val selectedId = _uiState.value.timelineState.selectedClipId ?: return
        val currentProject = _uiState.value.project
        val updated = adjustSpeedUseCase(currentProject, selectedId, speed)
        applyProjectState(updated)
    }

    // Canvas Aspect Ratio & Format
    fun setAspectRatio(aspectRatio: CanvasAspectRatio) {
        val currentProject = _uiState.value.project
        val updated = setAspectRatioUseCase(currentProject, aspectRatio)
        applyProjectState(updated)
    }

    fun setResolution(resolution: Resolution) {
        val currentProject = _uiState.value.project
        applyProjectState(currentProject.copy(resolution = resolution))
    }

    // Text Overlays
    fun addTextOverlay(
        text: String,
        fontStyleName: String = "Display Bold",
        colorArgb: Long = 0xFFFFFFFF,
        animationType: String = "Fade",
        labelStyle: String = "Dark Pill"
    ) {
        val currentProject = _uiState.value.project
        val playhead = _uiState.value.timelineState.currentPositionMs
        val overlay = TextOverlay(
            id = UUID.randomUUID().toString(),
            text = text,
            fontStyleName = fontStyleName,
            textColorArgb = colorArgb,
            animationType = animationType,
            labelStyle = labelStyle,
            startTimelineMs = playhead,
            durationMs = 5000L
        )
        val updated = addTextOverlayUseCase(currentProject, overlay)
        applyProjectState(updated)
        _uiState.update {
            it.copy(
                selectedTextOverlay = overlay,
                isEditingText = false,
                timelineState = it.timelineState.copy(selectedTextId = overlay.id)
            )
        }
    }

    fun updateTextOverlay(overlay: TextOverlay) {
        val currentProject = _uiState.value.project
        val updated = updateTextOverlayUseCase(currentProject, overlay)
        applyProjectState(updated)
    }

    fun moveTextOverlay(overlayId: String, newXNormalized: Float, newYNormalized: Float) {
        val currentProject = _uiState.value.project
        val updatedOverlays = currentProject.textOverlays.map { overlay ->
            if (overlay.id == overlayId) {
                overlay.copy(
                    xNormalized = newXNormalized.coerceIn(0.1f, 0.9f),
                    yNormalized = newYNormalized.coerceIn(0.1f, 0.9f)
                )
            } else {
                overlay
            }
        }
        applyProjectState(currentProject.copy(textOverlays = updatedOverlays), updateUndoStack = false)
    }

    fun moveTextTimeline(overlayId: String, newStartMs: Long) {
        val currentProject = _uiState.value.project
        val updatedOverlays = currentProject.textOverlays.map { overlay ->
            if (overlay.id == overlayId) {
                overlay.copy(startTimelineMs = newStartMs.coerceAtLeast(0L))
            } else {
                overlay
            }
        }
        applyProjectState(currentProject.copy(textOverlays = updatedOverlays), updateUndoStack = false)
    }

    fun selectText(textId: String?) {
        val overlay = _uiState.value.project.textOverlays.find { it.id == textId }
        _uiState.update {
            it.copy(
                activePanel = if (textId != null) EditorPanel.TEXT_EDIT else EditorPanel.NONE,
                selectedTextOverlay = overlay,
                timelineState = it.timelineState.copy(
                    selectedTextId = textId,
                    selectedClipId = null,
                    selectedAudioId = null,
                    activeTrackType = if (textId != null) TrackType.TEXT else TrackType.VIDEO
                )
            )
        }
    }

    fun duplicateTextOverlay(overlayId: String) {
        val currentProject = _uiState.value.project
        val existing = currentProject.textOverlays.find { it.id == overlayId } ?: return
        val duplicate = existing.copy(
            id = UUID.randomUUID().toString(),
            xNormalized = (existing.xNormalized + 0.05f).coerceIn(0.1f, 0.9f),
            yNormalized = (existing.yNormalized + 0.05f).coerceIn(0.1f, 0.9f)
        )
        val updated = addTextOverlayUseCase(currentProject, duplicate)
        applyProjectState(updated)
        selectText(duplicate.id)
    }

    fun deleteTextOverlay(overlayId: String) {
        val currentProject = _uiState.value.project
        val targetId = if (overlayId.isNotBlank()) overlayId else (_uiState.value.timelineState.selectedTextId ?: currentProject.textOverlays.lastOrNull()?.id ?: return)
        val updated = deleteTextOverlayUseCase(currentProject, targetId)
        applyProjectState(updated)
        _uiState.update {
            it.copy(
                activePanel = EditorPanel.NONE,
                selectedTextOverlay = null,
                isEditingText = false,
                timelineState = it.timelineState.copy(selectedTextId = null)
            )
        }
    }

    fun rotateSelectedClip() {
        val currentProject = _uiState.value.project
        val selectedId = _uiState.value.timelineState.selectedClipId
            ?: currentProject.videoClips.firstOrNull()?.id
            ?: return
        val updatedClips = currentProject.videoClips.map { clip ->
            if (clip.id == selectedId) {
                clip.copy(rotationDegrees = (clip.rotationDegrees + 90) % 360)
            } else {
                clip
            }
        }
        applyProjectState(currentProject.copy(videoClips = updatedClips))
    }

    // Crop & Transform
    fun setClipCrop(scale: Float, panX: Float, panY: Float, preset: String) {
        val currentProject = _uiState.value.project
        val selectedId = _uiState.value.timelineState.selectedClipId
            ?: currentProject.videoClips.firstOrNull()?.id
            ?: return

        val newAspectRatio = when (preset) {
            "9:16" -> CanvasAspectRatio.RATIO_9_16
            "16:9" -> CanvasAspectRatio.RATIO_16_9
            "1:1" -> CanvasAspectRatio.RATIO_1_1
            "4:3", "4:5" -> CanvasAspectRatio.RATIO_4_5
            else -> currentProject.aspectRatio
        }

        val updatedClips = currentProject.videoClips.map { clip ->
            if (clip.id == selectedId) {
                clip.copy(scale = scale, panOffsetX = panX, panOffsetY = panY, cropPreset = preset)
            } else {
                clip
            }
        }
        applyProjectState(currentProject.copy(videoClips = updatedClips, aspectRatio = newAspectRatio))
    }

    // Clip In-Animation
    fun setClipAnimation(animation: String) {
        val selectedId = _uiState.value.timelineState.selectedClipId ?: return
        val currentProject = _uiState.value.project
        val updatedClips = currentProject.videoClips.map { clip ->
            if (clip.id == selectedId) {
                clip.copy(animation = animation)
            } else {
                clip
            }
        }
        applyProjectState(currentProject.copy(videoClips = updatedClips))
    }

    // Clip Volume & Mute
    fun setClipVolume(volume: Float) {
        val selectedId = _uiState.value.timelineState.selectedClipId ?: return
        val currentProject = _uiState.value.project
        val updatedClips = currentProject.videoClips.map { clip ->
            if (clip.id == selectedId) {
                clip.copy(volume = volume)
            } else {
                clip
            }
        }
        applyProjectState(currentProject.copy(videoClips = updatedClips))
        playbackEngine?.updateClipAudio(volume, false)
    }

    fun toggleClipMute() {
        val selectedId = _uiState.value.timelineState.selectedClipId ?: return
        val currentProject = _uiState.value.project
        val clip = currentProject.videoClips.find { it.id == selectedId } ?: return
        val newMute = !clip.isMuted
        val updatedClips = currentProject.videoClips.map { c ->
            if (c.id == selectedId) {
                c.copy(isMuted = newMute)
            } else {
                c
            }
        }
        applyProjectState(currentProject.copy(videoClips = updatedClips))
        playbackEngine?.updateClipAudio(clip.volume, newMute)
    }

    // Viral Labels & Stickers
    fun addLabelOverlay(label: String, style: String, animation: String) {
        val currentProject = _uiState.value.project
        val playhead = _uiState.value.timelineState.currentPositionMs
        val overlay = TextOverlay(
            id = UUID.randomUUID().toString(),
            text = label,
            fontStyleName = "Display Bold",
            textColorArgb = if (style == "Golden Badge") 0xFFFBBF24 else 0xFFFFFFFF,
            animationType = animation,
            labelStyle = style,
            startTimelineMs = playhead,
            durationMs = 4000L
        )
        val updated = addTextOverlayUseCase(currentProject, overlay)
        applyProjectState(updated)
        _uiState.update {
            it.copy(
                activePanel = EditorPanel.NONE,
                selectedTextOverlay = overlay,
                timelineState = it.timelineState.copy(selectedTextId = overlay.id)
            )
        }
    }

    // Audio Tracks
    fun selectAudio(audioId: String?) {
        _uiState.update {
            it.copy(
                activePanel = if (audioId != null) EditorPanel.AUDIO_EDIT else EditorPanel.NONE,
                timelineState = it.timelineState.copy(
                    selectedAudioId = audioId,
                    selectedClipId = null,
                    selectedTextId = null,
                    activeTrackType = if (audioId != null) TrackType.AUDIO else TrackType.VIDEO
                )
            )
        }
    }

    fun deleteAudioTrack(audioId: String) {
        val currentProject = _uiState.value.project
        val targetId = if (audioId.isNotBlank()) audioId else (_uiState.value.timelineState.selectedAudioId ?: currentProject.audioClips.lastOrNull()?.id ?: return)
        val updated = deleteAudioTrackUseCase(currentProject, targetId)
        applyProjectState(updated)
        _uiState.update {
            it.copy(
                activePanel = EditorPanel.NONE,
                timelineState = it.timelineState.copy(selectedAudioId = null)
            )
        }
    }

    fun moveAudioTrack(audioId: String, newStartMs: Long) {
        val currentProject = _uiState.value.project
        val updated = moveAudioTrackUseCase(currentProject, audioId, newStartMs)
        applyProjectState(updated, updateUndoStack = false)
    }

    fun addAudioTrack(audioClip: AudioClip) {
        val currentProject = _uiState.value.project
        val playhead = _uiState.value.timelineState.currentPositionMs
        val positioned = audioClip.copy(
            id = UUID.randomUUID().toString(),
            startTimelineMs = playhead
        )
        val updated = addAudioTrackUseCase(currentProject, positioned)
        applyProjectState(updated)
        selectAudio(positioned.id)
    }

    // Media Picker / Import Video
    fun addVideoClipFromPicker(uri: String, name: String, durationMs: Long) {
        val currentProject = _uiState.value.project
        val newClip = VideoClip(
            id = UUID.randomUUID().toString(),
            uri = uri,
            name = name,
            sourceDurationMs = durationMs
        )
        val updated = currentProject.copy(videoClips = currentProject.videoClips + newClip)
        applyProjectState(updated)
        selectClip(newClip.id)
        _uiState.update { it.copy(isMediaPickerOpen = false) }
    }

    // Active Toolbar Panel
    fun setActivePanel(panel: EditorPanel) {
        _uiState.update {
            it.copy(activePanel = if (it.activePanel == panel) EditorPanel.NONE else panel)
        }
    }

    fun setMediaPickerOpen(open: Boolean) {
        _uiState.update { it.copy(isMediaPickerOpen = open) }
    }

    fun setTextEditingModalOpen(open: Boolean, overlay: TextOverlay? = null) {
        _uiState.update {
            it.copy(
                isEditingText = open,
                selectedTextOverlay = overlay ?: it.selectedTextOverlay,
                timelineState = if (overlay != null) it.timelineState.copy(selectedTextId = overlay.id) else it.timelineState
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        playbackEngine?.release()
    }
}
