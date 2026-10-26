package com.novacut.editor.presentation.ui.editor.components.timeline

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.novacut.editor.core.util.TimeFormatter
import com.novacut.editor.theme.NovaCutTheme
import com.novacut.editor.theme.StudioBorder
import com.novacut.editor.theme.StudioTextSecondary

@Composable
fun TimelineRuler(
    totalDurationMs: Long,
    zoomLevel: Float,
    basePxPerSec: Float = 100f,
    modifier: Modifier = Modifier
) {
    val pxPerSec = basePxPerSec * zoomLevel

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp)
    ) {
        val totalSeconds = (totalDurationMs / 1000f).toInt() + 2

        for (sec in 0..totalSeconds) {
            val startX = sec * pxPerSec

            // Major second mark
            drawLine(
                color = StudioTextSecondary.copy(alpha = 0.5f),
                start = Offset(startX, size.height - 12.dp.toPx()),
                end = Offset(startX, size.height),
                strokeWidth = 1.5f
            )

            // Half-second sub-tick
            val halfX = startX + (pxPerSec / 2f)
            drawLine(
                color = StudioBorder,
                start = Offset(halfX, size.height - 6.dp.toPx()),
                end = Offset(halfX, size.height),
                strokeWidth = 1f
            )

            // Draw time text on every major tick (or every 2 seconds if zoomed out)
            if (zoomLevel >= 1.0f || sec % 2 == 0) {
                drawContext.canvas.nativeCanvas.apply {
                    val text = TimeFormatter.formatShortTime((sec * 1000).toLong())
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.argb(180, 160, 160, 175)
                        textSize = 9.dp.toPx()
                        isAntiAlias = true
                    }
                    drawText(text, startX + 4.dp.toPx(), 11.dp.toPx(), paint)
                }
            }
        }
    }
}

@Preview(name = "Timeline Ruler Preview", showBackground = true, widthDp = 360, heightDp = 30)
@Composable
fun TimelineRulerPreview() {
    NovaCutTheme {
        TimelineRuler(
            totalDurationMs = 10000L,
            zoomLevel = 1.0f
        )
    }
}
