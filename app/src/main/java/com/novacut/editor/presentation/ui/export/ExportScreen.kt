package com.novacut.editor.presentation.ui.export

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novacut.editor.domain.model.ExportConfig
import com.novacut.editor.domain.model.ExportState
import com.novacut.editor.domain.model.Resolution
import com.novacut.editor.presentation.viewmodel.ExportViewModel
import com.novacut.editor.theme.NovaCutTheme
import com.novacut.editor.theme.StudioBorder
import com.novacut.editor.theme.StudioDarkBg
import com.novacut.editor.theme.StudioPrimary
import com.novacut.editor.theme.StudioSecondary
import com.novacut.editor.theme.StudioSuccess
import com.novacut.editor.theme.StudioSurface
import com.novacut.editor.theme.StudioSurfaceElevated
import com.novacut.editor.theme.StudioTextPrimary
import com.novacut.editor.theme.StudioTextSecondary

@Composable
fun ExportScreen(
    projectId: String,
    viewModel: ExportViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(projectId) {
        viewModel.loadProject(projectId)
    }

    ExportContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onResolutionSelected = { res ->
            viewModel.updateConfig(uiState.config.copy(resolution = res))
        },
        onFrameRateSelected = { fps ->
            viewModel.updateConfig(uiState.config.copy(frameRate = fps))
        },
        onWatermarkToggled = { enabled ->
            viewModel.updateConfig(uiState.config.copy(includeWatermark = enabled))
        },
        onStartExport = { viewModel.startExport() },
        onCancelExport = { viewModel.cancelExport() },
        onShareClick = { filePath ->
            viewModel.shareExportedVideo(context, filePath)
        }
    )
}

@Composable
fun ExportContent(
    uiState: com.novacut.editor.presentation.viewmodel.ExportUiState,
    onBackClick: () -> Unit,
    onResolutionSelected: (Resolution) -> Unit,
    onFrameRateSelected: (Int) -> Unit,
    onWatermarkToggled: (Boolean) -> Unit,
    onStartExport: () -> Unit,
    onCancelExport: () -> Unit,
    onShareClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .statusBarsPadding()
            .padding(20.dp)
    ) {
        // Top Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = StudioTextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Export Project",
                color = StudioTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (val state = uiState.exportState) {
            is ExportState.Idle -> {
                // Resolution Selector Cards
                Text(
                    text = "Export Resolution",
                    color = StudioTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Resolution.values().forEach { res ->
                        val isSelected = res == uiState.config.resolution
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) StudioSurfaceElevated else StudioSurface)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) StudioPrimary else StudioBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onResolutionSelected(res) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = res.label.split(" ").first(),
                                color = if (isSelected) StudioPrimary else StudioTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Frame Rate Selector (30 fps / 60 fps)
                Text(
                    text = "Frame Rate",
                    color = StudioTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    listOf(30, 60).forEach { fps ->
                        val isSelected = fps == uiState.config.frameRate
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) StudioSurfaceElevated else StudioSurface)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) StudioPrimary else StudioBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onFrameRateSelected(fps) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$fps FPS",
                                color = if (isSelected) StudioPrimary else StudioTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Watermark toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(StudioSurface)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Brand Watermark",
                            color = StudioTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Add subtle end watermark",
                            color = StudioTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = uiState.config.includeWatermark,
                        onCheckedChange = onWatermarkToggled,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = StudioPrimary,
                            checkedTrackColor = StudioPrimary.copy(alpha = 0.4f)
                        )
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Primary Start Export Button
                Button(
                    onClick = onStartExport,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StudioPrimary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Start Export",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            is ExportState.Preparing, is ExportState.Rendering -> {
                val progress = if (state is ExportState.Rendering) state.progress else 0f
                val etaText = if (state is ExportState.Rendering) "${state.etaSeconds}s remaining" else "Preparing engine…"
                val percentageText = if (state is ExportState.Rendering) state.percentageText else "0%"

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Progress Ring
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { progress },
                                color = StudioPrimary,
                                trackColor = StudioSurfaceElevated,
                                strokeWidth = 8.dp,
                                modifier = Modifier.size(160.dp)
                            )
                            Text(
                                text = percentageText,
                                color = StudioTextPrimary,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        Text(
                            text = "Exporting Video…",
                            color = StudioTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "$etaText • Please don't lock your screen",
                            color = StudioTextSecondary,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = onCancelExport,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StudioSurfaceElevated,
                                contentColor = StudioTextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "Cancel", fontSize = 13.sp)
                        }
                    }
                }
            }

            is ExportState.Success -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(StudioSuccess.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = StudioSuccess,
                                modifier = Modifier.size(44.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "Ready to Share!",
                            color = StudioTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Saved to Gallery (${state.fileSizeFormatted})",
                            color = StudioTextSecondary,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        Button(
                            onClick = { onShareClick(state.outputPath) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StudioPrimary,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Share Video",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onBackClick,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StudioSurfaceElevated,
                                contentColor = StudioTextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "Back to Studio")
                        }
                    }
                }
            }

            is ExportState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Export Failed",
                            color = StudioPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.message,
                            color = StudioTextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onStartExport) {
                            Text(text = "Retry")
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Export Screen Preview", showBackground = true)
@Composable
fun ExportScreenPreview() {
    NovaCutTheme {
        ExportContent(
            uiState = com.novacut.editor.presentation.viewmodel.ExportUiState(),
            onBackClick = {},
            onResolutionSelected = {},
            onFrameRateSelected = {},
            onWatermarkToggled = {},
            onStartExport = {},
            onCancelExport = {},
            onShareClick = {}
        )
    }
}
