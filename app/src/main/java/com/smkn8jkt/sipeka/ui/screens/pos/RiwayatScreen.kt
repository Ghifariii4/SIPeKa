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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.smkn8jkt.sipeka.data.model.OrderData
import com.smkn8jkt.sipeka.navigation.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val DarkBrown = Color(0xFF3E2723)
private val PrimaryOrange = Color(0xFFFF7043)
private val SurfaceBeige = Color(0xFFFAF7F4)
private val CardWhite = Color(0xFFFFFFFF)

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
    if (isoString.isNullOrBlank()) return if (!shiftId.isNullOrBlank()) "Shift #$shiftId" else "Shift Aktif"
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

        val shiftLabel = if (!shiftId.isNullOrBlank()) " • Shift #$shiftId" else ""
        "$dayLabel$shiftLabel"
    } catch (_: Exception) {
        if (!shiftId.isNullOrBlank()) "Shift #$shiftId" else "Shift Aktif"
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
                            inputFmt.parse(it)?.let { d ->
                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(d)
                            }
                        }
                    } catch (_: Exception) { null }
                    orderDateStr == todayStr || order.createdAt == null
                }
                else -> true
            }

            matchesSearch && matchesTab
        }
    }

    // Grouping per Shift dan Hari
    val groupedOrders = remember(filteredList) {
        filteredList.groupBy { formatDayGroupHeader(it.displayCreatedAt, it.shiftId) }
    }

    val totalOmzet = remember(filteredList) {
        filteredList.sumOf { it.totalAmount ?: 0.0 }
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
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Riwayat") },
                    label = { Text("Riwayat", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryOrange,
                        selectedTextColor = PrimaryOrange,
                        indicatorColor = PrimaryOrange.copy(alpha = 0.12f)
                    )
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Profile) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                    label = { Text("Profil", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
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

            Text(
                text = "Riwayat Transaksi",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBrown
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Summary Stat Card
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
                            text = "Total Penjualan",
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
                            text = "${filteredList.size} Transaksi",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filterTabs) { tab ->
                    val isSelected = tab == selectedFilterTab
                    Button(
                        onClick = { selectedFilterTab = tab },
                        shape = CircleShape,
                        border = if (!isSelected) BorderStroke(1.dp, Color(0xFFE0E0E0)) else null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) DarkBrown else CardWhite,
                            contentColor = if (isSelected) Color.White else DarkBrown
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(text = tab, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

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
                    Text("Memuat riwayat...", color = Color.Gray, fontSize = 14.sp)
                }
            } else if (filteredList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(PrimaryOrange.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                contentDescription = "Empty",
                                tint = PrimaryOrange,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Belum Ada Transaksi", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkBrown)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp),
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
                            TransactionHistoryCard(
                                order = order,
                                onClick = { viewModel.selectOrderForDetail(order) }
                            )
                        }
                    }
                }
            }
        }
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

@Composable
fun TransactionHistoryCard(
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
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                        contentDescription = "Order ID",
                        tint = PrimaryOrange,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "#${order.id ?: "-"}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DarkBrown
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFE8F5E9)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = (order.status ?: "COMPLETED").uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = formatOrderDate(order.displayCreatedAt),
                fontSize = 11.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Klik rincian",
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
