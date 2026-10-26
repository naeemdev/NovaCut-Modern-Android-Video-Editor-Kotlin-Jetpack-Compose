package com.novacut.editor.presentation.ui.editor.components.toolbars

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import com.novacut.editor.domain.model.TextOverlay
import com.novacut.editor.theme.NovaCutTheme
import com.novacut.editor.theme.StudioBorder
import com.novacut.editor.theme.StudioPrimary
import com.novacut.editor.theme.StudioSurface
import com.novacut.editor.theme.StudioSurfaceElevated
import com.novacut.editor.theme.StudioTextPrimary
import com.novacut.editor.theme.StudioTextSecondary

private val colorSwatches = listOf(
    0xFFFFFFFF, // Pure White
    0xFF00E5FF, // Cyan
    0xFFFACC15, // Yellow
    0xFFA855F7, // Purple
    0xFFEF4444, // Red
    0xFF10B981, // Green
    0xFF000000  // Black
)

private val fontOptions = listOf("Display Bold", "Default", "Monospace", "Sans")
private val animationOptions = listOf("None", "Fade", "Pop", "Slide", "Typewriter", "Glow")
private val labelOptions = listOf("None", "Dark Pill", "Cyber Neon", "Golden Badge")

@Composable
fun TextEditorSheet(
    existingOverlay: TextOverlay? = null,
    onConfirm: (text: String, font: String, colorArgb: Long, animation: String, labelStyle: String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember(existingOverlay) { mutableStateOf(existingOverlay?.text ?: "") }
    var selectedFont by remember(existingOverlay) { mutableStateOf(existingOverlay?.fontStyleName ?: "Display Bold") }
    var selectedColor by remember(existingOverlay) { mutableLongStateOf(existingOverlay?.textColorArgb ?: 0xFFFFFFFF) }
    var selectedAnimation by remember(existingOverlay) { mutableStateOf(existingOverlay?.animationType ?: "Fade") }
    var selectedLabel by remember(existingOverlay) { mutableStateOf(existingOverlay?.labelStyle ?: "Dark Pill") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(StudioSurface)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (existingOverlay != null && existingOverlay.text.isNotBlank()) "Edit Text" else "Add Text & Labels",
                color = StudioTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = StudioTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Input Field
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text("Type your caption text…", color = StudioTextSecondary) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(selectedColor),
                unfocusedTextColor = Color(selectedColor),
                focusedBorderColor = StudioPrimary,
                unfocusedBorderColor = StudioBorder,
                focusedContainerColor = StudioSurfaceElevated,
                unfocusedContainerColor = StudioSurfaceElevated
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Color Palettes
        Text(text = "Color", color = StudioTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            colorSwatches.forEach { colorArgb ->
                val isSelected = colorArgb == selectedColor
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(colorArgb))
                        .border(
                            width = if (isSelected) 2.5f.dp else 1.dp,
                            color = if (isSelected) StudioPrimary else StudioBorder,
                            shape = CircleShape
                        )
                        .clickable { selectedColor = colorArgb },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = if (colorArgb == 0xFFFFFFFF) Color.Black else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Animation Type Chips (Pop, Fade, Slide, etc.)
        Text(text = "Text Animation", color = StudioTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(animationOptions) { anim ->
                val isSelected = anim == selectedAnimation
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) StudioPrimary else StudioSurfaceElevated)
                        .clickable { selectedAnimation = anim }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = anim,
                        color = if (isSelected) Color.Black else StudioTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Label / Background Badge Style
        Text(text = "Label Badge Style", color = StudioTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(labelOptions) { label ->
                val isSelected = label == selectedLabel
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) StudioPrimary else StudioSurfaceElevated)
                        .clickable { selectedLabel = label }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.Black else StudioTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Save Button
        Button(
            onClick = {
                if (text.isNotBlank()) {
                    onConfirm(text, selectedFont, selectedColor, selectedAnimation, selectedLabel)
                }
            },
            enabled = text.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = StudioPrimary,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Apply Caption", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Preview(name = "Text Editor Sheet Preview", showBackground = true)
@Composable
fun TextEditorSheetPreview() {
    NovaCutTheme {
        TextEditorSheet(
            existingOverlay = TextOverlay(text = "CINEMATIC VLOG"),
            onConfirm = { _, _, _, _, _ -> },
            onClose = {}
        )
    }
}
