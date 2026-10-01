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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

import com.smkn8jkt.sipeka.ui.theme.BgDarkEspresso
import com.smkn8jkt.sipeka.ui.theme.BgWarmTan
import com.smkn8jkt.sipeka.ui.theme.BtnCreamWhite
import com.smkn8jkt.sipeka.ui.theme.BtnDarkChocolate
import com.smkn8jkt.sipeka.ui.theme.BtnMocha
import com.smkn8jkt.sipeka.ui.theme.CardCreamWhite
import com.smkn8jkt.sipeka.ui.theme.GreenSuccess
import com.smkn8jkt.sipeka.ui.theme.GreenSuccessContainer
import com.smkn8jkt.sipeka.ui.theme.OutlineWarm
import com.smkn8jkt.sipeka.ui.theme.RedError
import com.smkn8jkt.sipeka.ui.theme.RedErrorContainer
import com.smkn8jkt.sipeka.ui.theme.SipekaTheme
import com.smkn8jkt.sipeka.ui.theme.TextDark
import com.smkn8jkt.sipeka.ui.theme.TextLight
import com.smkn8jkt.sipeka.ui.theme.TextMedium
import com.smkn8jkt.sipeka.ui.theme.TextMuted

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
    val dateFormat = remember { SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BgDarkEspresso),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                    text = "Jam Sistem PKK",
                    fontSize = 12.sp,
                    color = BgWarmTan,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = dateFormat.format(Date(currentTime)),
                    fontSize = 13.sp,
                    color = BtnCreamWhite,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = BtnMocha.copy(alpha = 0.4f)
            ) {
                Text(
                    text = timeFormat.format(Date(currentTime)),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BtnCreamWhite,
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

    val kasirName by viewModel.kasirName.collectAsState()
    val kasirNip by viewModel.kasirNip.collectAsState()
    val isUpdatingKasir by viewModel.isUpdatingKasirProfile.collectAsState()

    var nameInput by remember(kasirName) { mutableStateOf(kasirName) }
    var nipInput by remember(kasirNip) { mutableStateOf(kasirNip) }
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }

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

    val shiftSales = remember(currentShift, displayModalAwal) {
        val expected = currentShift?.expectedCash ?: displayModalAwal
        (expected - displayModalAwal).coerceAtLeast(0.0)
    }

    var showClockOutConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchCurrentShift()
    }

    LaunchedEffect(clockOutSuccess) {
        if (clockOutSuccess) {
            Toast.makeText(context, "Shift berhasil ditutup", Toast.LENGTH_SHORT).show()
            viewModel.resetClockOutState()
            navController.navigate(Screen.Home) {
                popUpTo(Screen.Home) { inclusive = true }
            }
        }
    }

    Scaffold(
        containerColor = com.smkn8jkt.sipeka.ui.theme.BgLightCanvas,
        topBar = {
            Surface(
                color = BgDarkEspresso,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(BtnMocha),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "PKK",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = BtnCreamWhite
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Profil Kasir",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextLight
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = BgWarmTan
                                ) {
                                    Text(
                                        text = "KASIR",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = BtnDarkChocolate,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Toko PKK SMKN 8 Jakarta",
                                fontSize = 11.sp,
                                color = BgWarmTan.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = CardCreamWhite,
                tonalElevation = 8.dp,
                modifier = Modifier.height(64.dp)
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Home) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Beranda", modifier = Modifier.size(22.dp)) },
                    label = { Text("Beranda", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BtnDarkChocolate,
                        selectedTextColor = BtnDarkChocolate,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = com.smkn8jkt.sipeka.ui.theme.SegmentBg
                    )
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.History) },
                    icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Riwayat", modifier = Modifier.size(22.dp)) },
                    label = { Text("Riwayat", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BtnDarkChocolate,
                        selectedTextColor = BtnDarkChocolate,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = com.smkn8jkt.sipeka.ui.theme.SegmentBg
                    )
                )

                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil", modifier = Modifier.size(22.dp)) },
                    label = { Text("Profil", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BtnDarkChocolate,
                        selectedTextColor = BtnDarkChocolate,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = com.smkn8jkt.sipeka.ui.theme.SegmentBg
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Profil Kasir",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(14.dp))

            LiveClockCard()

            Spacer(modifier = Modifier.height(14.dp))

            // Profil Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardCreamWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(BgWarmTan.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profil",
                            tint = BtnDarkChocolate,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = kasirName,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "NIP: $kasirNip • ID: ${currentShift?.displayKasirId ?: "-"}",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Card Edit Informasi Akun Kasir Sendiri
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardCreamWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Edit Informasi Akun Kasir",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Perbarui nama, NIP, dan kata sandi akun kasir Anda.",
                        fontSize = 11.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(text = "Nama Lengkap Petugas", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it; formError = null },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BtnDarkChocolate,
                            unfocusedBorderColor = OutlineWarm.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Nomor NIP / ID Kasir", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = nipInput,
                        onValueChange = { nipInput = it; formError = null },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BtnDarkChocolate,
                            unfocusedBorderColor = OutlineWarm.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = OutlineWarm.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Ganti Kata Sandi (Opsional)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it; formError = null },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle Password",
                                    tint = TextMuted
                                )
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BtnDarkChocolate,
                            unfocusedBorderColor = OutlineWarm.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Konfirmasi Kata Sandi Baru", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = confirmPasswordInput,
                        onValueChange = { confirmPasswordInput = it; formError = null },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BtnDarkChocolate,
                            unfocusedBorderColor = OutlineWarm.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    formError?.let { err ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = err, color = RedError, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (nameInput.isBlank()) {
                                formError = "Nama kasir tidak boleh kosong."
                                return@Button
                            }
                            if (nipInput.isBlank()) {
                                formError = "NIP kasir tidak boleh kosong."
                                return@Button
                            }
                            if (passwordInput.isNotBlank()) {
                                if (passwordInput.length < 6) {
                                    formError = "Kata sandi minimal 6 karakter."
                                    return@Button
                                }
                                if (passwordInput != confirmPasswordInput) {
                                    formError = "Konfirmasi kata sandi tidak cocok."
                                    return@Button
                                }
                            }
                            viewModel.updateKasirProfile(
                                nameInput,
                                nipInput,
                                if (passwordInput.isNotBlank()) passwordInput else null
                            )
                            passwordInput = ""
                            confirmPasswordInput = ""
                        },
                        enabled = !isUpdatingKasir,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BtnDarkChocolate,
                            contentColor = BtnCreamWhite
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        if (isUpdatingKasir) {
                            CircularProgressIndicator(color = BtnCreamWhite, modifier = Modifier.size(20.dp))
                        } else {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Simpan Perubahan Akun", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Card Status Shift
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardCreamWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LockClock,
                                contentDescription = "Shift",
                                tint = BtnDarkChocolate,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Status Shift",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (currentShift?.isShiftActive == true) GreenSuccessContainer else RedErrorContainer
                        ) {
                            Text(
                                text = if (currentShift?.isShiftActive == true) "SHIFT AKTIF" else "TIDAK AKTIF",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentShift?.isShiftActive == true) GreenSuccess else RedError,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = OutlineWarm.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Mulai Shift", fontSize = 13.sp, color = TextMuted)
                        Text(formatShiftTime(currentShift?.displayStartTime), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Modal Awal", fontSize = 13.sp, color = TextMuted)
                        Text(displayModalAwal.toRupiahFormat(), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Penjualan", fontSize = 13.sp, color = TextMuted)
                        Text(shiftSales.toRupiahFormat(), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Kas Laci", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text((currentShift?.expectedCash ?: displayModalAwal).toRupiahFormat(), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = BtnDarkChocolate)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Tombol Tutup Shift
            Button(
                onClick = { showClockOutConfirmDialog = true },
                enabled = !isClockOutLoading && currentShift?.isShiftActive == true,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BtnDarkChocolate,
                    contentColor = BtnCreamWhite,
                    disabledContainerColor = BtnDarkChocolate.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = if (isClockOutLoading) "Memproses..." else "Tutup Shift",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = BtnCreamWhite
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
                border = BorderStroke(1.5.dp, BtnMocha),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = BtnDarkChocolate,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Keluar / Logout",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = BtnDarkChocolate
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Dialog Konfirmasi Tutup Shift
    if (showClockOutConfirmDialog) {
        val startingCash = displayModalAwal
        val expectedCash = currentShift?.expectedCash ?: startingCash
        val sales = (expectedCash - startingCash).coerceAtLeast(0.0)

        AlertDialog(
            onDismissRequest = { showClockOutConfirmDialog = false },
            title = {
                Text(
                    text = "Konfirmasi Tutup Shift",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = TextDark
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BgWarmTan.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Modal Awal:", fontSize = 12.sp, color = TextMuted)
                                Text(startingCash.toRupiahFormat(), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Penjualan:", fontSize = 12.sp, color = TextMuted)
                                Text(sales.toRupiahFormat(), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            HorizontalDivider(color = OutlineWarm.copy(alpha = 0.4f))
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("TOTAL SETORAN:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BtnDarkChocolate)
                                Text(expectedCash.toRupiahFormat(), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = BtnDarkChocolate)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Serahkan uang tunai fisik sesuai Total Setoran di atas kepada Guru Pembina.", fontSize = 12.sp, color = TextMuted)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClockOutConfirmDialog = false
                        viewModel.clockOut()
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BtnDarkChocolate, contentColor = BtnCreamWhite)
                ) {
                    Text("Setor & Tutup Shift", fontWeight = FontWeight.Bold, color = BtnCreamWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClockOutConfirmDialog = false }) {
                    Text("Batal", color = TextMuted)
                }
            },
            containerColor = CardCreamWhite,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

