package com.novacut.editor.presentation.ui.editor.components.toolbars

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novacut.editor.theme.NovaCutTheme
import com.novacut.editor.theme.StudioBorder
import com.novacut.editor.theme.StudioPrimary
import com.novacut.editor.theme.StudioSurface
import com.novacut.editor.theme.StudioSurfaceElevated
import com.novacut.editor.theme.StudioTextPrimary
import com.novacut.editor.theme.StudioTextSecondary
import java.util.Locale

private val cropPresets = listOf("Free", "9:16", "16:9", "1:1", "4:3")

@Composable
fun CropPanel(
    initialScale: Float,
    initialPanX: Float,
    initialPanY: Float,
    initialPreset: String,
    onCropChange: (scale: Float, panX: Float, panY: Float, preset: String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember(initialScale) { mutableFloatStateOf(initialScale.coerceIn(1.0f, 3.0f)) }
    var panX by remember(initialPanX) { mutableFloatStateOf(initialPanX.coerceIn(-300f, 300f)) }
    var panY by remember(initialPanY) { mutableFloatStateOf(initialPanY.coerceIn(-300f, 300f)) }
    var selectedPreset by remember(initialPreset) { mutableStateOf(initialPreset) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(StudioSurface)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Crop & Transform Video",
                color = StudioTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        scale = 1.0f
                        panX = 0f
                        panY = 0f
                        selectedPreset = "Free"
                        onCropChange(1.0f, 0f, 0f, "Free")
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Crop",
                        tint = StudioTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = StudioTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Crop Preset Chips
        Text(text = "Aspect Crop Presets", color = StudioTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            cropPresets.forEach { preset ->
                val isSelected = preset == selectedPreset
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) StudioPrimary else StudioSurfaceElevated)
                        .clickable {
                            selectedPreset = preset
                            val targetScale = when (preset) {
                                "9:16" -> 1.4f
                                "16:9" -> 1.2f
                                "1:1" -> 1.3f
                                "4:3" -> 1.15f
                                else -> 1.0f
                            }
                            scale = targetScale
                            onCropChange(scale, panX, panY, selectedPreset)
                        }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = preset,
                        color = if (isSelected) Color.Black else StudioTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Zoom / Scale Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Zoom / Scale", color = StudioTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(
                text = String.format(Locale.US, "%.1fx", scale),
                color = StudioPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = scale,
            onValueChange = {
                scale = it
                onCropChange(scale, panX, panY, selectedPreset)
            },
            valueRange = 1.0f..3.0f,
            colors = SliderDefaults.colors(
                thumbColor = StudioPrimary,
                activeTrackColor = StudioPrimary,
                inactiveTrackColor = StudioSurfaceElevated
            )
        )

        // Pan X & Pan Y Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Horizontal Pan", color = StudioTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "${panX.toInt()}px",
                color = StudioTextPrimary,
                fontSize = 11.sp
            )
        }
        Slider(
            value = panX,
            onValueChange = {
                panX = it
                onCropChange(scale, panX, panY, selectedPreset)
            },
            valueRange = -250f..250f,
            colors = SliderDefaults.colors(
                thumbColor = StudioPrimary,
                activeTrackColor = StudioPrimary,
                inactiveTrackColor = StudioSurfaceElevated
            )
        )
    }
}

@Preview(name = "Crop Panel Preview", showBackground = true)
@Composable
fun CropPanelPreview() {
    NovaCutTheme {
        CropPanel(
            initialScale = 1.2f,
            initialPanX = 0f,
            initialPanY = 0f,
            initialPreset = "Free",
            onCropChange = { _, _, _, _ -> },
            onClose = {}
        )
    }
}
