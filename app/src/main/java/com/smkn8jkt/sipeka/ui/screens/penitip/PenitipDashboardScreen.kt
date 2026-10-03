package com.smkn8jkt.sipeka.ui.screens.penitip

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.smkn8jkt.sipeka.data.model.ProductResponse
import com.smkn8jkt.sipeka.data.remote.TokenManager
import com.smkn8jkt.sipeka.navigation.Screen
import com.smkn8jkt.sipeka.ui.components.PrimaryButton
import com.smkn8jkt.sipeka.ui.screens.auth.AuthViewModel
import com.smkn8jkt.sipeka.ui.screens.pos.ProductImage
import com.smkn8jkt.sipeka.ui.screens.pos.toRupiahFormat
import com.smkn8jkt.sipeka.ui.theme.AmberWarning
import com.smkn8jkt.sipeka.ui.theme.AmberWarningContainer
import com.smkn8jkt.sipeka.ui.theme.BgDarkEspresso
import com.smkn8jkt.sipeka.ui.theme.BgWarmTan
import com.smkn8jkt.sipeka.ui.theme.BorderStitch
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
import com.smkn8jkt.sipeka.ui.theme.VibrantOrange
import kotlinx.coroutines.launch

// Color constants matching the Stitch / Warm Mocha Design System
private val PageBgWarm = Color(0xFFFBF8F4)
private val DarkMochaHero = Color(0xFF451A0D)
private val HeroIconBg = Color(0xFF5A2818)
private val SoftPeachBadge = Color(0xFFFEE6D8)
private val ChipUnselectedBg = Color(0xFFFDF2E9)
private val ChipUnselectedText = Color(0xFF4D2314)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PenitipDashboardScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: PenitipViewModel = viewModel(),
    authViewModel: AuthViewModel? = null,
    tokenManager: TokenManager? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Produk Titipan, 1: Bagi Hasil, 2: Toko & Profil
    var showAddSheet by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val dashboardData by viewModel.dashboardData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isAddLoading by viewModel.isAddLoading.collectAsState()
    val isAddSuccess by viewModel.isAddSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    val penitipName by viewModel.penitipName.collectAsState()
    val penitipNip by viewModel.penitipNip.collectAsState()
    val penitipShop by viewModel.penitipShop.collectAsState()
    val isUpdatingProfile by viewModel.isUpdatingPenitipProfile.collectAsState()
    val updateProfileSuccess by viewModel.updateProfileSuccess.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchDashboard()
    }

    LaunchedEffect(isAddSuccess) {
        if (isAddSuccess) {
            showAddSheet = false
            viewModel.resetAddSuccess()
        }
    }

    LaunchedEffect(updateProfileSuccess) {
        updateProfileSuccess?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearUpdateSuccess()
        }
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

    val products = dashboardData?.products ?: emptyList()
    val unpaidEarnings = dashboardData?.effectiveEarnings ?: 0.0

    Scaffold(
        containerColor = PageBgWarm,
        contentWindowInsets = WindowInsets.statusBars.union(WindowInsets.displayCutout),
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showAddSheet = true },
                    containerColor = DarkMochaHero,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Titip Produk",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Titip Menu Baru",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                // Tab 0: Produk Titipan
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Produk Titipan",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Produk Titipan",
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkMochaHero,
                        selectedTextColor = DarkMochaHero,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = SoftPeachBadge
                    )
                )

                // Tab 1: Bagi Hasil
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        viewModel.fetchDashboard()
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "Bagi Hasil",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Bagi Hasil",
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkMochaHero,
                        selectedTextColor = DarkMochaHero,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = SoftPeachBadge
                    )
                )

                // Tab 2: Toko & Profil
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Toko & Profil",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Toko & Profil",
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkMochaHero,
                        selectedTextColor = DarkMochaHero,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = SoftPeachBadge
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
                0 -> PenitipProductsTab(
                    penitipName = penitipName,
                    penitipShop = penitipShop,
                    products = products,
                    isLoading = isLoading,
                    onRefresh = { viewModel.fetchDashboard() },
                    onDeleteProduct = { viewModel.deleteProduct(it) },
                    onOpenAddSheet = { showAddSheet = true }
                )

                1 -> PenitipBagiHasilTab(
                    unpaidEarnings = unpaidEarnings,
                    products = products,
                    isLoading = isLoading,
                    onRefresh = { viewModel.fetchDashboard() }
                )

                2 -> PenitipProfileTab(
                    penitipName = penitipName,
                    penitipNip = penitipNip,
                    penitipShop = penitipShop,
                    isUpdating = isUpdatingProfile,
                    onSaveProfile = { name, nip, shop, newPw ->
                        viewModel.updatePenitipProfile(name, nip, shop, newPw)
                    },
                    onLogoutClick = { showLogoutDialog = true }
                )
            }
        }
    }

    // Modal Bottom Sheet: Tambah Produk Titipan
    if (showAddSheet) {
        AddProductBottomSheet(
            isLoading = isAddLoading,
            onDismiss = { showAddSheet = false },
            onConfirmUpload = { imageUri, name, price, desc, stock, category ->
                viewModel.uploadProduct(context, imageUri, name, price, desc, stock, category)
            }
        )
    }

    // Modal Dialog: Konfirmasi Logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Konfirmasi Keluar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = TextDark
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin keluar dari akun Mitra Penitip SIPeKa?",
                    fontSize = 13.sp,
                    color = TextMuted,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        scope.launch {
                            tokenManager?.clearToken()
                            authViewModel?.resetState()
                            navController.navigate(Screen.Login) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedError),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Ya, Keluar", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal", color = TextMuted)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

