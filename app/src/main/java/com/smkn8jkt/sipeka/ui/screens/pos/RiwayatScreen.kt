package com.smkn8jkt.sipeka.ui.screens.pos

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.smkn8jkt.sipeka.data.model.OrderData
import com.smkn8jkt.sipeka.navigation.Screen
import com.smkn8jkt.sipeka.ui.theme.BgDarkEspresso
import com.smkn8jkt.sipeka.ui.theme.BgLightCanvas
import com.smkn8jkt.sipeka.ui.theme.BgWarmTan
import com.smkn8jkt.sipeka.ui.theme.BorderStitch
import com.smkn8jkt.sipeka.ui.theme.BtnCreamWhite
import com.smkn8jkt.sipeka.ui.theme.BtnDarkChocolate
import com.smkn8jkt.sipeka.ui.theme.BtnMocha
import com.smkn8jkt.sipeka.ui.theme.CardCreamWhite
import com.smkn8jkt.sipeka.ui.theme.GreenSuccess
import com.smkn8jkt.sipeka.ui.theme.GreenSuccessContainer
import com.smkn8jkt.sipeka.ui.theme.SegmentBg
import com.smkn8jkt.sipeka.ui.theme.TextDark
import com.smkn8jkt.sipeka.ui.theme.TextLight
import com.smkn8jkt.sipeka.ui.theme.TextMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun formatOrderDate(isoString: String?): String {
    if (isoString.isNullOrBlank()) return "-"
    return try {
        val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        inputFormatter.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormatter.parse(isoString)
        val outputFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm 'WIB'", Locale.getDefault())
        outputFormatter.timeZone = TimeZone.getDefault()
        if (date != null) outputFormatter.format(date) else isoString
    } catch (_: Exception) {
        isoString
    }
}

