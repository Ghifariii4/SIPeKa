package com.smkn8jkt.sipeka.ui.screens.pos

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Size
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.smkn8jkt.sipeka.ui.theme.BgDarkEspresso
import com.smkn8jkt.sipeka.ui.theme.BgWarmTan
import com.smkn8jkt.sipeka.ui.theme.BorderStitch
import com.smkn8jkt.sipeka.ui.theme.BtnCreamWhite
import com.smkn8jkt.sipeka.ui.theme.BtnDarkChocolate
import com.smkn8jkt.sipeka.ui.theme.CardCreamWhite
import com.smkn8jkt.sipeka.ui.theme.GreenSuccess
import com.smkn8jkt.sipeka.ui.theme.GreenSuccessContainer
import com.smkn8jkt.sipeka.ui.theme.TextDark
import com.smkn8jkt.sipeka.ui.theme.TextMuted
import com.smkn8jkt.sipeka.ui.theme.VibrantOrange
import com.smkn8jkt.sipeka.util.QrCodeUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

/**
 * Dialog Popup Pemindai QR Otomatis di dalam aplikasi SIPeKa (In-App Live Scanner).
 * Menggunakan CameraX ImageAnalysis untuk mendeteksi QR secara real-time dan otomatis
 * tanpa perlu menekan tombol jepret kamera eksternal.
 */
