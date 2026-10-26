package com.novacut.editor.presentation.ui.editor.components.toolbars

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novacut.editor.presentation.viewmodel.EditorPanel
import com.novacut.editor.theme.NovaCutTheme
import com.novacut.editor.theme.StudioBorder
import com.novacut.editor.theme.StudioPrimary
import com.novacut.editor.theme.StudioSurface
import com.novacut.editor.theme.StudioSurfaceElevated
import com.novacut.editor.theme.StudioTextPrimary
import com.novacut.editor.theme.StudioTextSecondary

data class ToolItem(val panel: EditorPanel, val label: String, val icon: ImageVector)

private val toolItems = listOf(
    ToolItem(EditorPanel.EDIT, "Edit", Icons.Default.ContentCut),
    ToolItem(EditorPanel.AUDIO, "Audio", Icons.Default.Audiotrack),
    ToolItem(EditorPanel.TEXT, "Text", Icons.Default.TextFields),
    ToolItem(EditorPanel.LABELS, "Labels", Icons.Default.Label),
    ToolItem(EditorPanel.FILTERS, "Filters", Icons.Default.Filter),
    ToolItem(EditorPanel.FACE_EFFECTS, "Face AR", Icons.Default.Face),
    ToolItem(EditorPanel.ADJUST, "Adjust", Icons.Default.Tune),
    ToolItem(EditorPanel.FORMAT, "Format", Icons.Default.AspectRatio),
    ToolItem(EditorPanel.SPEED, "Speed", Icons.Default.Speed)
)

@Composable
fun MainBottomBar(
    activePanel: EditorPanel,
    onToolClick: (EditorPanel) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(StudioSurface)
            .horizontalScroll(scrollState)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        toolItems.forEach { item ->
            val isActive = activePanel == item.panel

            Column(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onToolClick(item.panel) }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isActive) StudioPrimary else StudioSurfaceElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isActive) StudioSurface else StudioTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.label,
                    color = if (isActive) StudioPrimary else StudioTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Preview(name = "Main Bottom Bar Preview", showBackground = true)
@Composable
fun MainBottomBarPreview() {
    NovaCutTheme {
        MainBottomBar(
            activePanel = EditorPanel.EDIT,
            onToolClick = {}
        )
    }
}
