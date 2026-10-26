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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novacut.editor.theme.NovaCutTheme
import com.novacut.editor.theme.StudioDanger
import com.novacut.editor.theme.StudioPrimary
import com.novacut.editor.theme.StudioSurface
import com.novacut.editor.theme.StudioSurfaceElevated
import com.novacut.editor.theme.StudioTextPrimary
import com.novacut.editor.theme.StudioTextSecondary

@Composable
fun EditSubToolbar(
    onSplitClick: () -> Unit,
    onSpeedClick: () -> Unit,
    onCropClick: () -> Unit,
    onAnimationClick: () -> Unit,
    onVolumeClick: () -> Unit,
    onRotateClick: () -> Unit,
    onDuplicateClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(StudioSurface)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Clip Editing Tools",
                color = StudioTextPrimary,
                fontSize = 13.sp,
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
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubToolButton(
                icon = Icons.Default.ContentCut,
                label = "Split",
                tint = StudioPrimary,
                onClick = onSplitClick
            )

            SubToolButton(
                icon = Icons.Default.Speed,
                label = "Speed",
                tint = StudioTextPrimary,
                onClick = onSpeedClick
            )

            SubToolButton(
                icon = Icons.Default.AspectRatio,
                label = "Crop",
                tint = StudioTextPrimary,
                onClick = onCropClick
            )

            SubToolButton(
                icon = Icons.Default.AutoAwesome,
                label = "Animation",
                tint = StudioTextPrimary,
                onClick = onAnimationClick
            )

            SubToolButton(
                icon = Icons.Default.VolumeUp,
                label = "Volume",
                tint = StudioTextPrimary,
                onClick = onVolumeClick
            )

            SubToolButton(
                icon = Icons.Default.Refresh,
                label = "Rotate",
                tint = StudioTextPrimary,
                onClick = onRotateClick
            )

            SubToolButton(
                icon = Icons.Default.ContentCopy,
                label = "Duplicate",
                tint = StudioTextPrimary,
                onClick = onDuplicateClick
            )

            SubToolButton(
                icon = Icons.Default.Delete,
                label = "Delete",
                tint = StudioDanger,
                onClick = onDeleteClick
            )
        }
    }
}

@Composable
private fun SubToolButton(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(StudioSurfaceElevated),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (tint == StudioDanger) StudioDanger else StudioTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(name = "Edit SubToolbar Preview", showBackground = true)
@Composable
fun EditSubToolbarPreview() {
    NovaCutTheme {
        EditSubToolbar(
            onSplitClick = {},
            onSpeedClick = {},
            onCropClick = {},
            onAnimationClick = {},
            onVolumeClick = {},
            onRotateClick = {},
            onDuplicateClick = {},
            onDeleteClick = {},
            onClose = {}
        )
    }
}
