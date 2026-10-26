package com.novacut.editor.presentation.ui.editor.components.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.TextFields
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
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novacut.editor.domain.model.TextOverlay
import com.novacut.editor.theme.NovaCutTheme
import com.novacut.editor.theme.StudioBorder
import com.novacut.editor.theme.StudioDanger
import com.novacut.editor.theme.TrackTextBg
import com.novacut.editor.theme.TrackTextHighlight

@Composable
fun TextTrackView(
    textOverlays: List<TextOverlay>,
    selectedTextId: String?,
    zoomLevel: Float,
    basePxPerSec: Float = 100f,
    onTextClick: (TextOverlay) -> Unit,
    onTextTimelineMove: (overlayId: String, newStartMs: Long) -> Unit = { _, _ -> },
    onTextDelete: (overlayId: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val pxPerSec = basePxPerSec * zoomLevel
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .height(32.dp)
            .padding(vertical = 2.dp)
    ) {
        textOverlays.forEach { textOverlay ->
            var dragOffsetPx by remember(textOverlay.startTimelineMs) { mutableFloatStateOf(0f) }
            val currentStartMs = (textOverlay.startTimelineMs + (dragOffsetPx / pxPerSec * 1000f).toLong()).coerceAtLeast(0L)
            val startOffsetPx = (currentStartMs / 1000f) * pxPerSec
            val widthPx = (textOverlay.durationMs / 1000f) * pxPerSec
            val startOffsetDp = with(density) { startOffsetPx.toDp() }
            val widthDp = with(density) { widthPx.coerceAtLeast(30f).toDp() }
            val isSelected = textOverlay.id == selectedTextId

            Box(
                modifier = Modifier
                    .offset(x = startOffsetDp)
                    .width(widthDp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(TrackTextBg)
                    .border(
                        width = if (isSelected) 1.5f.dp else 1.dp,
                        color = if (isSelected) TrackTextHighlight else StudioBorder,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clickable { onTextClick(textOverlay) }
                    .pointerInput(textOverlay.id) {
                        detectDragGestures(
                            onDragStart = { onTextClick(textOverlay) },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffsetPx += dragAmount.x
                                val calculatedMs = (textOverlay.startTimelineMs + (dragOffsetPx / pxPerSec * 1000f).toLong()).coerceAtLeast(0L)
                                onTextTimelineMove(textOverlay.id, calculatedMs)
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
                            imageVector = Icons.Default.TextFields,
                            contentDescription = null,
                            tint = TrackTextHighlight,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = textOverlay.text,
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
                                .size(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(StudioDanger.copy(alpha = 0.25f))
                                .clickable { onTextDelete(textOverlay.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Text",
                                tint = StudioDanger,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Text Track View Preview", showBackground = true, widthDp = 360, heightDp = 40)
@Composable
fun TextTrackViewPreview() {
    NovaCutTheme {
        TextTrackView(
            textOverlays = listOf(
                TextOverlay(
                    id = "t1",
                    text = "HELLO WORLD",
                    startTimelineMs = 1000L,
                    durationMs = 4000L
                )
            ),
            selectedTextId = "t1",
            zoomLevel = 1.0f,
            onTextClick = {}
        )
    }
}
