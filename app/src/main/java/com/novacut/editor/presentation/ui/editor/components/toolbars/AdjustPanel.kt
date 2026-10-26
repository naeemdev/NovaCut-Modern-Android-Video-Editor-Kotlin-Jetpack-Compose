package com.novacut.editor.presentation.ui.editor.components.toolbars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novacut.editor.theme.NovaCutTheme
import com.novacut.editor.theme.StudioPrimary
import com.novacut.editor.theme.StudioSurface
import com.novacut.editor.theme.StudioTextPrimary
import com.novacut.editor.theme.StudioTextSecondary
import java.util.Locale

@Composable
fun AdjustPanel(
    initialBrightness: Float = 0f,
    initialContrast: Float = 1f,
    initialSaturation: Float = 1f,
    onAdjustChange: (brightness: Float, contrast: Float, saturation: Float) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var brightness by remember(initialBrightness) { mutableFloatStateOf(initialBrightness) }
    var contrast by remember(initialContrast) { mutableFloatStateOf(initialContrast) }
    var saturation by remember(initialSaturation) { mutableFloatStateOf(initialSaturation) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(StudioSurface)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Fine Adjustments",
                color = StudioTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Row {
                IconButton(
                    onClick = {
                        brightness = 0f
                        contrast = 1f
                        saturation = 1f
                        onAdjustChange(0f, 1f, 1f)
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = StudioTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
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

        // Brightness Slider (-1.0 to 1.0)
        AdjustSliderRow(
            label = "Brightness",
            value = brightness,
            valueRange = -1f..1f,
            valueFormat = "${(brightness * 100).toInt()}%",
            onValueChange = {
                brightness = it
                onAdjustChange(brightness, contrast, saturation)
            }
        )

        // Contrast Slider (0.5 to 1.5)
        AdjustSliderRow(
            label = "Contrast",
            value = contrast,
            valueRange = 0.5f..1.5f,
            valueFormat = String.format(Locale.US, "%.1fx", contrast),
            onValueChange = {
                contrast = it
                onAdjustChange(brightness, contrast, saturation)
            }
        )

        // Saturation Slider (0.0 to 2.0)
        AdjustSliderRow(
            label = "Saturation",
            value = saturation,
            valueRange = 0f..2f,
            valueFormat = String.format(Locale.US, "%.1fx", saturation),
            onValueChange = {
                saturation = it
                onAdjustChange(brightness, contrast, saturation)
            }
        )
    }
}

@Composable
private fun AdjustSliderRow(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    valueFormat: String,
    onValueChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = StudioTextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.weight(0.3f)
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = StudioPrimary,
                activeTrackColor = StudioPrimary
            ),
            modifier = Modifier.weight(0.55f)
        )
        Text(
            text = valueFormat,
            color = StudioTextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.15f)
        )
    }
}

@Preview(name = "Adjust Panel Preview", showBackground = true)
@Composable
fun AdjustPanelPreview() {
    NovaCutTheme {
        AdjustPanel(
            onAdjustChange = { _, _, _ -> },
            onClose = {}
        )
    }
}
