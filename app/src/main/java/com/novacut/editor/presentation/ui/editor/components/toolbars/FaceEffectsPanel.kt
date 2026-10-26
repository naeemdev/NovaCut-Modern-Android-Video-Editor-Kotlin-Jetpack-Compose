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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novacut.editor.domain.model.FaceTrackingEffect
import com.novacut.editor.theme.NovaCutTheme
import com.novacut.editor.theme.StudioBorder
import com.novacut.editor.theme.StudioPrimary
import com.novacut.editor.theme.StudioSurface
import com.novacut.editor.theme.StudioSurfaceElevated
import com.novacut.editor.theme.StudioTextPrimary
import com.novacut.editor.theme.StudioTextSecondary

@Composable
fun FaceEffectsPanel(
    selectedEffect: FaceTrackingEffect,
    onSelectEffect: (FaceTrackingEffect) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(StudioSurface)
            .padding(vertical = 12.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(StudioPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Face AR",
                        tint = StudioPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "AR 3D Face Tracking",
                        color = StudioTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Real-time 468-point 3D landmark mesh",
                        color = StudioTextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

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

        // Horizontal List of AR Face Effects
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FaceTrackingEffect.values().forEach { effect ->
                val isSelected = selectedEffect == effect

                Column(
                    modifier = Modifier
                        .width(96.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) StudioPrimary.copy(alpha = 0.15f) else StudioSurfaceElevated)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) StudioPrimary else StudioBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onSelectEffect(effect) }
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Preview Icon
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) {
                                    Brush.linearGradient(
                                        listOf(StudioPrimary, Color(0xFF00C6FF))
                                    )
                                } else {
                                    Brush.linearGradient(
                                        listOf(Color(0xFF2C2D35), Color(0xFF1E1F24))
                                    )
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        val icon = when (effect) {
                            FaceTrackingEffect.NONE -> Icons.Default.Close
                            FaceTrackingEffect.MESH_3D_WIREFRAME -> Icons.Default.Hub
                            FaceTrackingEffect.CYBERPUNK_HUD -> Icons.Default.AutoAwesome
                            FaceTrackingEffect.AR_SUNGLASSES -> Icons.Default.Visibility
                            FaceTrackingEffect.BEAUTY_SMOOTH -> Icons.Default.AutoAwesome
                            FaceTrackingEffect.GOLDEN_CROWN -> Icons.Default.VpnKey
                            FaceTrackingEffect.ANIME_EXPRESSION -> Icons.Default.Face
                        }

                        Icon(
                            imageVector = icon,
                            contentDescription = effect.title,
                            tint = if (isSelected) Color.White else StudioTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = effect.title,
                        color = if (isSelected) StudioPrimary else StudioTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Badge (e.g. 468 pts or 3D AR)
                    Text(
                        text = if (effect.isAr3D) "${effect.landmarkCount} pts" else if (effect == FaceTrackingEffect.NONE) "Off" else "2D",
                        color = if (isSelected) StudioPrimary.copy(alpha = 0.8f) else StudioTextSecondary,
                        fontSize = 9.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Info pill showing engine details
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(StudioSurfaceElevated)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedEffect.description,
                    color = StudioTextSecondary,
                    fontSize = 11.sp
                )

                if (selectedEffect.isAr3D) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(StudioPrimary.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "GPU ACCELERATED",
                            color = StudioPrimary,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Face Effects Panel Preview", showBackground = true)
@Composable
fun FaceEffectsPanelPreview() {
    NovaCutTheme {
        FaceEffectsPanel(
            selectedEffect = FaceTrackingEffect.MESH_3D_WIREFRAME,
            onSelectEffect = {},
            onClose = {}
        )
    }
}
