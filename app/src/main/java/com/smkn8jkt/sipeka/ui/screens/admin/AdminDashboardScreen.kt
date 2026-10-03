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
import com.smkn8jkt.sipeka.ui.components.SipekaLottieEmptyState
import com.smkn8jkt.sipeka.ui.components.SipekaProductSkeletonCard
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.imePadding
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
import com.smkn8jkt.sipeka.ui.theme.AmberWarning
import com.smkn8jkt.sipeka.ui.theme.AmberWarningContainer
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
import com.smkn8jkt.sipeka.ui.theme.TextDark
import com.smkn8jkt.sipeka.ui.theme.TextLight
import com.smkn8jkt.sipeka.ui.theme.TextMuted
import kotlinx.coroutines.launch

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
        containerColor = com.smkn8jkt.sipeka.ui.theme.BgLightCanvas,
        topBar = {
            TopAppBar(
                title = {
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
                                    text = when (selectedTab) {
                                        0 -> "Dashboard Keuangan"
                                        1 -> "Riwayat Penjualan"
                                        else -> "Manajemen Akun"
                                    },
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextLight
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = BgWarmTan
                                ) {
                                    Text(
                                        text = "ADMIN",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = BtnDarkChocolate,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Guru Pembina PKK SMKN 8",
                                fontSize = 11.sp,
                                color = BgWarmTan.copy(alpha = 0.85f)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshAll() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = TextLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgDarkEspresso)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CardCreamWhite,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.MonetizationOn, contentDescription = "Keuangan", modifier = Modifier.size(22.dp)) },
                    label = { Text("Keuangan", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BtnDarkChocolate,
                        selectedTextColor = BtnDarkChocolate,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = com.smkn8jkt.sipeka.ui.theme.SegmentBg
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Penjualan", modifier = Modifier.size(22.dp)) },
                    label = { Text("Penjualan", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BtnDarkChocolate,
                        selectedTextColor = BtnDarkChocolate,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = com.smkn8jkt.sipeka.ui.theme.SegmentBg
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Group, contentDescription = "Pengguna", modifier = Modifier.size(22.dp)) },
                    label = { Text("Pengguna", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
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
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // CARD LABA SEKOLAH (Hero Card - Bento Box 24dp)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(BgDarkEspresso, BtnDarkChocolate)
                        )
                    )
                    .padding(24.dp)
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
                                    .background(BtnMocha.copy(alpha = 0.35f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = "Profit",
                                    tint = BtnCreamWhite,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Laba Bersih Kas PKK",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = BtnCreamWhite
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = BtnMocha.copy(alpha = 0.4f)
                        ) {
                            Text(
                                text = "REAL-TIME",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BtnCreamWhite,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = schoolProfit.toRupiahFormat(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BtnCreamWhite
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Bagian keuntungan SMKN 8 dari bagi hasil penitip & penjualan",
                        fontSize = 14.sp,
                        color = BgWarmTan.copy(alpha = 0.85f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

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
                    tint = TextDark,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Validasi Setoran Kasir",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = BtnDarkChocolate
            ) {
                Text(
                    text = "${pendingShifts.size} Shift",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BtnCreamWhite,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
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
                        colors = CheckboxDefaults.colors(
                            checkedColor = BtnDarkChocolate,
                            uncheckedColor = TextMuted
                        )
                    )
                    Text("Pilih Semua", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextDark)
                }

                if (selectedShifts.isNotEmpty()) {
                    Button(
                        onClick = {
                            onValidateMultipleShifts(selectedShifts.toList())
                            selectedShifts.clear()
                        },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BtnDarkChocolate,
                            contentColor = BtnCreamWhite
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                    ) {
                        Text("Validasi (${selectedShifts.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (pendingShifts.isEmpty()) {
            SipekaLottieEmptyState(
                title = "Semua Shift Tervalidasi",
                description = "Tidak ada setoran kasir yang menunggu validasi saat ini.",
                animationRes = com.smkn8jkt.sipeka.R.raw.lottie_success
            )
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

        Spacer(modifier = Modifier.height(24.dp))

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
                    tint = TextDark,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Bagi Hasil Penitip",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = BtnMocha
            ) {
                Text(
                    text = "${pendingPayouts.size} Penitip",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BtnCreamWhite,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
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
                        colors = CheckboxDefaults.colors(
                            checkedColor = BtnDarkChocolate,
                            uncheckedColor = TextMuted
                        )
                    )
                    Text("Pilih Semua", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextDark)
                }

                if (selectedPayouts.isNotEmpty()) {
                    Button(
                        onClick = {
                            onProcessMultiplePayouts(selectedPayouts.toList())
                            selectedPayouts.clear()
                        },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BtnDarkChocolate,
                            contentColor = BtnCreamWhite
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                    ) {
                        Text("Cairkan (${selectedPayouts.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (pendingPayouts.isEmpty()) {
            SipekaLottieEmptyState(
                title = "Pencairan Selesai",
                description = "Tidak ada pencairan dana penitip yang tertunda saat ini.",
                animationRes = com.smkn8jkt.sipeka.R.raw.lottie_success
            )
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

        Spacer(modifier = Modifier.height(120.dp))
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

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Total Omzet Card (Dark Espresso)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BgDarkEspresso),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                            text = "Total Omzet Penjualan",
                            fontSize = 12.sp,
                            color = BgWarmTan,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = totalOmzet.toRupiahFormat(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BtnCreamWhite
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = BtnMocha.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "${filteredOrders.size} Transaksi",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BtnCreamWhite,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari ID Transaksi...", fontSize = 14.sp, color = TextMuted) },
                singleLine = true,
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardCreamWhite,
                    unfocusedContainerColor = CardCreamWhite,
                    focusedBorderColor = BtnDarkChocolate,
                    unfocusedBorderColor = OutlineWarm.copy(alpha = 0.6f),
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp)
            )
        }

        if (isLoading) {
            items(3) {
                SipekaProductSkeletonCard()
            }
        } else if (filteredOrders.isEmpty()) {
            item {
                SipekaLottieEmptyState(
                    title = "Belum Ada Transaksi",
                    description = if (searchQuery.isNotBlank()) "Tidak ditemukan transaksi dengan kata kunci \"$searchQuery\"." else "Belum ada data transaksi penjualan yang tercatat.",
                    ctaText = if (searchQuery.isNotBlank()) "Reset Pencarian" else null,
                    onCtaClick = { searchQuery = "" }
                )
            }
        } else {
            groupedOrders.forEach { (headerTitle, ordersInGroup) ->
                item {
                    Surface(
                        color = BgDarkEspresso.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Shift",
                                tint = BtnDarkChocolate,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = headerTitle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
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
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // ACC Pendaftaran Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardCreamWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GroupAdd,
                            contentDescription = "Pending",
                            tint = TextDark,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Persetujuan Akun (ACC)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = BtnMocha
                    ) {
                        Text(
                            text = "${pendingUsers.size} Antrean",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BtnCreamWhite,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (pendingUsers.isEmpty()) {
                    Text(
                        text = "Belum ada pendaftaran akun baru.",
                        fontSize = 14.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(vertical = 8.dp)
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

        // Tambah Kasir Button
        Button(
            onClick = onOpenAddKasir,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BtnDarkChocolate,
                contentColor = BtnCreamWhite
            ),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PersonAdd,
                contentDescription = "Add",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "+ Tambah Petugas Kasir Baru",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Daftar Semua User
        Text(
            text = "Daftar Pengguna Sistem (${allUsers.size}) • Klik untuk detail/edit",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardCreamWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (allUsers.isEmpty()) {
                    SipekaLottieEmptyState(
                        title = "Belum Ada Pengguna",
                        description = "Belum ada data pengguna yang terdaftar di sistem."
                    )
                } else {
                    allUsers.forEachIndexed { index, user ->
                        UserListRow(
                            user = user,
                            onClick = { onSelectUserForDetail(user) }
                        )
                        if (index < allUsers.size - 1) {
                            HorizontalDivider(color = OutlineWarm.copy(alpha = 0.25f))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Logout Button
        OutlinedButton(
            onClick = onLogout,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, BtnDarkChocolate),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Logout",
                tint = BtnDarkChocolate,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Keluar dari Akun (Logout)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = BtnDarkChocolate
            )
        }

        Spacer(modifier = Modifier.height(120.dp))
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) BtnMocha.copy(alpha = 0.12f) else CardCreamWhite
        ),
        border = BorderStroke(
            1.dp,
            if (isSelected) BtnDarkChocolate else OutlineWarm.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggleSelect() },
                colors = CheckboxDefaults.colors(
                    checkedColor = BtnDarkChocolate,
                    uncheckedColor = TextMuted
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = shift.displayKasirName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = AmberWarningContainer
                    ) {
                        Text(
                            text = "BELUM DIVALIDASI",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberWarning,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Selesai: ${shift.displayTime}",
                    fontSize = 12.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Setoran:", fontSize = 12.sp, color = TextMuted)
                        Text(
                            text = (shift.expectedCash ?: 0.0).toRupiahFormat(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BtnDarkChocolate
                        )
                    }

                    Button(
                        onClick = onValidate,
                        enabled = !isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BtnDarkChocolate,
                            contentColor = BtnCreamWhite
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Validate",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Validasi", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) BtnMocha.copy(alpha = 0.12f) else CardCreamWhite
        ),
        border = BorderStroke(
            1.dp,
            if (isSelected) BtnDarkChocolate else OutlineWarm.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggleSelect() },
                colors = CheckboxDefaults.colors(
                    checkedColor = BtnDarkChocolate,
                    uncheckedColor = TextMuted
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = payout.displayPenitipName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = RedErrorContainer
                    ) {
                        Text(
                            text = "BELUM DIBAYAR",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = RedError,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
                        Text("Bagi Hasil Bersih:", fontSize = 12.sp, color = TextMuted)
                        Text(
                            text = payout.displayAmount.toRupiahFormat(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BtnDarkChocolate
                        )
                    }

                    Button(
                        onClick = onProcessPayout,
                        enabled = !isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BtnMocha,
                            contentColor = BtnCreamWhite
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                    ) {
                        Text("Cairkan Uang", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                color = TextDark
            )
            Text(
                text = "ID: ${user.nisnNip ?: "-"} • Klik untuk edit",
                fontSize = 11.sp,
                color = TextMuted
            )
        }

        val roleColor = when ((user.role ?: "").lowercase()) {
            "admin", "guru", "pembina" -> RedError
            "kasir" -> BtnDarkChocolate
            "penitip", "penjual" -> GreenSuccess
            else -> TextMuted
        }

        val roleBg = when ((user.role ?: "").lowercase()) {
            "admin", "guru", "pembina" -> RedErrorContainer
            "kasir" -> BgDarkEspresso.copy(alpha = 0.12f)
            "penitip", "penjual" -> GreenSuccessContainer
            else -> BgDarkEspresso.copy(alpha = 0.08f)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = roleBg
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
                tint = BtnMocha,
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
                    color = TextDark
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Clear, contentDescription = "Close", tint = TextMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
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
                Text("Batal", color = TextMuted)
            }
        },
        containerColor = CardCreamWhite,
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
fun AdminTransactionCard(
    order: OrderData,
    onClick: () -> Unit
) {
    val shortId = order.id?.take(8)?.uppercase() ?: "-"
    val isCompleted = (order.status ?: "").equals("COMPLETED", ignoreCase = true) ||
            (order.status ?: "").equals("SUKSES", ignoreCase = true)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardCreamWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.4f))
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = BgWarmTan.copy(alpha = 0.35f)
                    ) {
                        Text(
                            text = "#$shortId",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BtnDarkChocolate,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = formatOrderDate(order.displayCreatedAt),
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isCompleted) GreenSuccessContainer else BgWarmTan.copy(alpha = 0.3f)
                ) {
                    Text(
                        text = if (isCompleted) "SUKSES" else (order.status ?: "SELESAI").uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) GreenSuccess else BtnDarkChocolate,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Klik rincian item",
                    fontSize = 14.sp,
                    color = BtnMocha,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = (order.totalAmount ?: 0.0).toRupiahFormat(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BtnDarkChocolate
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
        shape = RoundedCornerShape(16.dp),
        color = BgWarmTan.copy(alpha = 0.35f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name ?: "User Baru",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = "ID: ${user.nisnNip ?: "-"} • ${(user.role ?: "Siswa").uppercase()}",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            Button(
                onClick = onApprove,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BtnDarkChocolate,
                    contentColor = BtnCreamWhite
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.defaultMinSize(minHeight = 48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "ACC",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("ACC", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                color = TextDark
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
                Text("Batal", color = TextMuted)
            }
        },
        containerColor = CardCreamWhite,
        shape = RoundedCornerShape(18.dp)
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
                        tint = BtnDarkChocolate,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Detail Transaksi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextDark
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Clear, contentDescription = "Close", tint = TextMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = BgWarmTan.copy(alpha = 0.25f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("ID Order:", fontSize = 11.sp, color = TextMuted)
                            Text("#${order.id ?: "-"}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Waktu:", fontSize = 11.sp, color = TextMuted)
                            Text(formatOrderDate(order.displayCreatedAt), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Rincian Item:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(6.dp))

                val displayItems = order.displayItems
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BtnDarkChocolate, modifier = Modifier.size(20.dp))
                    }
                } else if (displayItems.isEmpty()) {
                    Text(
                        text = "Detail item tidak tersedia",
                        fontSize = 12.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CardCreamWhite),
                        border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.4f))
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
                                            color = TextDark
                                        )
                                        Text(
                                            text = "$qty x ${price.toRupiahFormat()}",
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }
                                    Text(
                                        text = subtotal.toRupiahFormat(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BtnDarkChocolate
                                    )
                                }
                                if (index < displayItems.size - 1) {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = OutlineWarm.copy(alpha = 0.25f))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = OutlineWarm.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text((order.totalAmount ?: 0.0).toRupiahFormat(), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BtnDarkChocolate)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BtnDarkChocolate,
                    contentColor = BtnCreamWhite
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tutup", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = CardCreamWhite,
        shape = RoundedCornerShape(18.dp)
    )
}
