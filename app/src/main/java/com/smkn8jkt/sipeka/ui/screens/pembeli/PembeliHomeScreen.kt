package com.smkn8jkt.sipeka.ui.screens.pembeli

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
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
import com.smkn8jkt.sipeka.data.model.CartItem
import com.smkn8jkt.sipeka.data.model.OrderData
import com.smkn8jkt.sipeka.data.model.ProductResponse
import com.smkn8jkt.sipeka.data.remote.TokenManager
import com.smkn8jkt.sipeka.navigation.Screen
import com.smkn8jkt.sipeka.ui.screens.auth.AuthViewModel
import com.smkn8jkt.sipeka.ui.screens.pos.ProductImage
import com.smkn8jkt.sipeka.ui.screens.pos.formatOrderDate
import com.smkn8jkt.sipeka.ui.screens.pos.toRupiahFormat
import com.smkn8jkt.sipeka.ui.theme.AmberWarning
import com.smkn8jkt.sipeka.ui.theme.AmberWarningContainer
import com.smkn8jkt.sipeka.ui.theme.BgLightCanvas
import com.smkn8jkt.sipeka.ui.theme.BgWarmTan
import com.smkn8jkt.sipeka.ui.theme.BorderStitch
import com.smkn8jkt.sipeka.ui.theme.BtnCreamWhite
import com.smkn8jkt.sipeka.ui.theme.BtnDarkChocolate
import com.smkn8jkt.sipeka.ui.theme.BtnMocha
import com.smkn8jkt.sipeka.ui.theme.CardCreamWhite
import com.smkn8jkt.sipeka.ui.theme.GreenSuccess
import com.smkn8jkt.sipeka.ui.theme.GreenSuccessContainer
import com.smkn8jkt.sipeka.ui.theme.OutlineWarm
import com.smkn8jkt.sipeka.ui.theme.PlaceholderWarm
import com.smkn8jkt.sipeka.ui.theme.RedError
import com.smkn8jkt.sipeka.ui.theme.TextDark
import com.smkn8jkt.sipeka.ui.theme.TextLight
import com.smkn8jkt.sipeka.ui.theme.TextMuted
import com.smkn8jkt.sipeka.util.QrCodeUtil
import kotlinx.coroutines.launch