// ==========================================
// TAB 0: PRODUK TITIPAN (KATALOG & KONTROL STOK)
// ==========================================
@Composable
fun PenitipProductsTab(
    penitipName: String,
    penitipShop: String,
    products: List<ProductResponse>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onDeleteProduct: (String) -> Unit,
    onOpenAddSheet: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }
    val categories = remember { listOf("Semua", "Makanan", "Minuman", "Snack", "Paket") }

    val filteredProducts = remember(products, searchQuery, selectedCategory) {
        products.filter { product ->
            val effectiveCat = product.effectiveCategory
            val matchesCategory = selectedCategory.equals("Semua", ignoreCase = true) ||
                    effectiveCat.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    (product.name ?: "").contains(searchQuery, ignoreCase = true) ||
                    (product.description ?: "").contains(searchQuery, ignoreCase = true) ||
                    effectiveCat.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    val totalSoldAll = products.sumOf { it.totalSold }
    val lowStockCount = products.count { it.stock <= 5 }

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. HEADER MITRA PENITIP HERO CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(DarkMochaHero, Color(0xFF2B1810))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(HeroIconBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = "Toko",
                                    tint = SoftPeachBadge,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = penitipShop.ifBlank { "Kantin PKK Sejahtera" },
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = SoftPeachBadge
                                    ) {
                                        Text(
                                            text = "MITRA",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = DarkMochaHero,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Pengelola: $penitipName",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        IconButton(
                            onClick = onRefresh,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Data",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. STATISTIK RINGKASAN PRODUK & STOK (3 KPI Cards)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: Total Menu
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.7f)),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = "Total Menu", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${products.size}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "item titipan", fontSize = 10.sp, color = TextMuted)
                    }
                }

                // Card 2: Terjual
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.7f)),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = "Terjual", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$totalSoldAll",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GreenSuccess
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "porsi/pcs", fontSize = 10.sp, color = GreenSuccess)
                    }
                }

                // Card 3: Stok Kritis / Habis
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.7f)),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = "Perlu Restok", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$lowStockCount",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (lowStockCount > 0) AmberWarning else TextDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (lowStockCount > 0) "stok <= 5" else "stok aman",
                            fontSize = 10.sp,
                            color = if (lowStockCount > 0) AmberWarning else TextMuted
                        )
                    }
                }
            }
        }

        // 3. PENCARIAN & KATEGORI CHIPS
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari produk titipan Anda...", fontSize = 13.sp, color = TextMuted) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Cari",
                            tint = DarkMochaHero,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Hapus",
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = DarkMochaHero,
                        unfocusedBorderColor = OutlineWarm.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )

                // Category Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                        val count = if (cat == "Semua") products.size else products.count { it.effectiveCategory.equals(cat, ignoreCase = true) }

                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = if (isSelected) DarkMochaHero else ChipUnselectedBg,
                            border = if (!isSelected) BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.5f)) else null,
                            modifier = Modifier.clickable { selectedCategory = cat }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else ChipUnselectedText
                                )
                                Surface(
                                    shape = CircleShape,
                                    color = if (isSelected) Color.White.copy(alpha = 0.25f) else Color.White
                                ) {
                                    Text(
                                        text = "$count",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else DarkMochaHero,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. DAFTAR PRODUK TITIPAN
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = DarkMochaHero, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Memuat data produk titipan...", color = TextMuted, fontSize = 13.sp)
                    }
                }
            }
        } else if (products.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(SoftPeachBadge),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = "Empty",
                                tint = DarkMochaHero,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Belum Ada Menu Titipan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Titipkan makanan, minuman, atau snack Anda ke Kantin PKK SMKN 8 Jakarta untuk mulai menghasilkan bagi hasil.",
                            fontSize = 12.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center,
                            lineHeight = 17.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onOpenAddSheet,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkMochaHero)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Titip Menu Pertama", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        } else if (filteredProducts.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Tidak Ada Menu yang Cocok",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Coba ubah kata kunci pencarian atau pilih filter kategori lainnya.",
                            fontSize = 12.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(filteredProducts, key = { it.id }) { product ->
                PenitipProductCard(
                    product = product,
                    onDeleteProduct = onDeleteProduct
                )
            }
        }
    }
}

