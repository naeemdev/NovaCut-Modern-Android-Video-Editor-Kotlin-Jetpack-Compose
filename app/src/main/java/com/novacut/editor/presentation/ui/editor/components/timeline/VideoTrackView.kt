package com.novacut.editor.presentation.ui.editor.components.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DragHandle
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novacut.editor.core.util.TimeFormatter
import com.novacut.editor.domain.model.VideoClip
import com.novacut.editor.theme.NovaCutTheme
import com.novacut.editor.theme.StudioAccentYellow
import com.novacut.editor.theme.StudioBorder
import com.novacut.editor.theme.StudioPrimary
import com.novacut.editor.theme.StudioSurfaceElevated
import com.novacut.editor.theme.StudioTextPrimary
import com.novacut.editor.theme.TrackVideoBg
import com.novacut.editor.theme.TrackVideoHighlight

@Composable
fun VideoTrackView(
    clips: List<VideoClip>,
    selectedClipId: String?,
    zoomLevel: Float,
    basePxPerSec: Float = 100f,
    onClipClick: (String) -> Unit,
    onAddClipClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val pxPerSec = basePxPerSec * zoomLevel
    val density = LocalDensity.current

    Row(
        modifier = modifier
            .height(58.dp)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        clips.forEach { clip ->
            val clipWidthPx = (clip.timelineDurationMs / 1000f) * pxPerSec
            val clipWidthDp = with(density) { clipWidthPx.coerceAtLeast(40f).toDp() }
            val isSelected = clip.id == selectedClipId

            Box(
                modifier = Modifier
                    .width(clipWidthDp)
                    .fillMaxHeight()
                    .padding(horizontal = 1.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(TrackVideoBg)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) StudioAccentYellow else StudioBorder,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onClipClick(clip.id) }
            ) {
                // If selected, show yellow trim drag handles on left and right
                if (isSelected) {
                    // Left Trim Handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .width(12.dp)
                            .fillMaxHeight()
                            .background(StudioAccentYellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DragHandle,
                            contentDescription = "Trim Start",
                            tint = Color.Black,
                            modifier = Modifier.height(14.dp)
                        )
                    }

                    // Right Trim Handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .width(12.dp)
                            .fillMaxHeight()
                            .background(StudioAccentYellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DragHandle,
                            contentDescription = "Trim End",
                            tint = Color.Black,
                            modifier = Modifier.height(14.dp)
                        )
                    }
                }

                // Filmstrip content title and duration
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = if (isSelected) 16.dp else 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = clip.name,
                        color = StudioTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Text(
                        text = TimeFormatter.formatShortTime(clip.timelineDurationMs),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Plus (+) Add Clip Button at the end of the video timeline
        Box(
            modifier = Modifier
                .width(44.dp)
                .fillMaxHeight()
                .padding(horizontal = 2.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(StudioSurfaceElevated)
                .border(1.dp, StudioBorder, RoundedCornerShape(8.dp))
                .clickable { onAddClipClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Media",
                tint = StudioPrimary,
                modifier = Modifier.height(20.dp)
            )
        }

        if (clips.isEmpty()) {
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .fillMaxHeight()
                    .padding(horizontal = 2.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(StudioSurfaceElevated)
                    .border(1.dp, StudioBorder, RoundedCornerShape(8.dp))
                    .clickable { onAddClipClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+ Add Video Clip",
                    color = StudioPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(name = "Video Track View Preview", showBackground = true, widthDp = 360, heightDp = 70)
@Composable
fun VideoTrackViewPreview() {
    NovaCutTheme {
        VideoTrackView(
            clips = listOf(
                VideoClip(
                    id = "1",
                    uri = "",
                    name = "Intro Scene",
                    sourceDurationMs = 4000L
                ),
                VideoClip(
                    id = "2",
                    uri = "",
                    name = "Main Drone Shot",
                    sourceDurationMs = 6000L
                )
            ),
            selectedClipId = "2",
            zoomLevel = 1.0f,
            onClipClick = {}
        )
    }
}