// Color constants matching the user's design mockup
val VibrantOrange = Color(0xFFF95721)
val DarkMochaHero = Color(0xFF451A0D)
val HeroIconBg = Color(0xFF5A2818)
val SoftPeachBadge = Color(0xFFFEE6D8)
val LiveStockDot = Color(0xFFEF4444)
val ChipUnselectedBg = Color(0xFFFDF2E9)
val ChipUnselectedText = Color(0xFF4D2314)
val HeaderAmberGold = Color(0xFFC25E00)
val PageBgWarm = Color(0xFFFBF8F4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PembeliHomeScreen(
    navController: NavHostController,
    tokenManager: TokenManager? = null,
    viewModel: PembeliViewModel = viewModel(factory = PembeliViewModelFactory(tokenManager)),
    authViewModel: AuthViewModel? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Beranda (Mockup UI), 1: Pesanan Saya, 2: Profil
    var showCartSheet by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val products by viewModel.filteredProducts.collectAsState()
    val allProducts by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val userName by viewModel.userName.collectAsState()
    val userClass by viewModel.userClass.collectAsState()
    val userNisn by viewModel.userNisn.collectAsState()

    val cartItems by viewModel.cartItems.collectAsState()
    val totalCartPrice by viewModel.totalCartPrice.collectAsState()
    val totalCartCount by viewModel.totalCartCount.collectAsState()

    val activeTicketOrder by viewModel.activeTicketOrder.collectAsState()
    val myOrders by viewModel.myOrders.collectAsState()
    val isOrdersLoading by viewModel.isOrdersLoading.collectAsState()
    val isCheckoutLoading by viewModel.isCheckoutLoading.collectAsState()

    val isUpdatingProfile by viewModel.isUpdatingProfile.collectAsState()
    val updateProfileSuccess by viewModel.updateProfileSuccess.collectAsState()

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearErrorMessage()
        }
    }

    LaunchedEffect(updateProfileSuccess) {
        updateProfileSuccess?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearUpdateSuccess()
        }
    }

    Scaffold(
        containerColor = PageBgWarm,
        contentWindowInsets = WindowInsets.statusBars.union(WindowInsets.displayCutout),
        floatingActionButton = {
            // Floating Circular Orange Cart Button with Badge matching mockup
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showCartSheet = true },
                    containerColor = VibrantOrange,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Keranjang Belanja",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                        if (totalCartCount > 0) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(16.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "$totalCartCount",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = VibrantOrange
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                // Tab 1: Beranda
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Beranda",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Beranda",
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

                // Tab 2: Pesanan (Dengan Badge Notifikasi Pesanan Aktif)
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        viewModel.fetchMyOrders()
                    },
                    icon = {
                        Box {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                contentDescription = "Pesanan",
                                modifier = Modifier.size(24.dp)
                            )
                            val pendingCount = myOrders.count {
                                it.status.isNullOrBlank() ||
                                        it.status.equals("pending", ignoreCase = true) ||
                                        it.status.equals("proses", ignoreCase = true)
                            }
                            if (pendingCount > 0) {
                                Surface(
                                    shape = CircleShape,
                                    color = VibrantOrange,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(8.dp)
                                ) {}
                            }
                        }
                    },
                    label = {
                        Text(
                            text = "Pesanan",
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

                // Tab 3: Profil
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profil",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Profil",
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
                0 -> PembeliMockupHomeTab(
                    userName = userName,
                    userClass = userClass,
                    products = products,
                    allProductsCount = allProducts.size,
                    isLoading = isLoading,
                    selectedCategory = selectedCategory,
                    searchQuery = searchQuery,
                    cartItems = cartItems,
                    onSelectCategory = { viewModel.selectCategory(it) },
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    onAddToCart = { viewModel.addToCart(it) },
                    onIncreaseQuantity = { viewModel.increaseQuantity(it) },
                    onDecreaseQuantity = { viewModel.decreaseQuantity(it) },
                    onRefresh = { viewModel.fetchProducts() }
                )
                1 -> PembeliOrdersTab(
                    userName = userName,
                    orders = myOrders,
                    isLoading = isOrdersLoading,
                    onRefresh = { viewModel.fetchMyOrders() },
                    onOpenTicket = { viewModel.viewTicket(it) }
                )
                2 -> PembeliEditProfileTab(
                    initialName = userName,
                    initialNisn = userNisn,
                    initialClass = userClass,
                    isUpdating = isUpdatingProfile,
                    onSaveProfile = { name, nisn, kelas, newPw ->
                        viewModel.updateProfile(name, nisn, kelas, newPw)
                    },
                    onLogoutClick = { showLogoutDialog = true }
                )
            }
        }
    }

    // Modal Bottom Sheet: Keranjang Belanja Pre-Order
    if (showCartSheet) {
        PembeliCartBottomSheet(
            cartItems = cartItems,
            totalPrice = totalCartPrice,
            isCheckoutLoading = isCheckoutLoading,
            onDismiss = { showCartSheet = false },
            onIncreaseQuantity = { viewModel.increaseQuantity(it) },
            onDecreaseQuantity = { viewModel.decreaseQuantity(it) },
            onRemoveItem = { viewModel.removeFromCart(it) },
            onCheckout = {
                viewModel.checkoutPreOrder()
                showCartSheet = false
            }
        )
    }

    // Modal Dialog: Layar Tiket QR Code (Sesuai Activity Diagram)
    activeTicketOrder?.let { order ->
        QrTicketDialog(
            order = order,
            onDismiss = { viewModel.closeTicket() },
            onCancelOrder = { viewModel.cancelPreOrder(it) }
        )
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Konfirmasi Keluar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextDark
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin keluar dari akun Pembeli?",
                    fontSize = 13.sp,
                    color = TextMuted
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
                    shape = RoundedCornerShape(8.dp)
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
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// ==========================================
// TAB 0: BERANDA (PERSIS SESUAI MOCKUP GAMBAR USER)
// ==========================================
@Composable
fun PembeliMockupHomeTab(
    userName: String,
    userClass: String,
    products: List<ProductResponse>,
    allProductsCount: Int,
    isLoading: Boolean,
    selectedCategory: String,
    searchQuery: String,
    cartItems: List<CartItem>,
    onSelectCategory: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onAddToCart: (ProductResponse) -> Unit,
    onIncreaseQuantity: (String) -> Unit,
    onDecreaseQuantity: (String) -> Unit,
    onRefresh: () -> Unit
) {
    val categories = remember { listOf("Semua", "Makanan", "Minuman", "Snack", "Paket") }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 105.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. Header Bagian Atas
        item(span = { GridItemSpan(2) }) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "KANTIN PKK DIGITAL SMKN 8",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = HeaderAmberGold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Hai, $userName",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Kelas $userClass • Siswa Aktif",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextMuted
                )
            }
        }

        // 2. Rounded Search Bar dengan Filter Icon
        item(span = { GridItemSpan(2) }) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = "Cari nasi bakar, risol mayo, es teh...",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Cari",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filter",
                            tint = Color(0xFF57534E),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                singleLine = true,
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = DarkMochaHero,
                    unfocusedBorderColor = Color(0xFFE5E0D8),
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )
        }

        // 3. Category Chips Horizontal Row
        item(span = { GridItemSpan(2) }) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory.equals(cat, ignoreCase = true) ||
                            (cat.equals("Semua", ignoreCase = true) && selectedCategory.equals("Semua Menu", ignoreCase = true))
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (isSelected) DarkMochaHero else ChipUnselectedBg,
                        modifier = Modifier.clickable { onSelectCategory(cat) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (cat.equals("Paket", ignoreCase = true)) {
                                Icon(
                                    imageVector = Icons.Default.LocalOffer,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else ChipUnselectedText,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (isSelected) Color.White else ChipUnselectedText
                            )
                        }
                    }
                }
            }
        }

        // 4. Hero Banner Card "JADWAL AMBIL"
        item(span = { GridItemSpan(2) }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkMochaHero),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Clock circle badge
                    Surface(
                        shape = CircleShape,
                        color = HeroIconBg,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Jadwal Ambil",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = VibrantOrange
                            ) {
                                Text(
                                    text = "JADWAL AMBIL",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Istirahat I",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "09.40 – 09.55 WIB",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Tunjukkan QR setelah pesanan dikonfirmasi",
                            fontSize = 11.sp,
                            color = Color(0xFFD5C7BF)
                        )
                    }
                }
            }
        }

        // 5. Section Title "Menu PKK Hari Ini" dengan "X Pilihan" & "Live Stock 🔴"
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Menu PKK Hari Ini",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SoftPeachBadge
                    ) {
                        Text(
                            text = "${if (products.isNotEmpty()) products.size else allProductsCount} Pilihan",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VibrantOrange,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Live Stock",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Surface(
                        shape = CircleShape,
                        color = LiveStockDot,
                        modifier = Modifier.size(7.dp)
                    ) {}
                }
            }
        }

        // 6. Loading or Empty State or Product Cards Grid
        if (isLoading) {
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = VibrantOrange, modifier = Modifier.size(32.dp))
                }
            }
        } else if (products.isEmpty()) {
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Fastfood,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tidak ada menu yang sesuai.",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            items(products, key = { it.id }) { product ->
                val inCartItem = cartItems.find { it.product.id == product.id }
                val currentQty = inCartItem?.quantity ?: 0
                val productIndex = products.indexOf(product)

                MockupProductCard(
                    product = product,
                    index = productIndex,
                    cartQuantity = currentQty,
                    onAddToCart = { onAddToCart(product) },
                    onIncrease = { onIncreaseQuantity(product.id) },
                    onDecrease = { onDecreaseQuantity(product.id) }
                )
            }
        }
    }
}

