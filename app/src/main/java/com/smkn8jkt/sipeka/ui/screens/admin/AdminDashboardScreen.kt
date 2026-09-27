package com.smkn8jkt.sipeka.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.smkn8jkt.sipeka.data.model.PayoutResponse
import com.smkn8jkt.sipeka.data.model.ShiftValidationResponse
import com.smkn8jkt.sipeka.data.model.UserData
import com.smkn8jkt.sipeka.data.remote.TokenManager
import com.smkn8jkt.sipeka.navigation.Screen
import com.smkn8jkt.sipeka.ui.components.PrimaryButton
import com.smkn8jkt.sipeka.ui.components.SipekaPasswordField
import com.smkn8jkt.sipeka.ui.components.SipekaTextField
import com.smkn8jkt.sipeka.ui.screens.auth.AuthViewModel
import com.smkn8jkt.sipeka.ui.screens.pos.toRupiahFormat
import kotlinx.coroutines.launch

private val DarkBrown = Color(0xFF3E2723)
private val PrimaryOrange = Color(0xFFFF7043)
private val SurfaceBeige = Color(0xFFFAF7F4)
private val CardWhite = Color(0xFFFFFFFF)
private val GreenGoldStart = Color(0xFF1B5E20)
private val GreenGoldEnd = Color(0xFF2E7D32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: AdminViewModel = viewModel(),
    authViewModel: AuthViewModel? = null,
    tokenManager: TokenManager? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val schoolProfit by viewModel.schoolProfit.collectAsState()
    val pendingShifts by viewModel.pendingShifts.collectAsState()
    val pendingPayouts by viewModel.pendingPayouts.collectAsState()
    val pendingUsers by viewModel.pendingUsers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var showAddKasirDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.refreshAll()
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    Scaffold(
        containerColor = SurfaceBeige,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Dashboard Guru Pembina",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBrown
                        )
                        Text(
                            text = "Sistem Manajemen Keuangan Kantin",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshAll() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Data",
                            tint = PrimaryOrange
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CardWhite)
            )
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

            // BAGIAN 1: CARD LABA SEKOLAH
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(GreenGoldStart, GreenGoldEnd)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MonetizationOn,
                                        contentDescription = "Profit",
                                        tint = Color(0xFFFFD54F),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Laba Bersih Hari Ini",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "REAL-TIME",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = schoolProfit.toRupiahFormat(),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Akumulasi dari margin_snapshot transaksi penjualan sekolah",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // BAGIAN 2: VALIDASI SETORAN KASIR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PointOfSale,
                        contentDescription = "Validation",
                        tint = DarkBrown,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Validasi Setoran Kasir",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBrown
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryOrange.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "${pendingShifts.size} Shift",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryOrange,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (pendingShifts.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                ) {
                    Text(
                        text = "Semua setoran shift kasir telah divalidasi.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    pendingShifts.forEach { shift ->
                        ShiftValidationCard(
                            shift = shift,
                            onValidate = { viewModel.validateShift(shift.id) },
                            isLoading = isLoading
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // BAGIAN 3: BAGI HASIL PENITIP
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = "Payouts",
                        tint = DarkBrown,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Bagi Hasil Penitip",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBrown
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryOrange.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "${pendingPayouts.size} Penitip",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryOrange,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (pendingPayouts.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                ) {
                    Text(
                        text = "Tidak ada pencairan dana penitip yang tertunda.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    pendingPayouts.forEach { payout ->
                        PayoutCard(
                            payout = payout,
                            onProcessPayout = { viewModel.processPayout(payout.penitipId) },
                            isLoading = isLoading
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // BAGIAN 4: ACC PENDAFTARAN & MANAJEMEN USER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GroupAdd,
                        contentDescription = "User Mgmt",
                        tint = DarkBrown,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Manajemen & ACC Pendaftaran",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBrown
                    )
                }

                IconButton(
                    onClick = { showAddKasirDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Tambah Kasir",
                        tint = PrimaryOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // User Approval Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Persetujuan Akun Pendaftaran Siswa/Penitip",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBrown
                        )
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PrimaryOrange.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "${pendingUsers.size} Antrean",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryOrange,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (pendingUsers.isEmpty()) {
                        Text(
                            text = "Belum ada pendaftaran akun baru yang membutuhkan persetujuan.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            pendingUsers.forEach { user ->
                                PendingUserRow(
                                    user = user,
                                    onApprove = { viewModel.approveUser(user.id ?: "") }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showAddKasirDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+ Tambah Petugas Kasir Baru",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button Admin
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
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.Gray),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Logout Akun Admin Guru",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    // DIALOG TAMBAH USER KASIR BARU
    if (showAddKasirDialog) {
        AddKasirDialog(
            onDismiss = { showAddKasirDialog = false },
            onConfirm = { nip, name, pass ->
                viewModel.addKasirUser(nip, name, pass)
                showAddKasirDialog = false
            }
        )
    }
}

@Composable
fun ShiftValidationCard(
    shift: ShiftValidationResponse,
    onValidate: () -> Unit,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = shift.displayKasirName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFFFF3E0)
                ) {
                    Text(
                        text = "BELUM DIVALIDASI",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Selesai Shift: ${shift.displayTime}",
                fontSize = 11.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Setoran Laci:", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = (shift.expectedCash ?: 0.0).toRupiahFormat(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryOrange
                    )
                }

                Button(
                    onClick = onValidate,
                    enabled = !isLoading,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Validate",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Validasi Setoran", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PayoutCard(
    payout: PayoutResponse,
    onProcessPayout: () -> Unit,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = payout.displayPenitipName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFFFEBEE)
                ) {
                    Text(
                        text = "BELUM DIBAYAR",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC62828),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Bagi Hasil Bersih:", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = payout.displayAmount.toRupiahFormat(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryOrange
                    )
                }

                Button(
                    onClick = onProcessPayout,
                    enabled = !isLoading,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("Cairkan Uang", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PendingUserRow(
    user: UserData,
    onApprove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = SurfaceBeige,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name ?: "User Baru",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown
                )
                Text(
                    text = "NIP/NISN: ${user.nisnNip ?: "-"} • Peran: ${(user.role ?: "Siswa").uppercase()}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Button(
                onClick = onApprove,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "ACC",
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("ACC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AddKasirDialog(
    onDismiss: () -> Unit,
    onConfirm: (nip: String, name: String, pass: String) -> Unit
) {
    var nipInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var passInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Tambah Petugas Kasir",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = DarkBrown
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Daftarkan akun kasir baru untuk bertugas di kantin:",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                SipekaTextField(
                    value = nipInput,
                    onValueChange = { nipInput = it },
                    label = "NIP / ID Kasir",
                    placeholder = "Contoh: 19820101"
                )

                SipekaTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = "Nama Lengkap Petugas",
                    placeholder = "Contoh: Kasir Stand 2"
                )

                SipekaPasswordField(
                    value = passInput,
                    onValueChange = { passInput = it },
                    label = "Password Akun",
                    placeholder = "Password minimal 6 karakter"
                )
            }
        },
        confirmButton = {
            PrimaryButton(
                text = "Simpan Akun Kasir",
                enabled = nipInput.isNotBlank() && nameInput.isNotBlank() && passInput.isNotBlank(),
                onClick = {
                    onConfirm(nipInput, nameInput, passInput)
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = Color.Gray)
            }
        },
        containerColor = CardWhite,
        shape = RoundedCornerShape(18.dp)
    )
}
