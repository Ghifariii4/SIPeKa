package com.smkn8jkt.sipeka.ui.screens.pos

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.smkn8jkt.sipeka.navigation.Screen
import com.smkn8jkt.sipeka.ui.components.PrimaryButton
import com.smkn8jkt.sipeka.ui.components.SipekaTextField
import com.smkn8jkt.sipeka.ui.theme.BannerMarginBg
import com.smkn8jkt.sipeka.ui.theme.BannerMarginBorder
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

@Composable
fun HomeScreen(
    navController: NavHostController = rememberNavController(),
    posViewModel: PosViewModel = viewModel()
) {
    KasirHomeScreen(
        navController = navController,
        viewModel = posViewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KasirHomeScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: PosViewModel = viewModel()
) {
    val context = LocalContext.current

    val isShiftOpen by viewModel.isShiftOpen.collectAsState()
    val isClockInLoading by viewModel.isClockInLoading.collectAsState()
    val products by viewModel.products.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val cartTotal by viewModel.cartTotal.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val qrScanMessage by viewModel.qrScanMessage.collectAsState()

    var startingCashInput by remember { mutableStateOf("") }
    var selectedNavIndex by remember { mutableIntStateOf(0) }
    var showCheckoutSheet by remember { mutableStateOf(false) }
    var showQrDialog by remember { mutableStateOf(false) }
    var qrCodeInput by remember { mutableStateOf("") }

    val categories = remember { listOf("Semua", "Makanan", "Minuman", "Snack") }

    LaunchedEffect(Unit) {
        viewModel.fetchProducts()
        viewModel.checkCurrentShift()
    }

    LaunchedEffect(qrScanMessage) {
        qrScanMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearQrScanMessage()
        }
    }

    // KONDISI 1: SHIFT BELUM DIBUKA
    if (!isShiftOpen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgLightCanvas)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardCreamWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = BorderStroke(1.dp, BorderStitch)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(SegmentBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PointOfSale,
                            contentDescription = "Shift",
                            tint = BtnDarkChocolate,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Buka Shift Kasir",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Masukkan modal awal laci kasir untuk mulai transaksi POS.",
                        fontSize = 13.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    SipekaTextField(
                        value = startingCashInput,
                        onValueChange = { startingCashInput = it.filter { char -> char.isDigit() } },
                        label = "Modal Awal (Rp)",
                        placeholder = "Contoh: 100000",
                        keyboardType = KeyboardType.Number
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    PrimaryButton(
                        text = "Buka Shift Kasir",
                        isLoading = isClockInLoading,
                        enabled = startingCashInput.isNotBlank(),
                        onClick = {
                            val nominal = startingCashInput.toDoubleOrNull() ?: 0.0
                            viewModel.clockIn(nominal)
                        }
                    )
                }
            }
        }
        return
    }

    // KONDISI 2: SHIFT AKTIF (KATALOG & TRANSAKSI)
    val filteredProducts = remember(products, searchQuery, selectedCategory) {
        products.filter { product ->
            val matchesCategory = selectedCategory == "Semua" ||
                    (product.category ?: "").equals(selectedCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isEmpty() ||
                    (product.name ?: "").contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        containerColor = BgLightCanvas,
        topBar = {
            // Header Konsisten POS & Shift Info Sesuai Stitch Mockup
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
                        // Brand Box "PKK"
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(BtnMocha),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "PKK",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = BtnCreamWhite,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "PKK Mart Kasir",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextLight
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = BgWarmTan
                                ) {
                                    Text(
                                        text = "POS v2",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = BtnDarkChocolate,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
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
                                    text = "Shift Pagi • SMKN 8 Jakarta",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = BgWarmTan
                                )
                            }
                        }
                    }

                    // Status Chip Akun Kasir
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = BtnDarkChocolate,
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "POS #01",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BgWarmTan
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) {
                // Floating Cart Bar (Stitch Resting Over Nav Style)
                AnimatedVisibility(
                    visible = cartItems.isNotEmpty(),
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = CardCreamWhite,
                        shadowElevation = 10.dp,
                        border = BorderStroke(1.dp, BorderStitch)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { showCheckoutSheet = true }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SegmentBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingBag,
                                        contentDescription = "Cart",
                                        tint = BtnDarkChocolate,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    // Badge Counter
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(top = 2.dp, end = 2.dp)
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(BtnDarkChocolate),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$totalCount",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = BtnCreamWhite
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = "$totalCount Item (${cartItems.size} Jenis)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextMuted
                                    )
                                    Text(
                                        text = cartTotal.toRupiahFormat(),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = BtnDarkChocolate
                                    )
                                }
                            }

                            Button(
                                onClick = { showCheckoutSheet = true },
                                enabled = cartTotal > 0 && cartItems.isNotEmpty(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BtnDarkChocolate,
                                    contentColor = BtnCreamWhite
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Bayar Sekarang",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BtnCreamWhite
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Bayar",
                                    tint = BtnCreamWhite,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }

                // Bottom Tab Navigation Bar (Persistent Stitch Style)
                NavigationBar(
                    containerColor = CardCreamWhite,
                    tonalElevation = 8.dp,
                    modifier = Modifier.height(64.dp)
                ) {
                    NavigationBarItem(
                        selected = selectedNavIndex == 0,
                        onClick = { selectedNavIndex = 0 },
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
                        selected = selectedNavIndex == 1,
                        onClick = {
                            selectedNavIndex = 1
                            navController.navigate(Screen.History)
                        },
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
                        selected = selectedNavIndex == 2,
                        onClick = {
                            selectedNavIndex = 2
                            navController.navigate(Screen.Profile)
                        },
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // 1. Mode Transaksi Toggle Segmented Control (Stitch POS Style)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SegmentBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Button Mode POS
                    Surface(
                        shape = RoundedCornerShape(9.dp),
                        color = BtnDarkChocolate,
                        shadowElevation = 1.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(GreenSuccess)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Transaksi POS",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BtnCreamWhite
                            )
                        }
                    }

                    // Button Mode Scan QR PO
                    Surface(
                        shape = RoundedCornerShape(9.dp),
                        color = Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showQrDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Scan QR",
                                tint = TextMuted,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Scan QR Pre-Order",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Search Bar + Quick Barcode Scanner Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = "Cari produk titipan atau barcode...",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Cari",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
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
                    modifier = Modifier.weight(1f)
                )

                // Quick Barcode Action Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CardCreamWhite,
                    border = BorderStroke(1.dp, BorderStitch),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .size(46.dp)
                        .clickable { showQrDialog = true }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scan",
                            tint = BtnDarkChocolate,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Category Pill Chips Filter (Stitch Style)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    val isSelected = category == selectedCategory
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) BtnDarkChocolate else CardCreamWhite,
                        border = if (!isSelected) BorderStroke(1.dp, BorderStitch) else null,
                        shadowElevation = if (isSelected) 2.dp else 0.dp,
                        modifier = Modifier.clickable { viewModel.updateSelectedCategory(category) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = category,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) BtnCreamWhite else TextDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4. Strip Margin Kas PKK (Stitch Specification)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = BannerMarginBg,
                border = BorderStroke(1.dp, BannerMarginBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(CardCreamWhite),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Margin",
                                tint = BtnDarkChocolate,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        Text(
                            text = "Margin Kas PKK: Otomatis Rp 1.000/item ke sekolah",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextDark
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CardCreamWhite.copy(alpha = 0.8f)
                    ) {
                        Text(
                            text = "100% Amanah",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = BtnDarkChocolate,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 5. Catalog Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Etalase Titipan Siswa",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "Stok sinkron live otomatis",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SegmentBg
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(GreenSuccess)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "${filteredProducts.size} Siap Saji",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BtnDarkChocolate
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 6. Grid Produk 2-Kolom Sesuai Stitch Mockup
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Memuat data menu titipan...", color = TextMuted, fontSize = 13.sp)
                }
            } else if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Menu tidak ditemukan", color = TextMuted, fontSize = 13.sp)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 90.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredProducts, key = { it.id }) { product ->
                        val inCartQty = cartItems.find { it.product.id == product.id }?.quantity ?: 0
                        SipekaProductCard(
                            product = product,
                            cartQuantity = inCartQty,
                            onAddToCart = { viewModel.addToCart(it) },
                            onRemoveFromCart = { viewModel.decreaseQuantity(it.id) }
                        )
                    }
                }
            }
        }
    }

    // Modal Verifikasi QR Pre-order (Stitch Style)
    if (showQrDialog) {
        var isVerifiedStep by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = {
                showQrDialog = false
                isVerifiedStep = false
            },
            title = {
                Text(
                    text = if (!isVerifiedStep) "Scan QR Pre-Order" else "Verifikasi Fisik Titipan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = TextDark
                )
            },
            text = {
                Column {
                    if (!isVerifiedStep) {
                        Text(
                            text = "Masukkan atau scan kode QR voucher pre-order siswa:",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        SipekaTextField(
                            value = qrCodeInput,
                            onValueChange = { qrCodeInput = it },
                            label = "Kode QR",
                            placeholder = "Contoh: QR-PREORDER-1043"
                        )
                    } else {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SegmentBg,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Kode: ${qrCodeInput.ifBlank { "QR-1043" }}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = GreenSuccessContainer
                                ) {
                                    Text(
                                        text = "SIAP DIAMBIL",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GreenSuccess,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CardCreamWhite),
                            border = BorderStroke(1.dp, BorderStitch)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("2x Risoles Mayo Keju", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                                    Text("Rp 14.000", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BtnDarkChocolate)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Pemesan: Vina (NIS: 21904) • Status: Lunas QRIS", fontSize = 11.sp, color = TextMuted)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (!isVerifiedStep) {
                            if (qrCodeInput.isNotBlank()) isVerifiedStep = true
                        } else {
                            viewModel.processQrScan(qrCodeInput.ifBlank { "QR-PREORDER-1043" })
                            showQrDialog = false
                            isVerifiedStep = false
                            qrCodeInput = ""
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BtnDarkChocolate, contentColor = BtnCreamWhite)
                ) {
                    Text(
                        text = if (!isVerifiedStep) "Verifikasi Pesanan" else "Serahkan Barang ke Siswa",
                        fontWeight = FontWeight.Bold,
                        color = BtnCreamWhite
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showQrDialog = false; isVerifiedStep = false }) {
                    Text("Batal", color = TextMuted)
                }
            },
            containerColor = CardCreamWhite,
            shape = RoundedCornerShape(18.dp)
        )
    }

    if (showCheckoutSheet) {
        CheckoutBottomSheet(
            viewModel = viewModel,
            onDismiss = { showCheckoutSheet = false }
        )
    }
}