// ==========================================
// CARD PRODUK TITIPAN BERKUALITAS TINGGI
// ==========================================
@Composable
fun PenitipProductCard(
    product: ProductResponse,
    onDeleteProduct: (String) -> Unit
) {
    val isOutOfStock = product.stock <= 0
    val isLowStock = product.stock in 1..5
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val schoolMargin = product.schoolMargin ?: 1000.0
    val netEarningsPerUnit = (product.price - schoolMargin).coerceAtLeast(0.0)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.6f)),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Gambar Produk
                ProductImage(
                    imageUrl = product.imageUrl,
                    category = product.effectiveCategory,
                    contentDescription = product.name,
                    modifier = Modifier
                        .size(76.dp)
                        .clip(RoundedCornerShape(14.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Header: Kategori & Tombol Hapus
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ChipUnselectedBg
                        ) {
                            Text(
                                text = product.effectiveCategory,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkMochaHero,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }

                        IconButton(
                            onClick = { showDeleteConfirmDialog = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Hapus Produk",
                                tint = RedError,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = product.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (!product.description.isNullOrBlank()) {
                        Text(
                            text = product.description,
                            fontSize = 11.sp,
                            color = TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = product.price.toRupiahFormat(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DarkMochaHero
                        )
                        Text(
                            text = "•",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "Bagi Hasil: ${netEarningsPerUnit.toRupiahFormat()}/pcs",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GreenSuccess
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = OutlineWarm.copy(alpha = 0.4f), thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Footer Card: Badge Status Stok & Total Terjual
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Stok
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when {
                        isOutOfStock -> RedErrorContainer
                        isLowStock -> AmberWarningContainer
                        else -> GreenSuccessContainer
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isOutOfStock -> RedError
                                        isLowStock -> AmberWarning
                                        else -> GreenSuccess
                                    }
                                )
                        )
                        Text(
                            text = when {
                                isOutOfStock -> "Stok Habis"
                                isLowStock -> "Sisa: ${product.stock} pcs (Segera Restok)"
                                else -> "Sisa Stok: ${product.stock} pcs"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isOutOfStock -> RedError
                                isLowStock -> AmberWarning
                                else -> GreenSuccess
                            }
                        )
                    }
                }

                // Total Terjual
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Terjual: ${product.totalSold} pcs",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }
            }
        }
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Text(
                    text = "Hapus Menu Titipan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextDark
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menghapus '${product.name}' dari katalog titipan kantin? Tindakan ini tidak dapat dibatalkan.",
                    fontSize = 13.sp,
                    color = TextMuted,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteProduct(product.id)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RedError)
                ) {
                    Text("Ya, Hapus", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Batal", color = TextMuted)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

// ==========================================
// TAB 1: BAGI HASIL (KEUANGAN & KAS PKK)
// ==========================================
@Composable
fun PenitipBagiHasilTab(
    unpaidEarnings: Double,
    products: List<ProductResponse>,
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    val totalSoldAll = products.sumOf { it.totalSold }
    val totalGrossRevenue = products.sumOf { it.price * it.totalSold }
    val totalSchoolCommission = products.sumOf { (it.schoolMargin ?: 1000.0) * it.totalSold }

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. HERO CARD: TOTAL PENDAPATAN BERSIH BELUM DICAIRKAN
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(DarkMochaHero, Color(0xFF2B1810))
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
                                        .background(HeroIconBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = "Saldo",
                                        tint = SoftPeachBadge,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Saldo Bersih Mitra",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SoftPeachBadge
                                    )
                                    Text(
                                        text = "Belum Dicairkan",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.75f)
                                    )
                                }
                            }

                            IconButton(
                                onClick = onRefresh,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = unpaidEarnings.toRupiahFormat(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "Formula: (Harga Jual - Rp 1.000) × Terjual",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = SoftPeachBadge,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. METRIK KEUANGAN (2 COLUMNS)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Omset Kotor
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.7f)),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "Total Omset Penjualan", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = totalGrossRevenue.toRupiahFormat(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "$totalSoldAll porsi terjual", fontSize = 10.sp, color = TextMuted)
                    }
                }

                // Kontribusi Kas PKK
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.7f)),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "Kas Operasional PKK", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = totalSchoolCommission.toRupiahFormat(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkMochaHero
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Rp 1.000 per porsi", fontSize = 10.sp, color = DarkMochaHero)
                    }
                }
            }
        }

        // 3. KARTU TRANSPARANSI BAGI HASIL SEKOLAH
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = ChipUnselectedBg,
                border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = DarkMochaHero,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Transparansi Bagi Hasil Kantin PKK",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkMochaHero
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Setiap produk yang terjual melalui kasir akan dipotong margin tetap Rp 1.000 untuk kas operasional koperasi & kebersihan kantin sekolah SMKN 8 Jakarta. Sisa 100% adalah hak bersih mitra penitip.",
                        fontSize = 11.sp,
                        color = TextDark,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // 4. HEADER DAFTAR PER PRODUK
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rincian Bagi Hasil per Menu",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = "${products.size} Menu",
                    fontSize = 12.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 5. LIST RINCIAN PER PRODUK
        if (products.isEmpty()) {
            item {
                Text(
                    text = "Belum ada produk titipan yang terdaftar.",
                    fontSize = 12.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            items(products, key = { "earning_${it.id}" }) { product ->
                val margin = product.schoolMargin ?: 1000.0
                val netUnit = (product.price - margin).coerceAtLeast(0.0)
                val netTotal = netUnit * product.totalSold

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = product.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = netTotal.toRupiahFormat(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GreenSuccess
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Harga: ${product.price.toRupiahFormat()} × ${product.totalSold} terjual",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "Bersih: ${netUnit.toRupiahFormat()}/pcs",
                                fontSize = 11.sp,
                                color = DarkMochaHero,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // 6. INFO PENCAIRAN SALDO
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GreenSuccessContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = GreenSuccess,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Cara Pencairan Dana",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Pencairan saldo dapat diambil langsung melalui Guru Pembina PKK atau Petugas Kasir pada akhir shift atau jadwal mingguan sekolah.",
                            fontSize = 11.sp,
                            color = TextMuted,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 2: TOKO & PROFIL MITRA (LENGKAP DI DALAM LAYAR)
// ==========================================
@Composable
fun PenitipProfileTab(
    penitipName: String,
    penitipNip: String,
    penitipShop: String,
    isUpdating: Boolean,
    onSaveProfile: (name: String, nip: String, shop: String, newPassword: String?) -> Unit,
    onLogoutClick: () -> Unit
) {
    var nameInput by remember(penitipName) { mutableStateOf(penitipName) }
    var shopInput by remember(penitipShop) { mutableStateOf(penitipShop) }
    var nipInput by remember(penitipNip) { mutableStateOf(penitipNip) }
    var passwordInput by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .padding(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. BANNER BRAND TOKO TITIPAN
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(DarkMochaHero, Color(0xFF2B1810))
                        )
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(HeroIconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Avatar Toko",
                            tint = SoftPeachBadge,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = penitipShop.ifBlank { "Kantin PKK Sejahtera" },
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Penanggung Jawab: $penitipName",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SoftPeachBadge
                    ) {
                        Text(
                            text = "MITRA RESMI KANTIN PKK SMKN 8 JAKARTA",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DarkMochaHero,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // 2. FORM EDIT INFORMASI AKUN & TOKO
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Informasi Akun & Usaha Titipan",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = "Perbarui nama pemilik, merek dagang, dan nomor kontak Anda.",
                    fontSize = 12.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Field: Nama Pemilik
                Text(text = "Nama Pemilik / Pengelola", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it; formError = null },
                    singleLine = true,
                    placeholder = { Text("Contoh: Ibu Sari", fontSize = 13.sp) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkMochaHero,
                        unfocusedBorderColor = OutlineWarm.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Field: Nama Toko / Usaha
                Text(text = "Nama Toko / Merek Titipan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = shopInput,
                    onValueChange = { shopInput = it; formError = null },
                    singleLine = true,
                    placeholder = { Text("Contoh: Dapur Mama Ayu", fontSize = 13.sp) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkMochaHero,
                        unfocusedBorderColor = OutlineWarm.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Field: NIP / Kontak WhatsApp
                Text(text = "NIP / Nomor Kontak", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = nipInput,
                    onValueChange = { nipInput = it; formError = null },
                    singleLine = true,
                    placeholder = { Text("Contoh: 08123456789 atau PNT-8821", fontSize = 13.sp) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkMochaHero,
                        unfocusedBorderColor = OutlineWarm.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Field: Password Baru
                Text(text = "Password Baru (Opsional)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it; formError = null },
                    singleLine = true,
                    placeholder = { Text("Kosongkan jika tidak ingin diubah", fontSize = 13.sp) },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Password",
                                tint = TextMuted
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkMochaHero,
                        unfocusedBorderColor = OutlineWarm.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                formError?.let { err ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = err, color = RedError, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(18.dp))

                PrimaryButton(
                    text = "Simpan Perubahan Akun",
                    isLoading = isUpdating,
                    onClick = {
                        if (nameInput.isBlank()) {
                            formError = "Nama pemilik tidak boleh kosong."
                            return@PrimaryButton
                        }
                        if (passwordInput.isNotBlank() && passwordInput.length < 6) {
                            formError = "Password minimal 6 karakter."
                            return@PrimaryButton
                        }
                        onSaveProfile(nameInput, nipInput, shopInput, passwordInput.ifBlank { null })
                    }
                )
            }
        }

        // 3. PANDUAN & KETENTUAN TITIP PRODUK
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Standar Kemitraan Kantin PKK",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "1. Makanan basah wajib disetor sebelum pukul 08.30 WIB.\n2. Wajib menggunakan kemasan bersih, higienis, dan bertutup rapat.\n3. Cantumkan nama makanan dan nomor porsi saat menyerahkan fisik ke kantin.",
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 17.sp
                )
            }
        }

        // 4. TOMBOL LOGOUT
        OutlinedButton(
            onClick = onLogoutClick,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, RedError.copy(alpha = 0.6f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = RedError),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Keluar Akun",
                tint = RedError,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Keluar dari Akun Penitip",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = RedError
            )
        }
    }
}

// ==========================================
// MODAL BOTTOM SHEET: TAMBAH PRODUK TITIPAN (ERGONOMIS & CANTIK)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductBottomSheet(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirmUpload: (imageUri: Uri?, name: String, price: String, desc: String, stock: String, category: String) -> Unit
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var nameInput by remember { mutableStateOf("") }
    var categoryInput by remember { mutableStateOf("Makanan") }
    var priceInput by remember { mutableStateOf("") }
    var descInput by remember { mutableStateOf("") }
    var stockInput by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf<String?>(null) }

    val categoryOptions = remember { listOf("Makanan", "Minuman", "Snack", "Paket") }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
    }

    val priceNumeric = priceInput.toDoubleOrNull() ?: 0.0
    val netEarningsPreview = (priceNumeric - 1000.0).coerceAtLeast(0.0)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Sheet
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Titip Menu Baru ke Kantin",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "Isi detail makanan dan stok awal titipan",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
                }
            }

            HorizontalDivider(color = OutlineWarm.copy(alpha = 0.5f))

            // 1. Image Picker & Preview
            if (selectedImageUri != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Preview Foto Makanan",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.6f),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .clickable {
                                photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }
                    ) {
                        Text(
                            text = "Ganti Foto",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = ChipUnselectedBg,
                    border = BorderStroke(1.dp, OutlineWarm),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "Tambah Foto",
                            tint = DarkMochaHero,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pilih Foto Menu (Opsional)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkMochaHero
                        )
                    }
                }
            }

            // 2. Nama Produk
            Column {
                Text(text = "Nama Menu Titipan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it; formError = null },
                    placeholder = { Text("Contoh: Nasi Goreng Telur Spesial", fontSize = 13.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkMochaHero,
                        unfocusedBorderColor = OutlineWarm.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 3. Kategori Produk (Preset Chips)
            Column {
                Text(text = "Kategori Menu", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categoryOptions.forEach { cat ->
                        val isSelected = categoryInput.equals(cat, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) DarkMochaHero else ChipUnselectedBg,
                            border = BorderStroke(1.dp, if (isSelected) DarkMochaHero else OutlineWarm.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { categoryInput = cat }
                        ) {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else ChipUnselectedText,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // 4. Harga Jual & Kalkulator Bagi Hasil Dinamis
            Column {
                Text(text = "Harga Jual ke Pembeli (Rp)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = priceInput,
                    onValueChange = { priceInput = it.filter { char -> char.isDigit() }; formError = null },
                    placeholder = { Text("Contoh: 15000", fontSize = 13.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkMochaHero,
                        unfocusedBorderColor = OutlineWarm.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Live Margin Calculator Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (priceNumeric > 1000.0) GreenSuccessContainer.copy(alpha = 0.5f) else AmberWarningContainer.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, if (priceNumeric > 1000.0) GreenSuccess.copy(alpha = 0.3f) else AmberWarning.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (priceNumeric > 1000.0) {
                                "💡 Harga ${priceNumeric.toRupiahFormat()} = Kas PKK Rp 1.000 + Bagi Hasil Anda ${netEarningsPreview.toRupiahFormat()} / pcs"
                            } else {
                                "⚠️ Harga jual wajib lebih besar dari Rp 1.000 (margin operasional kas PKK)."
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (priceNumeric > 1000.0) GreenSuccess else AmberWarning
                        )
                    }
                }
            }

            // 5. Stok Awal Titipan
            Column {
                Text(text = "Jumlah Porsi / Stok Fisik", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = stockInput,
                    onValueChange = { stockInput = it.filter { char -> char.isDigit() }; formError = null },
                    placeholder = { Text("Contoh: 20", fontSize = 13.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkMochaHero,
                        unfocusedBorderColor = OutlineWarm.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 6. Deskripsi Menu
            Column {
                Text(text = "Deskripsi Singkat (Opsional)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = descInput,
                    onValueChange = { descInput = it },
                    placeholder = { Text("Contoh: Pedas manis dengan irisan sosis", fontSize = 13.sp) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkMochaHero,
                        unfocusedBorderColor = OutlineWarm.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            formError?.let { err ->
                Text(text = err, color = RedError, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(6.dp))

            PrimaryButton(
                text = "Simpan & Titipkan Produk",
                isLoading = isLoading,
                onClick = {
                    if (nameInput.isBlank()) {
                        formError = "Nama menu tidak boleh kosong."
                        return@PrimaryButton
                    }
                    if (priceNumeric <= 1000.0) {
                        formError = "Harga jual harus lebih besar dari Rp 1.000."
                        return@PrimaryButton
                    }
                    val stock = stockInput.toIntOrNull() ?: 0
                    if (stock <= 0) {
                        formError = "Stok awal titipan minimal 1 pcs."
                        return@PrimaryButton
                    }
                    onConfirmUpload(
                        selectedImageUri,
                        nameInput,
                        priceInput,
                        descInput,
                        stockInput,
                        categoryInput
                    )
                }
            )
        }
    }
}
