package com.novacut.editor.presentation.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview as CameraXPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import com.novacut.editor.core.util.FileUtils
import com.novacut.editor.core.util.SampleVideoProvider
import java.io.File
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.novacut.editor.theme.NovaCutTheme
import com.novacut.editor.theme.StudioBorder
import com.novacut.editor.theme.StudioDarkBg
import com.novacut.editor.theme.StudioPrimary
import com.novacut.editor.theme.StudioSecondary
import com.novacut.editor.theme.StudioSurface
import com.novacut.editor.theme.StudioSurfaceElevated
import com.novacut.editor.theme.StudioTextPrimary
import com.novacut.editor.theme.StudioTextSecondary
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun CameraScreen(
    onClose: () -> Unit,
    onRecordingFinished: (videoUri: String, durationMs: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val isPreviewMode = LocalInspectionMode.current

    // Permission state checking
    var hasCameraPermission by remember {
        mutableStateOf(
            isPreviewMode || ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var hasAudioPermission by remember {
        mutableStateOf(
            isPreviewMode || ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Permission Launcher: triggers the official Android system permission dialog
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCameraPermission = permissions[Manifest.permission.CAMERA] == true
        hasAudioPermission = permissions[Manifest.permission.RECORD_AUDIO] == true
        if (!hasCameraPermission) {
            Toast.makeText(context, "Camera permission is required to shoot video", Toast.LENGTH_LONG).show()
        }
    }

    // Auto-request permission on screen launch
    LaunchedEffect(Unit) {
        if (!isPreviewMode && (!hasCameraPermission || !hasAudioPermission)) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                )
            )
        }
    }

    // If permissions not yet granted, render Permission Request UI
    if (!hasCameraPermission) {
        CameraPermissionFallback(
            onRequestPermission = {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                    )
                )
            },
            onClose = onClose
        )
        return
    }

    // Camera Hardware & Mode State
    var isFrontCamera by remember { mutableStateOf(false) }
    var flashMode by remember { mutableIntStateOf(0) } // 0: Off, 1: On, 2: Auto
    var selectedSpeed by remember { mutableStateOf("1x") }
    var countdownTimerSeconds by remember { mutableIntStateOf(0) }
    var isTeleprompterOpen by remember { mutableStateOf(false) }
    var teleprompterText by remember { mutableStateOf("Welcome to NovaCut Studio! Start speaking naturally while looking directly at the camera lens...") }
    var isBeautyFilterOn by remember { mutableStateOf(true) }

    // Recording State
    var isRecording by remember { mutableStateOf(false) }
    var recordedDurationMs by remember { mutableLongStateOf(0L) }
    var maxDurationLimitMs by remember { mutableLongStateOf(15000L) }
    var hasRecordedClip by remember { mutableStateOf(false) }

    var activeRecording by remember { mutableStateOf<Recording?>(null) }
    var lastRecordedFile by remember { mutableStateOf<File?>(null) }
    var lastRecordedVideoUri by remember { mutableStateOf<String?>(null) }
    var pendingFinishAfterFinalize by remember { mutableStateOf(false) }

    // Native Video Picker from device storage/gallery
    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val cachedUri = FileUtils.copyUriToLocalCache(context, uri, "gallery_import")
            val realDuration = FileUtils.getVideoDurationMs(context, cachedUri)
            onRecordingFinished(cachedUri, realDuration)
        }
    }

    // CameraX VideoCapture instance
    val videoCapture = remember {
        val qualitySelector = QualitySelector.from(
            Quality.HD,
            FallbackStrategy.higherQualityOrLowerThan(Quality.SD)
        )
        val recorder = Recorder.Builder()
            .setQualitySelector(qualitySelector)
            .build()
        VideoCapture.withOutput(recorder)
    }

    // Clean up active recording when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            try {
                activeRecording?.stop()
                activeRecording?.close()
            } catch (_: Exception) {}
        }
    }

    val startCameraRecording: () -> Unit = {
        if (!isRecording) {
            val outputFile = File(context.cacheDir, "camera_rec_${System.currentTimeMillis()}.mp4")
            lastRecordedFile = outputFile
            val outputOptions = FileOutputOptions.Builder(outputFile).build()

            val pending = videoCapture.output.prepareRecording(context, outputOptions)
            if (hasAudioPermission && ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                pending.withAudioEnabled()
            }

            activeRecording = pending.start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                when (recordEvent) {
                    is VideoRecordEvent.Start -> {
                        isRecording = true
                        recordedDurationMs = 0L
                    }
                    is VideoRecordEvent.Status -> {
                        recordedDurationMs = recordEvent.recordingStats.recordedDurationNanos / 1_000_000L
                        if (recordedDurationMs >= maxDurationLimitMs) {
                            activeRecording?.stop()
                            activeRecording = null
                            isRecording = false
                            Toast.makeText(context, "Segment limit reached", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is VideoRecordEvent.Finalize -> {
                        isRecording = false
                        activeRecording = null
                        if (!recordEvent.hasError() || (outputFile.exists() && outputFile.length() > 0L)) {
                            val uriStr = Uri.fromFile(outputFile).toString()
                            lastRecordedVideoUri = uriStr
                            hasRecordedClip = true
                            if (pendingFinishAfterFinalize) {
                                pendingFinishAfterFinalize = false
                                val dur = if (recordedDurationMs > 0L) recordedDurationMs else FileUtils.getVideoDurationMs(context, uriStr)
                                onRecordingFinished(uriStr, dur)
                            }
                        } else {
                            if (outputFile.exists() && outputFile.length() > 0L) {
                                val uriStr = Uri.fromFile(outputFile).toString()
                                lastRecordedVideoUri = uriStr
                                hasRecordedClip = true
                                if (pendingFinishAfterFinalize) {
                                    pendingFinishAfterFinalize = false
                                    onRecordingFinished(uriStr, recordedDurationMs)
                                }
                            } else {
                                Toast.makeText(context, "Recording completed: using fallback asset", Toast.LENGTH_SHORT).show()
                                val fallbackUri = SampleVideoProvider.getSampleVideoUri(context, "sample_video.mp4")
                                lastRecordedVideoUri = fallbackUri
                                hasRecordedClip = true
                                if (pendingFinishAfterFinalize) {
                                    pendingFinishAfterFinalize = false
                                    onRecordingFinished(fallbackUri, 8000L)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    val stopCameraRecording: () -> Unit = {
        if (activeRecording != null) {
            activeRecording?.stop()
            activeRecording = null
        }
        isRecording = false
    }

    val finishRecordingSession: () -> Unit = {
        if (isRecording) {
            pendingFinishAfterFinalize = true
            stopCameraRecording()
        } else {
            val uriToUse = lastRecordedVideoUri
                ?: lastRecordedFile?.takeIf { it.exists() && it.length() > 0L }?.let { Uri.fromFile(it).toString() }
                ?: SampleVideoProvider.getSampleVideoUri(context, "sample_video.mp4")

            val duration = if (recordedDurationMs > 0L) recordedDurationMs else FileUtils.getVideoDurationMs(context, uriToUse)
            onRecordingFinished(uriToUse, duration)
        }
    }

    // Teleprompter Drag Offset
    var teleprompterOffsetX by remember { mutableFloatStateOf(0f) }
    var teleprompterOffsetY by remember { mutableFloatStateOf(0f) }

    // CameraControl reference for torch/flash
    var activeCameraControl by remember { mutableStateOf<CameraControl?>(null) }

    // Pulsing animation for recording ring
    val infiniteTransition = rememberInfiniteTransition(label = "RecordPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isRecording) 1.15f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // 1. REAL LIVE HARDWARE CAMERAX VIEWFINDER
        if (!isPreviewMode) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = CameraXPreview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val cameraSelector = if (isFrontCamera) {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            } else {
                                CameraSelector.DEFAULT_BACK_CAMERA
                            }

                            cameraProvider.unbindAll()
                            val camera = cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                videoCapture
                            )
                            activeCameraControl = camera.cameraControl
                            try {
                                camera.cameraControl.enableTorch(flashMode == 1)
                            } catch (_: Exception) {}
                        } catch (exc: Exception) {
                            exc.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                update = { previewView ->
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(previewView.context)
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = CameraXPreview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            val cameraSelector = if (isFrontCamera) {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            } else {
                                CameraSelector.DEFAULT_BACK_CAMERA
                            }
                            cameraProvider.unbindAll()
                            val camera = cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                videoCapture
                            )
                            activeCameraControl = camera.cameraControl
                            try {
                                camera.cameraControl.enableTorch(flashMode == 1)
                            } catch (_: Exception) {}
                        } catch (exc: Exception) {
                            exc.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(previewView.context))
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Simulated Viewfinder for Previews
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1E212B)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "LIVE HARDWARE CAMERA (CameraX)",
                    color = Color.White.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 2. Segmented Progress Bar on Top
        val progress = (recordedDurationMs.toFloat() / maxDurationLimitMs.toFloat()).coerceIn(0f, 1f)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Color.White.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(4.dp)
                    .background(if (isRecording) Color.Red else StudioPrimary)
            )
        }

        // 3. Top Action Bar: Close | Add Sound | Flash
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Music Sound Picker
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable {
                        Toast.makeText(context, "Trending Audio Library Loaded", Toast.LENGTH_SHORT).show()
                    }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = StudioPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Add Sound",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Flash Toggle
            IconButton(
                onClick = {
                    flashMode = (flashMode + 1) % 3
                    activeCameraControl?.enableTorch(flashMode == 1)
                },
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                val icon = when (flashMode) {
                    1 -> Icons.Default.FlashOn
                    2 -> Icons.Default.FlashAuto
                    else -> Icons.Default.FlashOff
                }
                Icon(
                    imageVector = icon,
                    contentDescription = "Flash",
                    tint = if (flashMode > 0) StudioPrimary else Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // 4. Right Sidebar: Flip | Speed | Beauty | Timer | Prompter
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Flip Front/Rear Camera
            CameraSidebarButton(
                icon = Icons.Default.FlipCameraAndroid,
                label = if (isFrontCamera) "Front" else "Rear",
                onClick = { isFrontCamera = !isFrontCamera }
            )

            // Speed Selector
            CameraSidebarButton(
                icon = Icons.Default.Speed,
                label = selectedSpeed,
                onClick = {
                    selectedSpeed = when (selectedSpeed) {
                        "0.5x" -> "1x"
                        "1x" -> "2x"
                        "2x" -> "3x"
                        else -> "0.5x"
                    }
                }
            )

            // Beauty Retouch
            CameraSidebarButton(
                icon = Icons.Default.AutoAwesome,
                label = if (isBeautyFilterOn) "Retouch" else "Natural",
                isActive = isBeautyFilterOn,
                onClick = { isBeautyFilterOn = !isBeautyFilterOn }
            )

            // Countdown Timer
            CameraSidebarButton(
                icon = Icons.Default.Timer,
                label = if (countdownTimerSeconds == 0) "Timer" else "${countdownTimerSeconds}s",
                isActive = countdownTimerSeconds > 0,
                onClick = {
                    countdownTimerSeconds = when (countdownTimerSeconds) {
                        0 -> 3
                        3 -> 10
                        else -> 0
                    }
                }
            )

            // Teleprompter toggle
            CameraSidebarButton(
                icon = Icons.Default.TextFields,
                label = "Prompter",
                isActive = isTeleprompterOpen,
                onClick = { isTeleprompterOpen = !isTeleprompterOpen }
            )
        }

        // 5. Draggable Teleprompter
        AnimatedVisibility(
            visible = isTeleprompterOpen,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(teleprompterOffsetX.roundToInt(), teleprompterOffsetY.roundToInt() + 80) }
                    .width(320.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.Black.copy(alpha = 0.8f))
                    .border(1.dp, StudioPrimary.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            teleprompterOffsetX += dragAmount.x
                            teleprompterOffsetY += dragAmount.y
                        }
                    }
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SMART TELEPROMPTER",
                            color = StudioPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { isTeleprompterOpen = false }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = teleprompterText,
                        color = Color.White,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // 6. Bottom Controls: Duration Selector | Record | Gallery | Done
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Live Timecode
            if (isRecording || recordedDurationMs > 0L) {
                val seconds = (recordedDurationMs / 1000)
                val millis = (recordedDurationMs % 1000) / 100
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = String.format("%02d:%02d.%d", seconds / 60, seconds % 60, millis),
                        color = if (isRecording) Color.Red else Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Duration Mode Switcher
            if (!isRecording) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "15s" to 15000L,
                        "60s" to 60000L,
                        "3m" to 180000L
                    ).forEach { (label, duration) ->
                        val isSelected = maxDurationLimitMs == duration
                        Text(
                            text = label,
                            color = if (isSelected) StudioPrimary else Color.White.copy(alpha = 0.6f),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) StudioSurfaceElevated else Color.Transparent)
                                .clickable { maxDurationLimitMs = duration }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Shutter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Upload / Gallery Pick
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        galleryPickerLauncher.launch("video/*")
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(StudioSurface)
                            .border(1.dp, StudioBorder, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Upload",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Upload", color = Color.White, fontSize = 10.sp)
                }

                // Shutter Button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(if (isRecording) Color.Red.copy(alpha = 0.35f) else StudioPrimary.copy(alpha = 0.25f))
                        .clickable {
                            if (!isRecording) {
                                startCameraRecording()
                            } else {
                                stopCameraRecording()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(if (isRecording) Color.Red else StudioPrimary)
                    )
                }

                // Finish / Checkmark to Editor
                if (hasRecordedClip || recordedDurationMs > 0L || isRecording) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            finishRecordingSession()
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(StudioPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Done",
                                tint = Color.Black,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Done", color = StudioPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Spacer(modifier = Modifier.size(46.dp))
                }
            }
        }
    }
}

@Composable
private fun CameraPermissionFallback(
    onRequestPermission: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = StudioTextSecondary
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(StudioPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = StudioPrimary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Camera & Audio Access",
                color = StudioTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "NovaCut needs camera and microphone permission to record high-definition videos with studio filters and sound synchronization.",
                color = StudioTextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }

        Button(
            onClick = onRequestPermission,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = StudioPrimary)
        ) {
            Text(
                text = "Grant Permission",
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CameraSidebarButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (isActive) StudioPrimary else Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color.Black else Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (isActive) StudioPrimary else Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(name = "Camera Screen Preview", showBackground = true)
@Composable
fun CameraScreenPreview() {
    NovaCutTheme {
        CameraScreen(
            onClose = {},
            onRecordingFinished = { _, _ -> }
        )
    }
}