/**
 * Kartu Produk sesuai desain persis pada tangkapan layar user:
 * - Kategori tag di kiri atas gambar
 * - Pill stok di kanan atas gambar ("Stok: 14 pcs", "Sisa 3 pcs!")
 * - Warna background placeholder dinamis (Orange, Abu, Amber, Slate) jika tidak ada gambar
 * - Judul produk, deskripsi ringkas, harga Rp format
 * - Tombol lingkaran oranye terang "+"
 */
@Composable
fun MockupProductCard(
    product: ProductResponse,
    index: Int,
    cartQuantity: Int,
    onAddToCart: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    val isOutOfStock = product.stock <= 0
    val isBeverage = (product.category ?: "").contains("minuman", ignoreCase = true)

    // Palette placeholder fallback jika gambar belum diupload sesuai mockup
    val placeholderColors = remember {
        listOf(
            Color(0xFFFF5722), // Vibrant Orange (Kuliner / Geprek)
            Color(0xFF474E51), // Charcoal Grey (PKK / Es Teh)
            Color(0xFFFFA726), // Amber Warm (Camilan)
            Color(0xFF5C6B73)  // Slate Dimsum
        )
    }
    val fallbackBg = placeholderColors[index % placeholderColors.size]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isOutOfStock) Modifier.alpha(0.6f) else Modifier),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFEFEAE2))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Container Gambar Atas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.15f)
                    .background(fallbackBg)
            ) {
                ProductImage(
                    imageUrl = product.imageUrl,
                    category = product.category,
                    contentDescription = product.name ?: "",
                    modifier = Modifier.fillMaxSize()
                )

                // Tag Kategori di Kiri Atas
                Surface(
                    shape = RoundedCornerShape(5.dp),
                    color = Color.Black.copy(alpha = 0.45f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = product.category?.ifBlank { "PKK" } ?: "PKK",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Tag Stok di Kanan Atas
                Surface(
                    shape = RoundedCornerShape(5.dp),
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = when {
                            isOutOfStock -> "Stok Habis"
                            product.stock <= 3 -> "Sisa ${product.stock} pcs!"
                            isBeverage -> "Stok: ${product.stock} cup"
                            else -> "Stok: ${product.stock} pcs"
                        },
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (product.stock <= 3) VibrantOrange else TextDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Container Detail Bawah
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                // Judul Produk
                Text(
                    text = product.name ?: "",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp,
                    modifier = Modifier.height(34.dp)
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Deskripsi Singkat
                Text(
                    text = product.description?.ifBlank { "Menu spesial kantin PKK SMKN 8" }
                        ?: "Menu spesial kantin PKK SMKN 8",
                    fontSize = 11.sp,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Harga & Tombol Aksi Tambah Oranye
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.price.toRupiahFormat(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark
                    )

                    if (isOutOfStock) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFF3EFEA)
                        ) {
                            Text(
                                text = "Habis",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    } else if (cartQuantity > 0) {
                        // Stepper mini jika sudah dipilih
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFFEE6D8),
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { onDecrease() }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = "-",
                                        tint = VibrantOrange,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }

                            Text(
                                text = "$cartQuantity",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = TextDark
                            )

                            Surface(
                                shape = CircleShape,
                                color = VibrantOrange,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { onIncrease() }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "+",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        // Tombol Oranye "+" sesuai mockup
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = VibrantOrange,
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { onAddToCart() }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Tambah ke Keranjang",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 1: RIWAYAT PESANAN SAYA (HANYA MILIK USER LOGIN)
// ==========================================
@Composable
fun PembeliOrdersTab(
    userName: String,
    orders: List<OrderData>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onOpenTicket: (OrderData) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("Semua") } // "Semua", "Pending", "Selesai"

    val filtered = remember(orders, selectedFilter) {
        when (selectedFilter) {
            "Pending" -> orders.filter {
                it.status.isNullOrBlank() ||
                        it.status.equals("pending", ignoreCase = true) ||
                        it.status.equals("proses", ignoreCase = true)
            }
            "Selesai" -> orders.filter {
                it.status.equals("completed", ignoreCase = true) ||
                        it.status.equals("sukses", ignoreCase = true) ||
                        it.status.equals("selesai", ignoreCase = true)
            }
            else -> orders
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Pesanan Saya",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark
                    )
                    Text(
                        text = "Hanya menampilkan riwayat pesanan milik Anda ($userName)",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, contentDescription = "Muat Ulang", tint = DarkMochaHero)
                }
            }
        }

        // Filter Chip Status
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Semua", "Pending", "Selesai").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (isSelected) DarkMochaHero else Color.White,
                        border = BorderStroke(1.dp, if (isSelected) DarkMochaHero else Color(0xFFE5E0D8)),
                        modifier = Modifier.clickable { selectedFilter = filter }
                    ) {
                        Text(
                            text = if (filter == "Pending") "Menunggu Pengambilan" else filter,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextDark,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = VibrantOrange, modifier = Modifier.size(32.dp))
                }
            }
        } else if (filtered.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Belum ada pesanan ${if (selectedFilter != "Semua") selectedFilter.lowercase() else ""} untuk akun Anda.",
                            color = TextMuted,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Silakan pesan menu di tab Beranda.",
                            color = HeaderAmberGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            items(filtered, key = { it.id ?: it.qrCode ?: "" }) { order ->
                val isPending = order.status.isNullOrBlank() ||
                        order.status.equals("pending", ignoreCase = true) ||
                        order.status.equals("proses", ignoreCase = true)
                val shortId = order.id?.take(8)?.uppercase() ?: order.qrCode?.takeLast(8)?.uppercase() ?: "-"

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenTicket(order) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, Color(0xFFEFEAE2))
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SoftPeachBadge
                                ) {
                                    Text(
                                        text = "#$shortId",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VibrantOrange,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    )
                                }

                                Text(
                                    text = formatOrderDate(order.displayCreatedAt),
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }

                            val isCancelled = order.isCancelled
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when {
                                    isCancelled -> Color(0xFFF3F4F6)
                                    isPending -> SoftPeachBadge
                                    else -> GreenSuccessContainer
                                }
                            ) {
                                Text(
                                    text = when {
                                        isCancelled -> "BATAL"
                                        isPending -> "PENDING"
                                        else -> "SELESAI"
                                    },
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        isCancelled -> Color(0xFF6B7280)
                                        isPending -> VibrantOrange
                                        else -> GreenSuccess
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Rincian Item Ringkas
                        val displayItems = order.displayItems
                        if (displayItems.isNotEmpty()) {
                            Text(
                                text = displayItems.joinToString(", ") { "${it.quantity ?: 1}x ${it.displayProductName}" },
                                fontSize = 12.sp,
                                color = TextDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = "QR",
                                    tint = if (isPending) VibrantOrange else DarkMochaHero,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isPending) "Lihat QR Tiket Pengambilan" else "Rincian Struk",
                                    fontSize = 11.sp,
                                    color = if (isPending) VibrantOrange else DarkMochaHero,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = (order.totalAmount ?: 0.0).toRupiahFormat(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = TextDark
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 2: PROFIL SISWA & EDIT INFORMASI AKUN
// ==========================================
@Composable
fun PembeliEditProfileTab(
    initialName: String,
    initialNisn: String,
    initialClass: String,
    isUpdating: Boolean,
    onSaveProfile: (name: String, nisn: String, kelas: String, newPassword: String?) -> Unit,
    onLogoutClick: () -> Unit
) {
    var nameInput by remember(initialName) { mutableStateOf(initialName) }
    var nisnInput by remember(initialNisn) { mutableStateOf(initialNisn) }
    var classInput by remember(initialClass) { mutableStateOf(initialClass) }

    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    var localError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Avatar Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = BorderStroke(1.dp, Color(0xFFEFEAE2))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = SoftPeachBadge,
                    modifier = Modifier.size(68.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = VibrantOrange,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = nameInput.ifBlank { "Siswa Pembeli" },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(2.dp))

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DarkMochaHero
                ) {
                    Text(
                        text = "SISWA PEMBELI AKTIF",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Form Edit Profil Akun
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = BorderStroke(1.dp, Color(0xFFEFEAE2))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = "Edit Informasi Akun",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = TextDark
                )
                Text(
                    text = "Perbarui nama, NISN, kelas, dan kata sandi akun Anda.",
                    fontSize = 11.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Field: Nama Lengkap
                Text(text = "Nama Lengkap", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it; localError = null },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkMochaHero,
                        unfocusedBorderColor = Color(0xFFE5E0D8)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Field: NISN
                Text(text = "Nomor NISN", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = nisnInput,
                    onValueChange = { nisnInput = it; localError = null },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkMochaHero,
                        unfocusedBorderColor = Color(0xFFE5E0D8)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Field: Kelas & Jurusan
                Text(text = "Kelas & Jurusan (Contoh: XII RPL)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = classInput,
                    onValueChange = { classInput = it; localError = null },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkMochaHero,
                        unfocusedBorderColor = Color(0xFFE5E0D8)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFEFEAE2))
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Ganti Kata Sandi (Opsional)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = "Biarkan kosong jika tidak ingin mengubah kata sandi.",
                    fontSize = 11.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Field: Password Baru
                Text(text = "Kata Sandi Baru", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it; localError = null },
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
                        focusedBorderColor = DarkMochaHero,
                        unfocusedBorderColor = Color(0xFFE5E0D8)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Field: Konfirmasi Password Baru
                Text(text = "Konfirmasi Kata Sandi Baru", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = confirmPasswordInput,
                    onValueChange = { confirmPasswordInput = it; localError = null },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkMochaHero,
                        unfocusedBorderColor = Color(0xFFE5E0D8)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                localError?.let { err ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = err, color = RedError, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Tombol Simpan Perubahan
                Button(
                    onClick = {
                        if (nameInput.isBlank()) {
                            localError = "Nama lengkap tidak boleh kosong."
                            return@Button
                        }
                        if (nisnInput.isBlank()) {
                            localError = "Nomor NISN tidak boleh kosong."
                            return@Button
                        }
                        if (classInput.isBlank()) {
                            localError = "Kelas tidak boleh kosong."
                            return@Button
                        }
                        if (passwordInput.isNotBlank()) {
                            if (passwordInput.length < 6) {
                                localError = "Kata sandi baru minimal harus 6 karakter."
                                return@Button
                            }
                            if (passwordInput != confirmPasswordInput) {
                                localError = "Konfirmasi kata sandi baru tidak cocok."
                                return@Button
                            }
                        }

                        onSaveProfile(
                            nameInput,
                            nisnInput,
                            classInput,
                            passwordInput.ifBlank { null }
                        )
                        passwordInput = ""
                        confirmPasswordInput = ""
                    },
                    enabled = !isUpdating,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VibrantOrange,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Simpan Perubahan Akun", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol Keluar dari Akun
        Button(
            onClick = onLogoutClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = RedError.copy(alpha = 0.08f),
                contentColor = RedError
            ),
            border = BorderStroke(1.dp, RedError.copy(alpha = 0.25f)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Keluar dari Akun", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

// ==========================================
// MODAL BOTTOM SHEET: KERANJANG BELANJA
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PembeliCartBottomSheet(
    cartItems: List<CartItem>,
    totalPrice: Double,
    isCheckoutLoading: Boolean,
    onDismiss: () -> Unit,
    onIncreaseQuantity: (String) -> Unit,
    onDecreaseQuantity: (String) -> Unit,
    onRemoveItem: (String) -> Unit,
    onCheckout: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .padding(bottom = 20.dp)
        ) {
            // Header Keranjang
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = VibrantOrange,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Keranjang Pre-Order",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
                }
            }

            HorizontalDivider(color = Color(0xFFEFEAE2), modifier = Modifier.padding(vertical = 10.dp))

            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Keranjang Anda masih kosong", color = TextMuted, fontSize = 13.sp)
                }
            } else {
                // List Item Belanja
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(cartItems, key = { it.product.id }) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = PageBgWarm),
                            border = BorderStroke(1.dp, Color(0xFFE5E0D8))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(PlaceholderWarm)
                                ) {
                                    ProductImage(
                                        imageUrl = item.product.imageUrl,
                                        category = item.product.category,
                                        contentDescription = item.product.name ?: "",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.product.name ?: "",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = item.product.price.toRupiahFormat(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = VibrantOrange
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFFEE6D8),
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clickable { onDecreaseQuantity(item.product.id) }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.Remove,
                                                contentDescription = "-",
                                                tint = VibrantOrange,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = "${item.quantity}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )

                                    Surface(
                                        shape = CircleShape,
                                        color = VibrantOrange,
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clickable { onIncreaseQuantity(item.product.id) }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.Add,
                                                contentDescription = "+",
                                                tint = Color.White,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Pre-order pickup note
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SoftPeachBadge,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = VibrantOrange,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Pesanan akan dibuatkan QR Code. Tunjukkan QR ke Kasir PKK pada jam Istirahat I (09.40 – 09.55 WIB) untuk pengambilan.",
                            fontSize = 11.sp,
                            color = DarkMochaHero,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Total Rincian
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Pembayaran:", fontSize = 13.sp, color = TextMuted)
                    Text(
                        text = totalPrice.toRupiahFormat(),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tombol "Pesan Sekarang (Checkout)"
                Button(
                    onClick = onCheckout,
                    enabled = !isCheckoutLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VibrantOrange,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (isCheckoutLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text(
                            text = "Konfirmasi & Buat Tiket QR",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// MODAL DIALOG: LAYAR TIKET QR CODE (ACTIVITY DIAGRAM)
// ==========================================
@Composable
fun QrTicketDialog(
    order: OrderData,
    onDismiss: () -> Unit,
    onCancelOrder: ((String) -> Unit)? = null
) {
    val qrCodeString = order.qrCode ?: "QR-PREORDER-${order.id?.take(8)?.uppercase() ?: "1043"}"
    val qrBitmap = remember(qrCodeString) {
        QrCodeUtil.generateQrBitmap(qrCodeString, size = 450)
    }

    val isPending = order.isPending
    val isCancelled = order.isCancelled

    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tiket Pengambilan PKK",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when {
                        isCancelled -> Color(0xFFF3F4F6)
                        isPending -> SoftPeachBadge
                        else -> GreenSuccessContainer
                    }
                ) {
                    Text(
                        text = when {
                            isCancelled -> "PESANAN DIBATALKAN"
                            isPending -> "MENUNGGU PENGAMBILAN (PENDING)"
                            else -> "SELESAI / SUDAH DIAMBIL"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isCancelled -> Color(0xFF4B5563)
                            isPending -> VibrantOrange
                            else -> GreenSuccess
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Container QR Code Bitmap
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(2.dp, Color(0xFFE5E0D8)),
                    shadowElevation = 3.dp,
                    modifier = Modifier.size(200.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (qrBitmap != null) {
                            Image(
                                bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = "QR Code Tiket",
                                modifier = Modifier
                                    .size(180.dp)
                                    .padding(8.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = "QR Code",
                                tint = DarkMochaHero,
                                modifier = Modifier.size(120.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Kode Tiket Text
                Text(
                    text = qrCodeString,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkMochaHero
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Instruksi Sesuai Diagram
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SoftPeachBadge,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "📌 Tunjukkan QR Code ini ke Kasir saat Istirahat",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkMochaHero
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Kasir akan memindai QR Code untuk memeriksa kecocokan fisik barang dan mengubah status pesanan menjadi Selesai.",
                            fontSize = 10.sp,
                            color = DarkMochaHero.copy(alpha = 0.8f),
                            lineHeight = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFEFEAE2))
                Spacer(modifier = Modifier.height(10.dp))

                // Rincian Pesanan
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Waktu Pesan:", fontSize = 11.sp, color = TextMuted)
                    Text(
                        formatOrderDate(order.displayCreatedAt),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark
                    )
                }

                val displayItems = order.displayItems
                if (displayItems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Item Pesanan:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    displayItems.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.quantity ?: 1}x ${item.displayProductName}",
                                fontSize = 11.sp,
                                color = TextDark
                            )
                            val sub = (item.displayPrice) * (item.quantity ?: 1)
                            Text(
                                text = sub.toRupiahFormat(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = VibrantOrange
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color(0xFFEFEAE2))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Bayar:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(
                        text = (order.totalAmount ?: 0.0).toRupiahFormat(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark
                    )
                }

                // Informasi Petugas Kasir & Serah-Terima (Bukti Resmi)
                if (order.isCompleted) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = GreenSuccessContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = GreenSuccess,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Makanan Telah Diterima!",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GreenSuccess
                                )
                            }
                            if (!order.kasirName.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Petugas Kasir: ${order.kasirName}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF14532D)
                                )
                            }
                            if (!order.completedAt.isNullOrBlank()) {
                                Text(
                                    text = "Waktu Verifikasi: ${formatOrderDate(order.completedAt)}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF14532D)
                                )
                            }
                        }
                    }
                }

                // Tombol Batalkan Pesanan (Jika masih PENDING)
                if (isPending && onCancelOrder != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = {
                            onCancelOrder(order.id ?: order.qrCode ?: "")
                            onDismiss()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                        border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Batalkan Pesanan Pre-Order", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkMochaHero,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tutup Tiket", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}
