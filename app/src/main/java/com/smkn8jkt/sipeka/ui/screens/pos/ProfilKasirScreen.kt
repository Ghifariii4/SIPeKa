package com.smkn8jkt.sipeka.ui.screens.pos

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.smkn8jkt.sipeka.data.remote.TokenManager
import com.smkn8jkt.sipeka.navigation.Screen
import com.smkn8jkt.sipeka.ui.screens.auth.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val DarkBrown = Color(0xFF3E2723)
private val PrimaryOrange = Color(0xFFFF7043)
private val SurfaceBeige = Color(0xFFFAF7F4)
private val CardWhite = Color(0xFFFFFFFF)

fun formatShiftTime(isoString: String?): String {
    if (isoString.isNullOrBlank()) return "-"
    return try {
        val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        inputFormatter.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormatter.parse(isoString)
        val outputFormatter = SimpleDateFormat("HH:mm 'WIB'", Locale.getDefault())
        outputFormatter.timeZone = TimeZone.getDefault()
        if (date != null) outputFormatter.format(date) else isoString
    } catch (_: Exception) {
        isoString
    }
}

@Composable
fun LiveClockCard() {
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val timeFormat = remember { SimpleDateFormat("HH:mm:ss 'WIB'", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryOrange),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Jam Real-Time Sistem",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = dateFormat.format(Date(currentTime)),
                    fontSize = 13.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Text(
                    text = timeFormat.format(Date(currentTime)),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilKasirScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: PosViewModel = viewModel(),
    authViewModel: AuthViewModel? = null,
    tokenManager: TokenManager? = null
) {
    val context = LocalContext.current

    val currentShift by viewModel.currentShiftData.collectAsState()
    val startingCashState by viewModel.startingCashState.collectAsState()
    val isClockOutLoading by viewModel.isClockOutLoading.collectAsState()
    val clockOutSuccess by viewModel.clockOutSuccess.collectAsState()
    val userRole by authViewModel?.userRole?.collectAsState() ?: remember { mutableStateOf("kasir") }

    val displayModalAwal = remember(currentShift, startingCashState) {
        val shiftStartingCash = currentShift?.startingCash
        if (shiftStartingCash != null && shiftStartingCash > 0) {
            shiftStartingCash
        } else if (startingCashState > 0) {
            startingCashState
        } else {
            currentShift?.expectedCash ?: 0.0
        }
    }

    var showClockOutConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchCurrentShift()
    }

    LaunchedEffect(clockOutSuccess) {
        if (clockOutSuccess) {
            Toast.makeText(context, "Shift berhasil ditutup (Clock-out)", Toast.LENGTH_SHORT).show()
            viewModel.resetClockOutState()
            navController.navigate(Screen.Home) {
                popUpTo(Screen.Home) { inclusive = true }
            }
        }
    }

    Scaffold(
        containerColor = SurfaceBeige,
        bottomBar = {
            NavigationBar(
                containerColor = CardWhite,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Home) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
                    label = { Text("Beranda", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.History) },
                    icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Riwayat") },
                    label = { Text("Riwayat", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                    label = { Text("Profil", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryOrange,
                        selectedTextColor = PrimaryOrange,
                        indicatorColor = PrimaryOrange.copy(alpha = 0.12f)
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            LiveClockCard()

            Spacer(modifier = Modifier.height(16.dp))

            // Card Header Profil
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(PrimaryOrange.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profil Kasir",
                            tint = PrimaryOrange,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Petugas Kasir 1",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBrown
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "User ID: ${currentShift?.displayKasirId ?: "-"}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = PrimaryOrange.copy(alpha = 0.12f),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            Text(
                                text = "Peran: ${userRole?.uppercase() ?: "KASIR"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryOrange,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card Status Shift
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LockClock,
                                contentDescription = "Shift Status",
                                tint = DarkBrown,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Status Shift Saat Ini",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkBrown
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (currentShift?.isShiftActive == true) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                        ) {
                            Text(
                                text = if (currentShift?.isShiftActive == true) "SHIFT AKTIF" else "TIDAK AKTIF",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentShift?.isShiftActive == true) Color(0xFF2E7D32) else Color(0xFFC62828),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Waktu Mulai Shift",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = formatShiftTime(currentShift?.displayStartTime),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkBrown
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Modal Awal (starting_cash)",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = displayModalAwal.toRupiahFormat(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkBrown
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Estimasi Kas Laci (expected_cash)",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = (currentShift?.expectedCash ?: 0.0).toRupiahFormat(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryOrange
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tombol Tutup Shift
            Button(
                onClick = { showClockOutConfirmDialog = true },
                enabled = !isClockOutLoading && currentShift?.isShiftActive == true,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = if (isClockOutLoading) "Memproses Clock-out..." else "Tutup Shift (Clock-out)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tombol Logout
            val scope = rememberCoroutineScope()
            OutlinedButton(
                onClick = {
                    scope.launch {
                        tokenManager?.clearToken()
                        authViewModel?.resetState()
                        navController.navigate(Screen.Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color.Gray),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Logout Akun Kasir",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    // Dialog Konfirmasi & Ringkasan Setoran Shift (Sesuai Activity Diagram)
    if (showClockOutConfirmDialog) {
        val startingCash = displayModalAwal
        val expectedCash = currentShift?.expectedCash ?: startingCash
        val shiftSales = (expectedCash - startingCash).coerceAtLeast(0.0)

        AlertDialog(
            onDismissRequest = { showClockOutConfirmDialog = false },
            title = {
                Text(
                    text = "Ringkasan Setoran Shift Kasir",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = DarkBrown
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Sistem telah menghitung total saldo kas. Silakan verifikasi uang fisik laci kasir:",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceBeige)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Modal Awal (Starting Cash):", fontSize = 12.sp, color = Color.Gray)
                                Text(startingCash.toRupiahFormat(), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkBrown)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Penjualan Shift Ini:", fontSize = 12.sp, color = Color.Gray)
                                Text(shiftSales.toRupiahFormat(), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkBrown)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color(0xFFE0E0E0))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("TOTAL HARUS DISETOR:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryOrange)
                                Text(expectedCash.toRupiahFormat(), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryOrange)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "📌 Harap serahkan uang tunai fisik sejumlah " + expectedCash.toRupiahFormat() + " kepada Guru Pembina.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkBrown,
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClockOutConfirmDialog = false
                        viewModel.clockOut()
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Setor & Tutup Shift", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClockOutConfirmDialog = false }
                ) {
                    Text("Batal", color = Color.Gray)
                }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
