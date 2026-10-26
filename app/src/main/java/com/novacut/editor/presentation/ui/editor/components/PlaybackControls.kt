package com.novacut.editor.presentation.ui.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novacut.editor.core.util.TimeFormatter
import com.novacut.editor.theme.NovaCutTheme
import com.novacut.editor.theme.StudioDarkBg
import com.novacut.editor.theme.StudioPrimary
import com.novacut.editor.theme.StudioSurfaceElevated
import com.novacut.editor.theme.StudioTextPrimary
import com.novacut.editor.theme.StudioTextSecondary

@Composable
fun PlaybackControls(
    currentPositionMs: Long,
    totalDurationMs: Long,
    isPlaying: Boolean,
    onTogglePlayPause: () -> Unit,
    onStepFrame: (Boolean) -> Unit,
    onFullscreenToggle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(StudioDarkBg)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Timecode Readout
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = TimeFormatter.formatTimecode(currentPositionMs),
                color = StudioTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = " / ${TimeFormatter.formatTimecode(totalDurationMs)}",
                color = StudioTextSecondary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // Center Playback Buttons: Step Back, Play/Pause, Step Forward
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { onStepFrame(false) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Step Backward",
                    tint = StudioTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Play / Pause Circle
            IconButton(
                onClick = onTogglePlayPause,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(StudioSurfaceElevated)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = StudioPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                onClick = { onStepFrame(true) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Step Forward",
                    tint = StudioTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Right side: Fullscreen / Fit
        IconButton(
            onClick = onFullscreenToggle,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Fullscreen,
                contentDescription = "Fullscreen",
                tint = StudioTextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(name = "Playback Controls Preview", showBackground = true)
@Composable
fun PlaybackControlsPreview() {
    NovaCutTheme {
        PlaybackControls(
            currentPositionMs = 4320L,
            totalDurationMs = 15000L,
            isPlaying = false,
            onTogglePlayPause = {},
            onStepFrame = {}
        )
    }
}
