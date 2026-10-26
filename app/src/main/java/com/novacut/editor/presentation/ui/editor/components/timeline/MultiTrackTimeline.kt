package com.novacut.editor.presentation.ui.editor.components.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.novacut.editor.domain.model.AudioClip
import com.novacut.editor.domain.model.Project
import com.novacut.editor.domain.model.TextOverlay
import com.novacut.editor.domain.model.VideoClip
import com.novacut.editor.theme.NovaCutTheme
import com.novacut.editor.theme.StudioDarkBg
import com.novacut.editor.theme.StudioSurface
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun MultiTrackTimeline(
    project: Project,
    currentPositionMs: Long,
    zoomLevel: Float,
    selectedClipId: String?,
    selectedAudioId: String?,
    selectedTextId: String?,
    onSeekTo: (Long) -> Unit,
    onClipClick: (String) -> Unit,
    onAudioClick: (String) -> Unit,
    onAudioMove: (String, Long) -> Unit = { _, _ -> },
    onAudioDelete: (String) -> Unit = {},
    onTextClick: (TextOverlay) -> Unit,
    onTextTimelineMove: (String, Long) -> Unit = { _, _ -> },
    onTextDelete: (String) -> Unit = {},
    onAddClipClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val basePxPerSec = 100f
    val pxPerSec = basePxPerSec * zoomLevel
    val density = LocalDensity.current

    // Keep playhead synchronized with scroll position
    LaunchedEffect(currentPositionMs, zoomLevel) {
        val targetPx = ((currentPositionMs / 1000f) * pxPerSec).toInt()
        if (targetPx != scrollState.value && !scrollState.isScrollInProgress) {
            scrollState.scrollTo(targetPx)
        }
    }

    // When user scrolls timeline manually, seek playhead
    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.value }
            .distinctUntilChanged()
            .collect { scrollPx ->
                if (scrollState.isScrollInProgress) {
                    val timeMs = ((scrollPx / pxPerSec) * 1000f).toLong().coerceAtLeast(0L)
                    onSeekTo(timeMs)
                }
            }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .background(StudioDarkBg)
    ) {
        val screenCenterDp = maxWidth / 2
        val totalTrackWidthPx = (project.totalDurationMs / 1000f) * pxPerSec
        val totalTrackWidthDp = with(density) { totalTrackWidthPx.toDp() } + maxWidth

        // Scrollable Timeline Tracks
        Box(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .width(totalTrackWidthDp)
                    .padding(start = screenCenterDp, end = screenCenterDp)
            ) {
                // Time Ruler
                TimelineRuler(
                    totalDurationMs = project.totalDurationMs,
                    zoomLevel = zoomLevel,
                    basePxPerSec = basePxPerSec
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Video Track (filmstrip)
                VideoTrackView(
                    clips = project.videoClips,
                    selectedClipId = selectedClipId,
                    zoomLevel = zoomLevel,
                    basePxPerSec = basePxPerSec,
                    onClipClick = onClipClick,
                    onAddClipClick = onAddClipClick
                )

                // Text Track
                if (project.textOverlays.isNotEmpty()) {
                    TextTrackView(
                        textOverlays = project.textOverlays,
                        selectedTextId = selectedTextId,
                        zoomLevel = zoomLevel,
                        basePxPerSec = basePxPerSec,
                        onTextClick = onTextClick,
                        onTextTimelineMove = onTextTimelineMove,
                        onTextDelete = onTextDelete
                    )
                }

                // Audio Track
                if (project.audioClips.isNotEmpty()) {
                    AudioTrackView(
                        audioClips = project.audioClips,
                        selectedAudioId = selectedAudioId,
                        zoomLevel = zoomLevel,
                        basePxPerSec = basePxPerSec,
                        onAudioClick = onAudioClick,
                        onAudioMove = onAudioMove,
                        onAudioDelete = onAudioDelete
                    )
                }
            }
        }

        // Stationary Playhead needle at the screen center
        Playhead(
            modifier = Modifier
                .align(Alignment.TopCenter)
        )
    }
}

@Preview(name = "MultiTrack Timeline Preview", showBackground = true, widthDp = 360, heightDp = 180)
@Composable
fun MultiTrackTimelinePreview() {
    NovaCutTheme {
        MultiTrackTimeline(
            project = Project(
                videoClips = listOf(
                    VideoClip(id = "1", uri = "", name = "Scene 1", sourceDurationMs = 5000L),
                    VideoClip(id = "2", uri = "", name = "Scene 2", sourceDurationMs = 7000L)
                ),
                audioClips = listOf(
                    AudioClip(id = "a1", title = "Beats", sourceDurationMs = 12000L)
                ),
                textOverlays = listOf(
                    TextOverlay(id = "t1", text = "VIBES", startTimelineMs = 1000L, durationMs = 3000L)
                )
            ),
            currentPositionMs = 2500L,
            zoomLevel = 1.0f,
            selectedClipId = "1",
            selectedAudioId = null,
            selectedTextId = null,
            onSeekTo = {},
            onClipClick = {},
            onAudioClick = {},
            onTextClick = {}
        )
    }
}