fun formatDayGroupHeader(isoString: String?, shiftId: String?): String {
    // Potong shift ID menjadi maks 6 karakter supaya header tidak boros ruang
    val shortShiftId = shiftId?.take(6)?.uppercase()
    if (isoString.isNullOrBlank()) return if (!shortShiftId.isNullOrBlank()) "Shift #$shortShiftId" else "Shift Aktif"
    return try {
        val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        inputFormatter.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormatter.parse(isoString)
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val orderDayStr = date?.let { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it) }

        val dayLabel = when (orderDayStr) {
            todayStr -> "Hari Ini"
            else -> date?.let { SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()).format(it) } ?: "Tanggal -"
        }

        val shiftLabel = if (!shortShiftId.isNullOrBlank()) " • Shift #$shortShiftId" else ""
        "$dayLabel$shiftLabel"
    } catch (_: Exception) {
        if (!shortShiftId.isNullOrBlank()) "Shift #$shortShiftId" else "Shift Aktif"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: PosViewModel = viewModel()
) {
    val historyList by viewModel.orderHistory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedDetail by viewModel.selectedOrderDetail.collectAsState()
    val isDetailLoading by viewModel.isDetailLoading.collectAsState()
    val currentShiftData by viewModel.currentShiftData.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterTab by remember { mutableStateOf("Shift Aktif") }

    val filterTabs = remember { listOf("Shift Aktif", "Hari Ini", "Semua") }

    LaunchedEffect(Unit) {
        viewModel.fetchHistory()
        viewModel.fetchCurrentShift()
    }

    // Filter daftar berdasarkan tab dan pencarian
    val filteredList = remember(historyList, searchQuery, selectedFilterTab, currentShiftData) {
        val activeShiftId = currentShiftData?.id
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        historyList.filter { order ->
            val matchesSearch = searchQuery.isBlank() ||
                    (order.id ?: "").contains(searchQuery, ignoreCase = true) ||
                    (order.status ?: "").contains(searchQuery, ignoreCase = true)

            val matchesTab = when (selectedFilterTab) {
                "Shift Aktif" -> activeShiftId.isNullOrBlank() || order.shiftId == activeShiftId
                "Hari Ini" -> {
                    val orderDateStr = try {
                        val inputFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                        inputFmt.timeZone = TimeZone.getTimeZone("UTC")
                        order.displayCreatedAt.let {
                            val parsed = inputFmt.parse(it)
                            parsed?.let { d -> SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(d) }
                        }
                    } catch (_: Exception) {
                        null
                    }
                    orderDateStr == todayStr
                }
                else -> true
            }

            matchesSearch && matchesTab
        }
    }

    val totalSalesAmount = remember(filteredList) {
        filteredList.sumOf { it.totalAmount ?: 0.0 }
    }

    val totalItemsSold = remember(filteredList) {
        filteredList.sumOf { order -> order.displayItems.sumOf { it.quantity ?: 1 } }
    }

    val kasPkkAmount = remember(totalItemsSold) {
        totalItemsSold * 1000.0
    }

    val hakPenitipAmount = remember(totalSalesAmount, kasPkkAmount) {
        if (totalSalesAmount > kasPkkAmount) totalSalesAmount - kasPkkAmount else 0.0
    }

    val groupedOrders = remember(filteredList) {
        filteredList.groupBy { formatDayGroupHeader(it.displayCreatedAt, it.shiftId) }
    }

    Scaffold(
        containerColor = BgLightCanvas,
        topBar = {
            // Header Kasir & Shift (Stitch Riwayat Redesign Style)
            Surface(
                color = BgDarkEspresso,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Profile & sync row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                        text = "POS #01",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BgWarmTan
                                    )
                                    Text(
                                        text = "•",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.5f)
                                    )
                                    Text(
                                        text = "Shift Pagi",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(GreenSuccess)
                                    )
                                    Text(
                                        text = "Kasir Aktif • SMKN 8 Jakarta",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextLight
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = {
                                viewModel.fetchHistory()
                                viewModel.fetchCurrentShift()
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.12f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Sync",
                                tint = TextLight,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.12f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Screen title row with badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                contentDescription = "Receipt",
                                tint = BgWarmTan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Riwayat Transaksi & Rekap",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextLight
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = BgWarmTan
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(GreenSuccess)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Shift Berjalan",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BtnDarkChocolate
                                )
                            }
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
                        indicatorColor = SegmentBg
                    )
                )

                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Riwayat", modifier = Modifier.size(22.dp)) },
                    label = { Text("Riwayat", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BtnDarkChocolate,
                        selectedTextColor = BtnDarkChocolate,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = SegmentBg
                    )
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Profile) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil", modifier = Modifier.size(22.dp)) },
                    label = { Text("Profil", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BtnDarkChocolate,
                        selectedTextColor = BtnDarkChocolate,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = SegmentBg
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
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Bento-Style Financial Recap Card Sesuai Stitch Mockup
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(BgDarkEspresso, BtnDarkChocolate)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        // Top Omzet Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "PENERIMAAN KASIR SHIFT INI",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BgWarmTan,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = totalSalesAmount.toRupiahFormat(),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextLight
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = BtnMocha.copy(alpha = 0.45f)
                            ) {
                                Text(
                                    text = "${filteredList.size} Transaksi",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BtnCreamWhite,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Split Cards: Kas PKK vs Hak Penitip
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Kas PKK
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.Black.copy(alpha = 0.25f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(BgWarmTan)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Kas PKK Sekolah",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = BgWarmTan
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = kasPkkAmount.toRupiahFormat(),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextLight
                                    )
                                    Text(
                                        text = "Rp 1.000 / transaksi",
                                        fontSize = 9.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            // Hak Penitip
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.Black.copy(alpha = 0.25f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(GreenSuccess)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Hak Penitip",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = BgWarmTan
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = hakPenitipAmount.toRupiahFormat(),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextLight
                                    )
                                    Text(
                                        text = "Siap rekap harian",
                                        fontSize = 9.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Tabs (Pill Chips)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filterTabs) { tab ->
                    val isSelected = tab == selectedFilterTab
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) BtnDarkChocolate else CardCreamWhite,
                        border = if (!isSelected) BorderStroke(1.dp, BorderStitch) else null,
                        shadowElevation = if (isSelected) 2.dp else 0.dp,
                        modifier = Modifier.clickable { selectedFilterTab = tab }
                    ) {
                        Text(
                            text = tab,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) BtnCreamWhite else TextDark,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari ID Transaksi...", fontSize = 12.sp, color = TextMuted) },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark,
                    focusedContainerColor = CardCreamWhite,
                    unfocusedContainerColor = CardCreamWhite,
                    focusedBorderColor = BtnDarkChocolate,
                    unfocusedBorderColor = BorderStitch,
                    cursorColor = BtnDarkChocolate
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Daftar Riwayat Transaksi
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BtnDarkChocolate, modifier = Modifier.size(32.dp))
                }
            } else if (filteredList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada riwayat transaksi.", color = TextMuted, fontSize = 13.sp)
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
                                color = SegmentBg,
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
                                        contentDescription = "Calendar",
                                        tint = BtnDarkChocolate,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = headerTitle,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                }
                            }
                        }

                        items(ordersInGroup, key = { it.id ?: "" }) { order ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.selectOrderForDetail(order) },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = CardCreamWhite),
                                border = BorderStroke(1.dp, BorderStitch),
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
                                        // Potong UUID agar tidak wrap — tampilkan 8 char saja
                                        val shortId = order.id?.take(8)?.uppercase() ?: "-"
                                        Text(
                                            text = "#$shortId…",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp,
                                            color = TextMuted,
                                            maxLines = 1
                                        )

                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = GreenSuccessContainer
                                        ) {
                                            Text(
                                                text = (order.status ?: "SUKSES").uppercase(),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GreenSuccess,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = formatOrderDate(order.displayCreatedAt),
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Klik rincian nota",
                                            fontSize = 11.sp,
                                            color = BtnMocha,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        Text(
                                            text = (order.totalAmount ?: 0.0).toRupiahFormat(),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = BtnDarkChocolate
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog Detail Transaksi (Stitch Receipt Style)
    selectedDetail?.let { order ->
        AlertDialog(
            onDismissRequest = { viewModel.clearOrderDetail() },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = "Nota",
                            tint = BtnDarkChocolate,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Rincian Transaksi POS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextDark
                        )
                    }
                    IconButton(onClick = { viewModel.clearOrderDetail() }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Clear, contentDescription = "Close", tint = TextMuted)
                    }
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SegmentBg,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("No. Order:", fontSize = 11.sp, color = TextMuted)
                                Text("#${order.id ?: "-"}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Waktu:", fontSize = 11.sp, color = TextMuted)
                                Text(formatOrderDate(order.displayCreatedAt), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Rincian Menu Titipan:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    val displayItems = order.displayItems
                    if (isDetailLoading) {
                        Box(modifier = Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = BtnDarkChocolate, modifier = Modifier.size(20.dp))
                        }
                    } else if (displayItems.isEmpty()) {
                        Text(
                            text = "Detail produk tidak tersedia",
                            fontSize = 11.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CardCreamWhite),
                            border = BorderStroke(1.dp, BorderStitch)
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
                                                fontSize = 10.sp,
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
                                        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = BorderStitch)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = BorderStitch)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Pembayaran:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text((order.totalAmount ?: 0.0).toRupiahFormat(), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = BtnDarkChocolate)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearOrderDetail() },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BtnDarkChocolate, contentColor = BtnCreamWhite),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = CardCreamWhite,
            shape = RoundedCornerShape(18.dp)
        )
    }
}
