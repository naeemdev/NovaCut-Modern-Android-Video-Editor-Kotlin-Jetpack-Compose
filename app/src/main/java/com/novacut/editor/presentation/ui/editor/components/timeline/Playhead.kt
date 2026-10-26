package com.novacut.editor.presentation.ui.editor.components.timeline

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.novacut.editor.theme.NovaCutTheme
import com.novacut.editor.theme.PlayheadGlow
import com.novacut.editor.theme.PlayheadWhite
import com.novacut.editor.theme.StudioPrimary

@Composable
fun Playhead(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(20.dp)
            .fillMaxHeight(),
        contentAlignment = Alignment.TopCenter
    ) {
        Canvas(modifier = Modifier.fillMaxHeight().width(20.dp)) {
            val centerX = size.width / 2f

            // Top Triangle Indicator
            val trianglePath = Path().apply {
                moveTo(centerX - 6.dp.toPx(), 0f)
                lineTo(centerX + 6.dp.toPx(), 0f)
                lineTo(centerX, 8.dp.toPx())
                close()
            }

            drawPath(
                path = trianglePath,
                color = Color.White
            )

            // Playhead Vertical Line Glow
            drawLine(
                color = PlayheadGlow,
                start = Offset(centerX, 8.dp.toPx()),
                end = Offset(centerX, size.height),
                strokeWidth = 4f
            )

            // Playhead Sharp Center Line
            drawLine(
                color = PlayheadWhite,
                start = Offset(centerX, 8.dp.toPx()),
                end = Offset(centerX, size.height),
                strokeWidth = 2f
            )
        }
    }
}

@Preview(name = "Playhead Preview", showBackground = true, widthDp = 50, heightDp = 150)
@Composable
fun PlayheadPreview() {
    NovaCutTheme {
        Playhead()
    }
}
