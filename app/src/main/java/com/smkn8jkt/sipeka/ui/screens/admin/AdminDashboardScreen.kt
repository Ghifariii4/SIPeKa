package com.smkn8jkt.sipeka.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.smkn8jkt.sipeka.data.model.OrderData
import com.smkn8jkt.sipeka.data.model.PayoutResponse
import com.smkn8jkt.sipeka.data.model.ShiftValidationResponse
import com.smkn8jkt.sipeka.data.model.UserData
import com.smkn8jkt.sipeka.data.remote.TokenManager
import com.smkn8jkt.sipeka.navigation.Screen
import com.smkn8jkt.sipeka.ui.components.PrimaryButton
import com.smkn8jkt.sipeka.ui.components.SipekaDropdownField
import com.smkn8jkt.sipeka.ui.components.SipekaPasswordField
import com.smkn8jkt.sipeka.ui.components.SipekaTextField
import com.smkn8jkt.sipeka.ui.screens.auth.AuthViewModel
import com.smkn8jkt.sipeka.ui.screens.pos.formatDayGroupHeader
import com.smkn8jkt.sipeka.ui.screens.pos.formatOrderDate
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

    var selectedTab by remember { mutableIntStateOf(0) }

    val schoolProfit by viewModel.schoolProfit.collectAsState()
    val pendingShifts by viewModel.pendingShifts.collectAsState()
    val pendingPayouts by viewModel.pendingPayouts.collectAsState()
    val pendingUsers by viewModel.pendingUsers.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val selectedDetail by viewModel.selectedOrderDetail.collectAsState()
    val selectedUserDetail by viewModel.selectedUserDetail.collectAsState()
    val isDetailLoading by viewModel.isDetailLoading.collectAsState()
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
                            text = when (selectedTab) {
                                0 -> "Dashboard Keuangan"
                                1 -> "Riwayat Transaksi Penjualan"
                                else -> "Manajemen Pengguna & Akun"
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBrown
                        )
                        Text(
                            text = "Guru Pembina Kantin SMKN 8",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshAll() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = PrimaryOrange
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CardWhite)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CardWhite,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.MonetizationOn, contentDescription = "Keuangan") },
                    label = { Text("Keuangan", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryOrange,
                        selectedTextColor = PrimaryOrange,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = PrimaryOrange.copy(alpha = 0.12f)
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Penjualan") },
                    label = { Text("Penjualan", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryOrange,
                        selectedTextColor = PrimaryOrange,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = PrimaryOrange.copy(alpha = 0.12f)
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Group, contentDescription = "Pengguna") },
                    label = { Text("Pengguna", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryOrange,
                        selectedTextColor = PrimaryOrange,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = PrimaryOrange.copy(alpha = 0.12f)
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> AdminFinanceTab(
                    schoolProfit = schoolProfit,
                    pendingShifts = pendingShifts,
                    pendingPayouts = pendingPayouts,
                    isLoading = isLoading,
                    onValidateShift = { viewModel.validateShift(it) },
                    onValidateMultipleShifts = { viewModel.validateMultipleShifts(it) },
                    onProcessPayout = { viewModel.processPayout(it) },
                    onProcessMultiplePayouts = { viewModel.processMultiplePayouts(it) }
                )
                1 -> AdminTransactionsTab(
                    orders = allOrders,
                    isLoading = isLoading,
                    onSelectOrder = { viewModel.selectOrderForDetail(it) }
                )
                2 -> AdminUsersTab(
                    pendingUsers = pendingUsers,
                    allUsers = allUsers,
                    onApproveUser = { viewModel.approveUser(it) },
                    onSelectUserForDetail = { viewModel.selectUserForDetail(it) },
                    onOpenAddKasir = { showAddKasirDialog = true },
                    onLogout = {
                        scope.launch {
                            tokenManager?.clearToken()
                            authViewModel?.resetState()
                            navController.navigate(Screen.Login) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
            }
        }
    }

    // DIALOG TAMBAH KASIR
    if (showAddKasirDialog) {
        AddKasirDialog(
            onDismiss = { showAddKasirDialog = false },
            onConfirm = { nip, name, pass ->
                viewModel.addKasirUser(nip, name, pass)
                showAddKasirDialog = false
            }
        )
    }

    // DIALOG EDIT/DETAIL USER
    selectedUserDetail?.let { user ->
        EditUserDialog(
            user = user,
            onDismiss = { viewModel.clearUserDetail() },
            onSave = { id, name, nip, role ->
                viewModel.updateUser(id, name, nip, role)
            }
        )
    }

    // DIALOG DETAIL TRANSAKSI
    selectedDetail?.let { order ->
        OrderDetailDialog(
            order = order,
            isLoading = isDetailLoading,
            onDismiss = { viewModel.clearOrderDetail() }
        )
    }
}

// TAB 1: KEUANGAN (LABA, VALIDASI SHIFT, BAGI HASIL)
@Composable
fun AdminFinanceTab(
    schoolProfit: Double,
    pendingShifts: List<ShiftValidationResponse>,
    pendingPayouts: List<PayoutResponse>,
    isLoading: Boolean,
    onValidateShift: (String) -> Unit,
    onValidateMultipleShifts: (List<String>) -> Unit,
    onProcessPayout: (String) -> Unit,
    onProcessMultiplePayouts: (List<String>) -> Unit
) {
    val selectedShifts = remember { mutableStateListOf<String>() }
    val selectedPayouts = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // CARD LABA SEKOLAH
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(GreenGoldStart, GreenGoldEnd)
                        )
                    )
                    .padding(18.dp)
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
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = "Profit",
                                    tint = Color(0xFFFFD54F),
                                    modifier = Modifier.size(22.dp)
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
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "REAL-TIME",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = schoolProfit.toRupiahFormat(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // VALIDASI SETORAN KASIR HEADER
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
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
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

        // BATCH ACTION VALIDASI SHIFT
        if (pendingShifts.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isAllSelected = selectedShifts.size == pendingShifts.size && pendingShifts.isNotEmpty()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        if (isAllSelected) {
                            selectedShifts.clear()
                        } else {
                            selectedShifts.clear()
                            selectedShifts.addAll(pendingShifts.map { it.id })
                        }
                    }
                ) {
                    Checkbox(
                        checked = isAllSelected,
                        onCheckedChange = { checked ->
                            if (checked) {
                                selectedShifts.clear()
                                selectedShifts.addAll(pendingShifts.map { it.id })
                            } else {
                                selectedShifts.clear()
                            }
                        },
                        colors = CheckboxDefaults.colors(checkedColor = PrimaryOrange)
                    )
                    Text("Pilih Semua", fontSize = 12.sp, color = DarkBrown)
                }

                if (selectedShifts.isNotEmpty()) {
                    Button(
                        onClick = {
                            onValidateMultipleShifts(selectedShifts.toList())
                            selectedShifts.clear()
                        },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Validasi (${selectedShifts.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (pendingShifts.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
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
                        .padding(14.dp)
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                pendingShifts.forEach { shift ->
                    val isSelected = selectedShifts.contains(shift.id)
                    ShiftValidationCard(
                        shift = shift,
                        isSelected = isSelected,
                        onToggleSelect = {
                            if (isSelected) selectedShifts.remove(shift.id)
                            else selectedShifts.add(shift.id)
                        },
                        onValidate = { onValidateShift(shift.id) },
                        isLoading = isLoading
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // BAGI HASIL PENITIP HEADER
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
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
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

        // BATCH ACTION CAIRKAN PENITIP
        if (pendingPayouts.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isAllPayoutSelected = selectedPayouts.size == pendingPayouts.size && pendingPayouts.isNotEmpty()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        if (isAllPayoutSelected) {
                            selectedPayouts.clear()
                        } else {
                            selectedPayouts.clear()
                            selectedPayouts.addAll(pendingPayouts.map { it.penitipId })
                        }
                    }
                ) {
                    Checkbox(
                        checked = isAllPayoutSelected,
                        onCheckedChange = { checked ->
                            if (checked) {
                                selectedPayouts.clear()
                                selectedPayouts.addAll(pendingPayouts.map { it.penitipId })
                            } else {
                                selectedPayouts.clear()
                            }
                        },
                        colors = CheckboxDefaults.colors(checkedColor = PrimaryOrange)
                    )
                    Text("Pilih Semua", fontSize = 12.sp, color = DarkBrown)
                }

                if (selectedPayouts.isNotEmpty()) {
                    Button(
                        onClick = {
                            onProcessMultiplePayouts(selectedPayouts.toList())
                            selectedPayouts.clear()
                        },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Cairkan (${selectedPayouts.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (pendingPayouts.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
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
                        .padding(14.dp)
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                pendingPayouts.forEach { payout ->
                    val isSelected = selectedPayouts.contains(payout.penitipId)
                    PayoutCard(
                        payout = payout,
                        isSelected = isSelected,
                        onToggleSelect = {
                            if (isSelected) selectedPayouts.remove(payout.penitipId)
                            else selectedPayouts.add(payout.penitipId)
                        },
                        onProcessPayout = { onProcessPayout(payout.penitipId) },
                        isLoading = isLoading
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

// TAB 2: RIWAYAT TRANSAKSI PENJUALAN
@Composable
fun AdminTransactionsTab(
    orders: List<OrderData>,
    isLoading: Boolean,
    onSelectOrder: (OrderData) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredOrders = remember(orders, searchQuery) {
        if (searchQuery.isBlank()) orders
        else orders.filter { order ->
            (order.id ?: "").contains(searchQuery, ignoreCase = true) ||
                    (order.status ?: "").contains(searchQuery, ignoreCase = true)
        }
    }

    val groupedOrders = remember(filteredOrders) {
        filteredOrders.groupBy { formatDayGroupHeader(it.displayCreatedAt, it.shiftId) }
    }

    val totalOmzet = remember(filteredOrders) {
        filteredOrders.sumOf { it.totalAmount ?: 0.0 }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Total Omzet Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryOrange),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Omzet Penjualan",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = totalOmzet.toRupiahFormat(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "${filteredOrders.size} Transaksi",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari ID Transaksi...", fontSize = 13.sp, color = Color.Gray) },
            singleLine = true,
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                    }
                }
            },
            shape = CircleShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardWhite,
                unfocusedContainerColor = CardWhite,
                focusedBorderColor = PrimaryOrange,
                unfocusedBorderColor = Color(0xFFE0E0E0)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Memuat data penjualan...", color = Color.Gray, fontSize = 14.sp)
            }
        } else if (filteredOrders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Belum ada data transaksi penjualan.", color = Color.Gray, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                groupedOrders.forEach { (headerTitle, ordersInGroup) ->
                    item {
                        Surface(
                            color = PrimaryOrange.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp, bottom = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Shift",
                                    tint = PrimaryOrange,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = headerTitle,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkBrown
                                )
                            }
                        }
                    }

                    items(ordersInGroup, key = { it.id ?: "" }) { order ->
                        AdminTransactionCard(
                            order = order,
                            onClick = { onSelectOrder(order) }
                        )
                    }
                }
            }
        }
    }
}

// TAB 3: MANAJEMEN PENGGUNA (ACC PENDAFTARAN & DAFTAR USER EDIT)
@Composable
fun AdminUsersTab(
    pendingUsers: List<UserData>,
    allUsers: List<UserData>,
    onApproveUser: (String) -> Unit,
    onSelectUserForDetail: (UserData) -> Unit,
    onOpenAddKasir: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // ACC Pendaftaran Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GroupAdd,
                            contentDescription = "Pending",
                            tint = DarkBrown,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Persetujuan Akun (ACC)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBrown
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
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
                        text = "Belum ada pendaftaran akun baru.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        pendingUsers.forEach { user ->
                            PendingUserRow(
                                user = user,
                                onApprove = { onApproveUser(user.id ?: "") }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tambah Kasir Button & Card
        Button(
            onClick = onOpenAddKasir,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.PersonAdd,
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

        Spacer(modifier = Modifier.height(16.dp))

        // Daftar Semua User
        Text(
            text = "Daftar Pengguna Sistem (${allUsers.size}) • Klik untuk detail/edit",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = DarkBrown
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (allUsers.isEmpty()) {
                    Text("Belum ada data pengguna.", fontSize = 12.sp, color = Color.Gray)
                } else {
                    allUsers.forEachIndexed { index, user ->
                        UserListRow(
                            user = user,
                            onClick = { onSelectUserForDetail(user) }
                        )
                        if (index < allUsers.size - 1) {
                            HorizontalDivider(color = Color(0xFFF0F0F0))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Logout
        OutlinedButton(
            onClick = onLogout,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color.Gray),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Logout",
                tint = Color.DarkGray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Logout",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
fun ShiftValidationCard(
    shift: ShiftValidationResponse,
    isSelected: Boolean,
    onToggleSelect: () -> Unit,
    onValidate: () -> Unit,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryOrange.copy(alpha = 0.08f) else CardWhite
        ),
        border = if (isSelected) BorderStroke(1.dp, PrimaryOrange) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggleSelect() },
                colors = CheckboxDefaults.colors(checkedColor = PrimaryOrange)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = shift.displayKasirName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBrown
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFFF3E0)
                    ) {
                        Text(
                            text = "BELUM DIVALIDASI",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Selesai: ${shift.displayTime}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Setoran:", fontSize = 11.sp, color = Color.Gray)
                        Text(
                            text = (shift.expectedCash ?: 0.0).toRupiahFormat(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryOrange
                        )
                    }

                    Button(
                        onClick = onValidate,
                        enabled = !isLoading,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Validate",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Validasi", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PayoutCard(
    payout: PayoutResponse,
    isSelected: Boolean,
    onToggleSelect: () -> Unit,
    onProcessPayout: () -> Unit,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryOrange.copy(alpha = 0.08f) else CardWhite
        ),
        border = if (isSelected) BorderStroke(1.dp, PrimaryOrange) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggleSelect() },
                colors = CheckboxDefaults.colors(checkedColor = PrimaryOrange)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = payout.displayPenitipName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBrown
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFFEBEE)
                    ) {
                        Text(
                            text = "BELUM DIBAYAR",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC62828),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Bagi Hasil Bersih:", fontSize = 11.sp, color = Color.Gray)
                        Text(
                            text = payout.displayAmount.toRupiahFormat(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryOrange
                        )
                    }

                    Button(
                        onClick = onProcessPayout,
                        enabled = !isLoading,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Cairkan Uang", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun UserListRow(
    user: UserData,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.name ?: "User",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBrown
            )
            Text(
                text = "ID: ${user.nisnNip ?: "-"} • Klik untuk edit",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }

        val roleColor = when ((user.role ?: "").lowercase()) {
            "admin", "guru", "pembina" -> Color(0xFFC62828)
            "kasir" -> PrimaryOrange
            "penitip", "penjual" -> Color(0xFF2E7D32)
            else -> Color.DarkGray
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = roleColor.copy(alpha = 0.12f)
            ) {
                Text(
                    text = (user.role ?: "Siswa").uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = roleColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit User",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// DIALOG EDIT/DETAIL USER
@Composable
fun EditUserDialog(
    user: UserData,
    onDismiss: () -> Unit,
    onSave: (id: String, name: String, nisnNip: String, role: String) -> Unit
) {
    var nameInput by remember(user) { mutableStateOf(user.name ?: "") }
    var nipInput by remember(user) { mutableStateOf(user.nisnNip ?: "") }
    var selectedRole by remember(user) { mutableStateOf(user.role ?: "kasir") }

    val roleOptions = remember { listOf("kasir", "penitip", "siswa", "admin") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Detail & Edit Pengguna",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = DarkBrown
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Clear, contentDescription = "Close", tint = Color.Gray)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SipekaTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = "Nama Pengguna",
                    placeholder = "Nama Lengkap"
                )

                SipekaTextField(
                    value = nipInput,
                    onValueChange = { nipInput = it },
                    label = "NIP / NISN",
                    placeholder = "Nomor Induk"
                )

                SipekaDropdownField(
                    options = roleOptions,
                    selectedOption = selectedRole,
                    onOptionSelected = { selectedRole = it },
                    label = "Peran / Akses Role"
                )
            }
        },
        confirmButton = {
            PrimaryButton(
                text = "Simpan Perubahan",
                enabled = nameInput.isNotBlank(),
                onClick = {
                    onSave(user.id ?: "", nameInput, nipInput, selectedRole)
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

@Composable
fun AdminTransactionCard(
    order: OrderData,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${order.id ?: "-"}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = DarkBrown
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFE8F5E9)
                ) {
                    Text(
                        text = (order.status ?: "COMPLETED").uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formatOrderDate(order.displayCreatedAt),
                fontSize = 11.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Klik rincian item",
                    fontSize = 11.sp,
                    color = PrimaryOrange,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = (order.totalAmount ?: 0.0).toRupiahFormat(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryOrange
                )
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
        shape = RoundedCornerShape(8.dp),
        color = SurfaceBeige,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name ?: "User Baru",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown
                )
                Text(
                    text = "ID: ${user.nisnNip ?: "-"} • ${(user.role ?: "Siswa").uppercase()}",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }

            Button(
                onClick = onApprove,
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                modifier = Modifier.height(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "ACC",
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("ACC", fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
                fontSize = 17.sp,
                color = DarkBrown
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SipekaTextField(
                    value = nipInput,
                    onValueChange = { nipInput = it },
                    label = "NIP / ID Kasir",
                    placeholder = "Contoh: 19820101"
                )

                SipekaTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = "Nama Petugas",
                    placeholder = "Contoh: Petugas Kasir 2"
                )

                SipekaPasswordField(
                    value = passInput,
                    onValueChange = { passInput = it },
                    label = "Password",
                    placeholder = "Password minimal 6 karakter"
                )
            }
        },
        confirmButton = {
            PrimaryButton(
                text = "Simpan Kasir",
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
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun OrderDetailDialog(
    order: OrderData,
    isLoading: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = "Detail",
                        tint = PrimaryOrange,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Detail Transaksi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DarkBrown
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Clear, contentDescription = "Close", tint = Color.Gray)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceBeige,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("ID Order:", fontSize = 11.sp, color = Color.Gray)
                            Text("#${order.id ?: "-"}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkBrown)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Waktu:", fontSize = 11.sp, color = Color.Gray)
                            Text(formatOrderDate(order.displayCreatedAt), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DarkBrown)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Rincian Item:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown
                )

                Spacer(modifier = Modifier.height(6.dp))

                val displayItems = order.displayItems
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryOrange, modifier = Modifier.size(20.dp))
                    }
                } else if (displayItems.isEmpty()) {
                    Text(
                        text = "Detail item tidak tersedia",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            displayItems.forEachIndexed { index, item ->
                                val price = item.displayPrice
                                val qty = item.quantity ?: 1
                                val subtotal = price * qty

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.displayProductName,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = DarkBrown
                                        )
                                        Text(
                                            text = "$qty x ${price.toRupiahFormat()}",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    Text(
                                        text = subtotal.toRupiahFormat(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryOrange
                                    )
                                }
                                if (index < displayItems.size - 1) {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0xFFF0F0F0))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFE0E0E0))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkBrown)
                    Text((order.totalAmount ?: 0.0).toRupiahFormat(), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryOrange)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tutup", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = CardWhite,
        shape = RoundedCornerShape(16.dp)
    )
}
