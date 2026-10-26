package com.novacut.editor.presentation.ui.editor.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.novacut.editor.domain.model.CanvasAspectRatio
import com.novacut.editor.domain.model.FaceTrackingEffect
import com.novacut.editor.domain.model.FilterType
import com.novacut.editor.domain.model.Project
import com.novacut.editor.domain.model.TextOverlay
import com.novacut.editor.theme.NovaCutTheme
import com.novacut.editor.theme.StudioBorder
import com.novacut.editor.theme.StudioDarkBg
import com.novacut.editor.theme.StudioPrimary
import com.novacut.editor.theme.StudioSurface
import com.novacut.editor.theme.StudioSurfaceElevated
import com.novacut.editor.theme.StudioTextPrimary
import com.novacut.editor.theme.StudioTextSecondary
import kotlin.math.roundToInt

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import com.novacut.editor.theme.StudioDanger

@Composable
fun VideoPreviewCanvas(
    project: Project,
    currentPositionMs: Long,
    isPlaying: Boolean,
    player: Player? = null,
    selectedClipId: String? = null,
    selectedTextId: String? = null,
    onTextOverlayClick: (TextOverlay) -> Unit,
    onEditTextClick: (TextOverlay) -> Unit = {},
    onDeleteTextClick: (String) -> Unit = {},
    onTextPositionChange: (overlayId: String, newX: Float, newY: Float) -> Unit = { _, _, _ -> },
    onAddClipClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val activeMatch = remember(project, currentPositionMs) {
        project.findClipAtTimelineTime(currentPositionMs)
    }
    // Resolve active clip either from timeline playhead or currently selected clip
    val activeClip = project.videoClips.find { it.id == selectedClipId } ?: activeMatch?.second
    val activeFilter = activeClip?.filterType ?: FilterType.NONE

    // Audio volume sync with ExoPlayer
    LaunchedEffect(activeClip?.volume, activeClip?.isMuted) {
        if (activeClip != null && player != null) {
            player.volume = if (activeClip.isMuted) 0f else activeClip.volume.coerceIn(0f, 1f)
        }
    }

    // Dynamic Clip In-Animation calculation
    val clipOffsetMs = activeMatch?.third ?: 0L
    val animProgress = (clipOffsetMs / 700f).coerceIn(0f, 1f)
    val clipAnim = activeClip?.animation ?: "None"

    val animAlpha = if (clipAnim == "Fade In") animProgress else 1.0f
    val animScale = if (clipAnim == "Zoom In") 0.75f + (0.25f * animProgress) else 1.0f
    val animTransY = if (clipAnim == "Slide Up") (1f - animProgress) * 120f else 0f
    val animRot = if (clipAnim == "Spin In") (1f - animProgress) * -35f else 0f
    val isFlash = clipAnim == "Flash" && clipOffsetMs in 0..400L

    // Outer workspace container
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(StudioDarkBg)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Framed Canvas Container strictly conforming to aspect ratio
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(project.aspectRatio.ratio, matchHeightConstraintsFirst = true)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(project.canvasBackgroundColor))
                .border(1.dp, StudioBorder, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            val canvasWidthPx = constraints.maxWidth.toFloat()
            val canvasHeightPx = constraints.maxHeight.toFloat()

            // 1. REAL MEDIA3 EXOPLAYER VIDEO RENDERING SURFACE WITH CROP, ROTATE, & ANIMATION
            if (player != null && project.videoClips.isNotEmpty()) {
                AndroidView(
                    factory = { ctx ->
                        val playerView = android.view.LayoutInflater.from(ctx).inflate(
                            com.novacut.editor.R.layout.view_video_player,
                            null
                        ) as PlayerView
                        playerView.player = player
                        playerView
                    },
                    update = { playerView ->
                        if (playerView.player != player) {
                            playerView.player = player
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = animAlpha
                            scaleX = (activeClip?.scale ?: 1.0f) * animScale
                            scaleY = (activeClip?.scale ?: 1.0f) * animScale
                            translationX = (activeClip?.panOffsetX ?: 0f)
                            translationY = (activeClip?.panOffsetY ?: 0f) + animTransY
                            rotationZ = (activeClip?.rotationDegrees ?: 0).toFloat() + animRot
                        }
                )
            } else {
                // Fallback Background when no player is initialized or clips are empty
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    StudioSurfaceElevated,
                                    Color(0xFF1B1E28)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (!isPlaying && project.videoClips.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.PlayCircleFilled,
                            contentDescription = "Paused",
                            tint = StudioPrimary.copy(alpha = 0.4f),
                            modifier = Modifier.size(54.dp)
                        )
                    }

                    if (project.videoClips.isEmpty()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(StudioSurface)
                                .border(1.dp, StudioBorder, RoundedCornerShape(12.dp))
                                .clickable { onAddClipClick() }
                                .padding(horizontal = 24.dp, vertical = 18.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(StudioPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Media",
                                    tint = Color.Black,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Tap to Add Video Clip",
                                color = StudioTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Gallery or Studio samples",
                                color = StudioTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // 2. WHITE FLASH ANIMATION OVERLAY
            if (isFlash) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = (1f - (clipOffsetMs / 400f)).coerceIn(0f, 0.85f)))
                )
            }

            // 3. LIVE COLOR GRADING FILTER OVERLAY
            if (activeFilter != FilterType.NONE) {
                val tintColor = when (activeFilter) {
                    FilterType.CINEMATIC -> Color(0x3300C6FF)
                    FilterType.CYBERPUNK -> Color(0x33FF007F)
                    FilterType.NOIR -> Color(0x66000000)
                    FilterType.VINTAGE -> Color(0x33D97706)
                    FilterType.GOLDEN_HOUR -> Color(0x33F59E0B)
                    FilterType.MOODY_TEAL -> Color(0x330D9488)
                    FilterType.VIBRANT -> Color(0x228B5CF6)
                    else -> Color.Transparent
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(tintColor)
                )
            }

            // 4. ACTIVE AR FACE TRACKING OVERLAY HUD
            val activeFaceEffect = activeClip?.faceEffect ?: FaceTrackingEffect.NONE
            if (activeFaceEffect != FaceTrackingEffect.NONE) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xDD00C6FF))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "AR: ${activeFaceEffect.title}",
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 5. INTERACTIVE DRAGGABLE TEXT OVERLAYS WITH ANIMATIONS & LABELS
            project.textOverlays.forEach { overlay ->
                val isVisible = overlay.isVisibleAt(currentPositionMs) || overlay.id == selectedTextId
                if (isVisible) {
                    DraggableTextItem(
                        overlay = overlay,
                        isSelected = overlay.id == selectedTextId,
                        canvasWidthPx = canvasWidthPx,
                        canvasHeightPx = canvasHeightPx,
                        onClick = { onTextOverlayClick(overlay) },
                        onEditClick = { onEditTextClick(overlay) },
                        onDeleteClick = { onDeleteTextClick(overlay.id) },
                        onPositionChange = { newX, newY ->
                            onTextPositionChange(overlay.id, newX, newY)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DraggableTextItem(
    overlay: TextOverlay,
    isSelected: Boolean,
    canvasWidthPx: Float,
    canvasHeightPx: Float,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onPositionChange: (Float, Float) -> Unit
) {
    var localOffsetX by remember { mutableFloatStateOf(0f) }
    var localOffsetY by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(overlay.xNormalized, overlay.yNormalized, canvasWidthPx, canvasHeightPx) {
        localOffsetX = (overlay.xNormalized - 0.5f) * canvasWidthPx.coerceAtLeast(100f)
        localOffsetY = (overlay.yNormalized - 0.5f) * canvasHeightPx.coerceAtLeast(100f)
    }

    // Animation scaling
    val infiniteTransition = rememberInfiniteTransition(label = "TextAnim")
    val animScale by infiniteTransition.animateFloat(
        initialValue = if (overlay.animationType == "Pop") 0.92f else 1.0f,
        targetValue = if (overlay.animationType == "Pop") 1.08f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PopAnim"
    )

    val fontFam = when (overlay.fontStyleName) {
        "Monospace" -> FontFamily.Monospace
        "Sans" -> FontFamily.SansSerif
        else -> FontFamily.Default
    }

    // Label style decorator
    val labelBgColor = when (overlay.labelStyle) {
        "Dark Pill" -> Color(0xCC111827)
        "Cyber Neon" -> Color(0xDD0F172A)
        "Golden Badge" -> Color(0xEE78350F)
        else -> Color.Transparent
    }

    val labelBorderColor = when (overlay.labelStyle) {
        "Cyber Neon" -> StudioPrimary
        "Golden Badge" -> Color(0xFFFBBF24)
        else -> if (isSelected) StudioPrimary else Color.Transparent
    }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(localOffsetX.roundToInt(), localOffsetY.roundToInt())
            }
            .scale(animScale)
    ) {
        // Main text content bubble with tap & drag gestures
        Box(
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(labelBgColor)
                .border(
                    width = if (isSelected || overlay.labelStyle != "None") 1.5.dp else 0.dp,
                    color = labelBorderColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .pointerInput(overlay.id) {
                    detectTapGestures(onTap = { onClick() })
                }
                .pointerInput(overlay.id) {
                    detectDragGestures(
                        onDragStart = { onClick() },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            localOffsetX += dragAmount.x
                            localOffsetY += dragAmount.y
                            val newNormX = (0.5f + (localOffsetX / canvasWidthPx.coerceAtLeast(1f))).coerceIn(0.05f, 0.95f)
                            val newNormY = (0.5f + (localOffsetY / canvasHeightPx.coerceAtLeast(1f))).coerceIn(0.05f, 0.95f)
                            onPositionChange(newNormX, newNormY)
                        }
                    )
                }
                .padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = overlay.text,
                color = Color(overlay.textColorArgb),
                fontSize = overlay.fontSizeSp.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = fontFam,
                textAlign = TextAlign.Center
            )
        }

        // CapCut-style corner handle controls when selected
        if (isSelected) {
            // Delete Handle (Top-Start)
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(StudioDanger)
                    .clickable { onDeleteClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete Text",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }

            // Edit Handle (Top-End)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(StudioPrimary)
                    .clickable { onEditClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Text",
                    tint = Color.Black,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Preview(name = "Video Preview Canvas 9:16", showBackground = true, widthDp = 360, heightDp = 320)
@Composable
fun VideoPreviewCanvasPreview() {
    NovaCutTheme {
        VideoPreviewCanvas(
            project = Project(
                aspectRatio = CanvasAspectRatio.RATIO_9_16,
                textOverlays = listOf(
                    TextOverlay(
                        text = "SUMMER TRIP ✈️",
                        startTimelineMs = 0L,
                        durationMs = 5000L
                    )
                )
            ),
            currentPositionMs = 1200L,
            isPlaying = false,
            player = null,
            onTextOverlayClick = {}
        )
    }
}