@Composable
fun LiveQrScannerPopup(
    onDismissRequest: () -> Unit,
    onQrDetected: (String) -> Unit,
    onOpenManualInput: () -> Unit = onDismissRequest
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            Toast.makeText(
                context,
                "Izin kamera diperlukan untuk memindai kode QR voucher pesanan siswa.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Gallery picker fallback
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val decoded = QrCodeUtil.decodeQrFromUri(context, uri)
            if (!decoded.isNullOrBlank()) {
                onQrDetected(decoded.trim())
            } else {
                Toast.makeText(
                    context,
                    "⚠️ Tidak dapat mendeteksi QR Code dari gambar galeri yang dipilih.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    var isTorchEnabled by remember { mutableStateOf(false) }
    var cameraControlInstance by remember { mutableStateOf<Camera?>(null) }
    var detectedQrCode by remember { mutableStateOf<String?>(null) }
    var isAnalyzing by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .safeDrawingPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = CardCreamWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // HEADER BAR
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = VibrantOrange,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Scan QR Otomatis",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Text(
                                    text = "Dekatkan voucher ke kotak kamera",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismissRequest,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Tutup",
                                tint = TextDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // AREA KAMERA ATAU TAMPILAN IZIN KAMERA
                    if (!hasCameraPermission) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(BgWarmTan.copy(alpha = 0.35f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = BtnDarkChocolate.copy(alpha = 0.15f),
                                    modifier = Modifier.size(64.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.PhotoCamera,
                                            contentDescription = null,
                                            tint = BtnDarkChocolate,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "Izin Kamera Diperlukan",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Kamera digunakan untuk membaca QR voucher siswa secara otomatis saat transaksi kasir.",
                                    fontSize = 12.sp,
                                    color = TextMuted,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                                    colors = ButtonDefaults.buttonColors(containerColor = BtnDarkChocolate),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Videocam,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Izinkan Akses Kamera",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    } else {
                        // KAMERA REAL-TIME DENGAN VIEWFINDER OVERLAY
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(310.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            // 1. CameraX PreviewView
                            val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
                            val mainHandler = remember { Handler(Looper.getMainLooper()) }

                            AndroidView(
                                factory = { ctx ->
                                    val previewView = PreviewView(ctx).apply {
                                        scaleType = PreviewView.ScaleType.FILL_CENTER
                                    }

                                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                    cameraProviderFuture.addListener({
                                        try {
                                            val cameraProvider = cameraProviderFuture.get()
                                            val preview = Preview.Builder().build().also {
                                                it.setSurfaceProvider(previewView.surfaceProvider)
                                            }

                                            val imageAnalysis = ImageAnalysis.Builder()
                                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                                .build()

                                            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                                if (isAnalyzing && detectedQrCode == null) {
                                                    try {
                                                        val bitmap = imageProxy.toBitmap()
                                                        val decoded = QrCodeUtil.decodeQrFromBitmap(bitmap)
                                                        if (!decoded.isNullOrBlank() && isAnalyzing && detectedQrCode == null) {
                                                            isAnalyzing = false
                                                            mainHandler.post {
                                                                detectedQrCode = decoded.trim()
                                                                coroutineScope.launch {
                                                                    delay(400)
                                                                    onQrDetected(decoded.trim())
                                                                }
                                                            }
                                                        }
                                                    } catch (_: Exception) {
                                                    } finally {
                                                        imageProxy.close()
                                                    }
                                                } else {
                                                    imageProxy.close()
                                                }
                                            }

                                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                                            cameraProvider.unbindAll()
                                            val camera = cameraProvider.bindToLifecycle(
                                                lifecycleOwner,
                                                cameraSelector,
                                                preview,
                                                imageAnalysis
                                            )
                                            cameraControlInstance = camera
                                        } catch (_: Exception) {
                                        }
                                    }, ContextCompat.getMainExecutor(ctx))

                                    previewView
                                },
                                modifier = Modifier.fillMaxSize()
                            )

                            DisposableEffect(Unit) {
                                onDispose {
                                    cameraExecutor.shutdown()
                                }
                            }

                            // 2. Viewfinder Frame & Scanning Laser Animation
                            val infiniteTransition = rememberInfiniteTransition(label = "LaserTransition")
                            val laserPosition by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "LaserPosition"
                            )

                            Box(
                                modifier = Modifier
                                    .size(220.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        width = 2.dp,
                                        color = if (detectedQrCode != null) GreenSuccess else Color.White.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                // 4 Corner Brackets
                                ViewfinderCornerMarkers(color = if (detectedQrCode != null) GreenSuccess else VibrantOrange)

                                // Animated Laser Line
                                if (detectedQrCode == null) {
                                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                                        val maxHeightPx = maxHeight
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(3.dp)
                                                .offset(y = maxHeightPx * laserPosition)
                                                .background(
                                                    brush = Brush.horizontalGradient(
                                                        colors = listOf(
                                                            Color.Transparent,
                                                            VibrantOrange,
                                                            Color.White,
                                                            VibrantOrange,
                                                            Color.Transparent
                                                        )
                                                    )
                                                )
                                        )
                                    }
                                }

                                // SUCCESS OVERLAY KETIKA QR TERDETEKSI
                                if (detectedQrCode != null) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.65f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(12.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Success",
                                                tint = GreenSuccess,
                                                modifier = Modifier.size(48.dp)
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "QR TERDETEKSI!",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 13.sp,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "#${detectedQrCode?.take(16)?.uppercase()}",
                                                fontSize = 11.sp,
                                                color = BgWarmTan,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            // Flashlight Toggle Icon on the top right inside camera
                            IconButton(
                                onClick = {
                                    cameraControlInstance?.let { cam ->
                                        val newState = !isTorchEnabled
                                        cam.cameraControl.enableTorch(newState)
                                        isTorchEnabled = newState
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(12.dp)
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.5f))
                            ) {
                                Icon(
                                    imageVector = if (isTorchEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                    contentDescription = "Flashlight",
                                    tint = if (isTorchEnabled) Color(0xFFFFD54F) else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // STATUS KETERANGAN
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = BgWarmTan.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "⚡",
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Kamera otomatis membaca QR voucher tanpa perlu menekan tombol apapun.",
                                fontSize = 11.sp,
                                color = BtnDarkChocolate,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // ACTION BUTTONS (PILIH GAMBAR / KETIK MANUAL)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, BorderStitch),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = CardCreamWhite),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = BtnDarkChocolate,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Dari Galeri",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BtnDarkChocolate
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                onDismissRequest()
                                onOpenManualInput()
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, BorderStitch),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = CardCreamWhite),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Keyboard,
                                contentDescription = null,
                                tint = BtnDarkChocolate,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Ketik Manual",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BtnDarkChocolate
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Komponen sudut aksen bingkai pemindai (Corner markers) gaya modern stitch.
 */
@Composable
private fun ViewfinderCornerMarkers(color: Color) {
    val cornerSize = 22.dp
    val strokeWidth = 3.5.dp

    Box(modifier = Modifier.fillMaxSize()) {
        // Top-Left Corner
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(cornerSize)
                .border(
                    BorderStroke(strokeWidth, color),
                    shape = RoundedCornerShape(topStart = 14.dp)
                )
        )

        // Top-Right Corner
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(cornerSize)
                .border(
                    BorderStroke(strokeWidth, color),
                    shape = RoundedCornerShape(topEnd = 14.dp)
                )
        )

        // Bottom-Left Corner
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(cornerSize)
                .border(
                    BorderStroke(strokeWidth, color),
                    shape = RoundedCornerShape(bottomStart = 14.dp)
                )
        )

        // Bottom-Right Corner
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(cornerSize)
                .border(
                    BorderStroke(strokeWidth, color),
                    shape = RoundedCornerShape(bottomEnd = 14.dp)
                )
        )
    }
}
