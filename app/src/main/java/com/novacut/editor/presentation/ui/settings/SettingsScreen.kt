package com.novacut.editor.presentation.ui.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VideoSettings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novacut.editor.BrandConfig
import com.novacut.editor.theme.NovaCutTheme
import com.novacut.editor.theme.StudioBorder
import com.novacut.editor.theme.StudioDarkBg
import com.novacut.editor.theme.StudioPrimary
import com.novacut.editor.theme.StudioSurface
import com.novacut.editor.theme.StudioSurfaceElevated
import com.novacut.editor.theme.StudioTextPrimary
import com.novacut.editor.theme.StudioTextSecondary

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Settings State
    var addDefaultEnding by remember { mutableStateOf(false) }
    var smartHdrEnabled by remember { mutableStateOf(true) }
    var hardwareAccelEnabled by remember { mutableStateOf(true) }
    var defaultResolution by remember { mutableStateOf("1080p") }
    var defaultFps by remember { mutableStateOf("30 fps") }
    var cacheSizeMb by remember { mutableStateOf("148.4 MB") }
    var selectedLanguage by remember { mutableStateOf("English (US)") }

    // Dialog state
    var showClearCacheDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = StudioTextPrimary
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "Settings",
                color = StudioTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Studio & Video Defaults
            item {
                SettingsSectionHeader(title = "Video Editing Defaults")
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(StudioSurface)
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    // Add default ending toggle
                    SettingsToggleRow(
                        icon = Icons.Default.MovieCreation,
                        title = "Add default ending",
                        subtitle = "Attach 2s animated NovaCut ending clip to new projects",
                        checked = addDefaultEnding,
                        onCheckedChange = { addDefaultEnding = it }
                    )

                    SettingsDivider()

                    // Default Export Resolution
                    SettingsOptionRow(
                        icon = Icons.Default.HighQuality,
                        title = "Default Export Quality",
                        currentValue = defaultResolution,
                        options = listOf("720p", "1080p", "2K/4K"),
                        onOptionSelected = { defaultResolution = it }
                    )

                    SettingsDivider()

                    // Default Frame Rate
                    SettingsOptionRow(
                        icon = Icons.Default.Speed,
                        title = "Default Frame Rate",
                        currentValue = defaultFps,
                        options = listOf("24 fps", "30 fps", "60 fps"),
                        onOptionSelected = { defaultFps = it }
                    )

                    SettingsDivider()

                    // Smart HDR
                    SettingsToggleRow(
                        icon = Icons.Default.VideoSettings,
                        title = "Smart HDR Export",
                        subtitle = "Preserve wide dynamic color range on supported displays",
                        checked = smartHdrEnabled,
                        onCheckedChange = { smartHdrEnabled = it }
                    )

                    SettingsDivider()

                    // Hardware Acceleration
                    SettingsToggleRow(
                        icon = Icons.Default.Memory,
                        title = "Hardware Acceleration",
                        subtitle = "Use GPU MediaCodec encoder for 3x faster export rendering",
                        checked = hardwareAccelEnabled,
                        onCheckedChange = { hardwareAccelEnabled = it }
                    )
                }
            }

            // Section 2: Storage & Cache Management
            item {
                SettingsSectionHeader(title = "Storage & Performance")
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(StudioSurface)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(StudioSurfaceElevated),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CleaningServices,
                                    contentDescription = null,
                                    tint = StudioPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Cache Size",
                                    color = StudioTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = cacheSizeMb,
                                    color = StudioTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Button(
                            onClick = { showClearCacheDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StudioSurfaceElevated,
                                contentColor = StudioPrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "Clear Cache", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Section 3: App Language
            item {
                SettingsSectionHeader(title = "Preferences")
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(StudioSurface)
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    SettingsOptionRow(
                        icon = Icons.Default.Language,
                        title = "Language",
                        currentValue = selectedLanguage,
                        options = listOf("English (US)", "Español", "Français"),
                        onOptionSelected = { selectedLanguage = it }
                    )
                }
            }

            // Section 4: About & Legal
            item {
                SettingsSectionHeader(title = "About & Legal")
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(StudioSurface)
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    SettingsClickableRow(
                        icon = Icons.Default.Info,
                        title = "About ${BrandConfig.APP_NAME}",
                        trailingText = "v1.0.0 (Pro)",
                        onClick = { showAboutDialog = true }
                    )

                    SettingsDivider()

                    SettingsClickableRow(
                        icon = Icons.Default.PrivacyTip,
                        title = "Privacy Policy",
                        trailingText = "",
                        onClick = { showPrivacyDialog = true }
                    )

                    SettingsDivider()

                    SettingsClickableRow(
                        icon = Icons.Default.Security,
                        title = "Terms of Service",
                        trailingText = "",
                        onClick = {
                            Toast.makeText(context, "NovaCut Terms: 100% Whitelabel & Royalty-Free", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Clear Cache Confirmation Dialog
    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            containerColor = StudioSurface,
            title = {
                Text(
                    text = "Clear Cache?",
                    color = StudioTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This will delete temporary preview renders and cached video frames. Your saved project drafts will not be affected.",
                    color = StudioTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        cacheSizeMb = "0.0 MB"
                        showClearCacheDialog = false
                        Toast.makeText(context, "Cache successfully cleared!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioPrimary)
                ) {
                    Text("Clear", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCacheDialog = false }) {
                    Text("Cancel", color = StudioTextSecondary)
                }
            }
        )
    }

    // About NovaCut Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = StudioSurface,
            title = {
                Text(
                    text = "${BrandConfig.APP_NAME} Pro Video Studio",
                    color = StudioTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Version: 1.0.0 Pro\nBuild: 2026.10\nArchitecture: Clean Architecture + SOLID\nEngine: Media3 Transformer & ExoPlayer\nStatus: Whitelabel Complete",
                        color = StudioTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioPrimary)
                ) {
                    Text("OK", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Privacy Policy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            containerColor = StudioSurface,
            title = {
                Text(
                    text = "Privacy Policy",
                    color = StudioTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Your media files, videos, audio, and draft timelines remain 100% on your device. NovaCut does not collect, track, or transmit your personal videos to unauthorized third parties.",
                    color = StudioTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioPrimary)
                ) {
                    Text("Understood", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        color = StudioPrimary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 4.dp, top = 8.dp)
    )
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(StudioBorder.copy(alpha = 0.5f))
    )
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(StudioSurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = StudioPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    color = StudioTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = StudioTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = StudioPrimary,
                checkedTrackColor = StudioPrimary.copy(alpha = 0.4f)
            )
        )
    }
}

@Composable
private fun SettingsOptionRow(
    icon: ImageVector,
    title: String,
    currentValue: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(StudioSurfaceElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = StudioPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    color = StudioTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = currentValue,
                color = StudioPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Options Pill Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                val isSelected = option == currentValue
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) StudioPrimary else StudioSurfaceElevated)
                        .border(
                            1.dp,
                            if (isSelected) StudioPrimary else StudioBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onOptionSelected(option) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        color = if (isSelected) Color.Black else StudioTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsClickableRow(
    icon: ImageVector,
    title: String,
    trailingText: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(StudioSurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = StudioPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                color = StudioTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        if (trailingText.isNotEmpty()) {
            Text(
                text = trailingText,
                color = StudioTextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

@Preview(name = "Settings Screen Preview", showBackground = true)
@Composable
fun SettingsScreenPreview() {
    NovaCutTheme {
        SettingsScreen(onBackClick = {})
    }
}
