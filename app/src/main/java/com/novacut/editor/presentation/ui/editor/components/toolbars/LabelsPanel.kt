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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Label
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

data class LabelBadgePreset(
    val title: String,
    val style: String,
    val animation: String,
    val category: String
)

private val viralLabelPresets = listOf(
    // Viral & Trending
    LabelBadgePreset("🔥 TRENDING", "Cyber Neon", "Pop", "Trending"),
    LabelBadgePreset("🚨 BREAKING", "Dark Pill", "Fade", "Trending"),
    LabelBadgePreset("⭐ VLOG DAY", "Golden Badge", "Pop", "Trending"),
    LabelBadgePreset("🎬 CINEMATIC", "Dark Pill", "Fade", "Trending"),
    LabelBadgePreset("✨ POV", "Cyber Neon", "Glow", "Trending"),

    // Social CTA
    LabelBadgePreset("🔔 SUBSCRIBE", "Golden Badge", "Pop", "Social"),
    LabelBadgePreset("👍 LIKE & SHARE", "Dark Pill", "Slide", "Social"),
    LabelBadgePreset("💬 COMMENT", "Dark Pill", "Fade", "Social"),
    LabelBadgePreset("🎧 SOUND ON", "Cyber Neon", "Glow", "Social"),

    // Promo & Deals
    LabelBadgePreset("💥 50% OFF", "Golden Badge", "Pop", "Promo"),
    LabelBadgePreset("🛍️ SHOP NOW", "Dark Pill", "Fade", "Promo"),
    LabelBadgePreset("⚡ FLASH SALE", "Cyber Neon", "Pop", "Promo"),

    // Vibes
    LabelBadgePreset("💯 CERTIFIED", "Golden Badge", "Pop", "Vibes"),
    LabelBadgePreset("👑 GOAT", "Golden Badge", "Glow", "Vibes"),
    LabelBadgePreset("🤯 UNREAL", "Cyber Neon", "Pop", "Vibes")
)

@Composable
fun LabelsPanel(
    onAddLabel: (text: String, style: String, animation: String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Label,
                    contentDescription = null,
                    tint = StudioPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Viral Badges & Labels",
                    color = StudioTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
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

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Tap any label to add to video timeline", color = StudioTextSecondary, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(viralLabelPresets) { item ->
                val badgeBorderColor = when (item.style) {
                    "Cyber Neon" -> StudioPrimary
                    "Golden Badge" -> Color(0xFFFBBF24)
                    else -> StudioBorder
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(StudioSurfaceElevated)
                        .border(1.5.dp, badgeBorderColor, RoundedCornerShape(10.dp))
                        .clickable { onAddLabel(item.title, item.style, item.animation) }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = item.title,
                            color = StudioTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${item.style} • ${item.animation}",
                            color = StudioTextSecondary,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Labels Panel Preview", showBackground = true)
@Composable
fun LabelsPanelPreview() {
    NovaCutTheme {
        LabelsPanel(
            onAddLabel = { _, _, _ -> },
            onClose = {}
        )
    }
}
