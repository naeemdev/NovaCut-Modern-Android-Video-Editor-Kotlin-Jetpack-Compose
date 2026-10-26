package com.novacut.editor.presentation.ui.editor.components.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novacut.editor.domain.model.AudioClip
import com.novacut.editor.theme.NovaCutTheme
import com.novacut.editor.theme.StudioBorder
import com.novacut.editor.theme.StudioDanger
import com.novacut.editor.theme.TrackAudioBg
import com.novacut.editor.theme.TrackAudioWaveform

@Composable
fun AudioTrackView(
    audioClips: List<AudioClip>,
    selectedAudioId: String?,
    zoomLevel: Float,
    basePxPerSec: Float = 100f,
    onAudioClick: (String) -> Unit,
    onAudioMove: (audioId: String, newStartMs: Long) -> Unit = { _, _ -> },
    onAudioDelete: (audioId: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val pxPerSec = basePxPerSec * zoomLevel
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .height(36.dp)
            .padding(vertical = 2.dp)
    ) {
        audioClips.forEach { audio ->
            var dragOffsetPx by remember(audio.startTimelineMs) { mutableFloatStateOf(0f) }
            val currentStartMs = (audio.startTimelineMs + (dragOffsetPx / pxPerSec * 1000f).toLong()).coerceAtLeast(0L)
            val startOffsetPx = (currentStartMs / 1000f) * pxPerSec
            val widthPx = (audio.durationMs / 1000f) * pxPerSec
            val startOffsetDp = with(density) { startOffsetPx.toDp() }
            val widthDp = with(density) { widthPx.coerceAtLeast(30f).toDp() }
            val isSelected = audio.id == selectedAudioId

            Box(
                modifier = Modifier
                    .offset(x = startOffsetDp)
                    .width(widthDp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(TrackAudioBg)
                    .border(
                        width = if (isSelected) 1.5f.dp else 1.dp,
                        color = if (isSelected) TrackAudioWaveform else StudioBorder,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clickable { onAudioClick(audio.id) }
                    .pointerInput(audio.id) {
                        detectDragGestures(
                            onDragStart = { onAudioClick(audio.id) },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffsetPx += dragAmount.x
                                val calculatedMs = (audio.startTimelineMs + (dragOffsetPx / pxPerSec * 1000f).toLong()).coerceAtLeast(0L)
                                onAudioMove(audio.id, calculatedMs)
                            }
                        )
                    }
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = TrackAudioWaveform,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = audio.title,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(StudioDanger.copy(alpha = 0.25f))
                                .clickable { onAudioDelete(audio.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Audio",
                                tint = StudioDanger,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Audio Track View Preview", showBackground = true, widthDp = 360, heightDp = 44)
@Composable
fun AudioTrackViewPreview() {
    NovaCutTheme {
        AudioTrackView(
            audioClips = listOf(
                AudioClip(
                    id = "a1",
                    title = "Cinematic Ambient Beat",
                    startTimelineMs = 500L,
                    sourceDurationMs = 6000L
                )
            ),
            selectedAudioId = "a1",
            zoomLevel = 1.0f,
            onAudioClick = {}
        )
    }
}
