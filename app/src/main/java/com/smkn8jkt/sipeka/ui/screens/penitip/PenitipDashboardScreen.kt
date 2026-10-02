package com.smkn8jkt.sipeka.ui.screens.penitip

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.smkn8jkt.sipeka.ui.components.SipekaDropdownField
import com.smkn8jkt.sipeka.ui.components.SipekaTextField
import com.smkn8jkt.sipeka.ui.screens.auth.AuthViewModel
import com.smkn8jkt.sipeka.ui.screens.pos.ProductImage
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
fun PenitipDashboardScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: PenitipViewModel = viewModel(),
    authViewModel: AuthViewModel? = null,
    tokenManager: TokenManager? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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

    var showAddDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchDashboard()
    }

    LaunchedEffect(isAddSuccess) {
        if (isAddSuccess) {
            showAddDialog = false
            viewModel.resetAddSuccess()
        }
    }

    LaunchedEffect(updateProfileSuccess) {
        updateProfileSuccess?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            showProfileDialog = false
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

    var penitipSearchQuery by remember { mutableStateOf("") }
    var selectedPenitipCategory by remember { mutableStateOf("Semua") }
    val penitipCategories = remember { listOf("Semua", "Makanan", "Minuman", "Snack", "Paket") }

    val filteredProducts = remember(products, penitipSearchQuery, selectedPenitipCategory) {
        products.filter { product ->
            val effectiveCat = product.effectiveCategory
            val matchesCategory = selectedPenitipCategory.equals("Semua", ignoreCase = true) ||
                    effectiveCat.equals(selectedPenitipCategory, ignoreCase = true)
            val matchesSearch = penitipSearchQuery.isBlank() ||
                    (product.name ?: "").contains(penitipSearchQuery, ignoreCase = true) ||
                    (product.description ?: "").contains(penitipSearchQuery, ignoreCase = true) ||
                    effectiveCat.contains(penitipSearchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
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
                                    text = "Dashboard Penitip",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextLight
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = BgWarmTan
                                ) {
                                    Text(
                                        text = "MITRA",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = BtnDarkChocolate,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Monitoring Produk & Bagi Hasil",
                                fontSize = 11.sp,
                                color = BgWarmTan.copy(alpha = 0.85f)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.fetchDashboard() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = TextLight
                        )
                    }
                    IconButton(onClick = { showProfileDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profil Akun",
                            tint = TextLight
                        )
                    }
                    IconButton(
                        onClick = {
                            scope.launch {
                                tokenManager?.clearToken()
                                authViewModel?.resetState()
                                navController.navigate(Screen.Login) {
                                popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = TextLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgDarkEspresso)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = BtnDarkChocolate,
                contentColor = BtnCreamWhite,
                shape = CircleShape,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Produk Titipan"
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 14.dp,
                bottom = 100.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // INFORMASI AKUN MITRA PENITIP CARD
            item {
                Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardCreamWhite),
                border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(BgWarmTan.copy(alpha = 0.45f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = "Toko",
                                tint = BtnDarkChocolate,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = penitipShop.ifBlank { "Kantin PKK Sejahtera" },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$penitipName • NIP/Kontak: $penitipNip",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { showProfileDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, BtnDarkChocolate),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Edit Profil",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BtnDarkChocolate
                        )
                    }
                }
            }
        }

        // HEADER CARD: PENDAPATAN BELUM DICAIRKAN (Dark Espresso Gradient)
            item {
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
                                brush = Brush.verticalGradient(
                                    colors = listOf(BgDarkEspresso, BtnDarkChocolate)
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(BtnMocha.copy(alpha = 0.35f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = "Earnings",
                                        tint = BtnCreamWhite,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Pendapatan Belum Dicairkan",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BgWarmTan
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = unpaidEarnings.toRupiahFormat(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = BtnCreamWhite
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Rumus: (Harga Jual - Rp 1.000) × Terjual",
                                fontSize = 11.sp,
                                color = BgWarmTan.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            // SECTION HEADER: DAFTAR PRODUK TITIPAN
            item {
                Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Products",
                        tint = TextDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Status Makanan & Barang Titipan",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = BtnDarkChocolate
                ) {
                    Text(
                        text = "${products.size} Menu",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BtnCreamWhite,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // SEARCH BAR & KATEGORI FILTER UNTUK PENITIP
            if (products.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = penitipSearchQuery,
                            onValueChange = { penitipSearchQuery = it },
                            placeholder = { Text("Cari produk titipan Anda...", fontSize = 12.sp, color = TextMuted) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Cari",
                                    tint = BtnDarkChocolate,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                if (penitipSearchQuery.isNotBlank()) {
                                    IconButton(onClick = { penitipSearchQuery = "" }) {
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
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextDark,
                                unfocusedTextColor = TextDark,
                                focusedContainerColor = CardCreamWhite,
                                unfocusedContainerColor = CardCreamWhite,
                                focusedBorderColor = BtnDarkChocolate,
                                unfocusedBorderColor = OutlineWarm.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(penitipCategories) { cat ->
                                val isSelected = selectedPenitipCategory.equals(cat, ignoreCase = true)
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected) BtnDarkChocolate else CardCreamWhite,
                                    border = if (!isSelected) BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.5f)) else null,
                                    modifier = Modifier.clickable { selectedPenitipCategory = cat }
                                ) {
                                    Text(
                                        text = cat,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) BtnCreamWhite else TextDark,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Memuat data produk titipan...", color = TextMuted, fontSize = 14.sp)
                    }
                }
            } else if (products.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(BtnMocha.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingBag,
                                    contentDescription = "Empty",
                                    tint = BtnDarkChocolate,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Belum Ada Produk Titipan",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tekan tombol '+' di kanan bawah untuk menitipkan produk ke Toko PKK.",
                                fontSize = 12.sp,
                                color = TextMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else if (filteredProducts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Tidak Ada Produk yang Cocok",
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
                        onDeleteProduct = { productId ->
                            viewModel.deleteProduct(productId)
                        }
                    )
                }
            }
        }
    }

    // DIALOG TAMBAH PRODUK TITIPAN
    if (showAddDialog) {
        AddProductDialog(
            isLoading = isAddLoading,
            onDismiss = { showAddDialog = false },
            onConfirmUpload = { imageUri, name, price, desc, stock, category ->
                viewModel.uploadProduct(context, imageUri, name, price, desc, stock, category)
            }
        )
    }

    // DIALOG EDIT INFORMASI AKUN PENITIP
    if (showProfileDialog) {
        EditProfilPenitipDialog(
            initialName = penitipName,
            initialNip = penitipNip,
            initialShop = penitipShop,
            isLoading = isUpdatingProfile,
            onDismiss = { showProfileDialog = false },
            onSave = { name, nip, shop, newPw ->
                viewModel.updatePenitipProfile(name, nip, shop, newPw)
            }
        )
    }
}

@Composable
fun PenitipProductCard(
    product: ProductResponse,
    onDeleteProduct: (String) -> Unit
) {
    val isOutOfStock = product.stock <= 0
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardCreamWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProductImage(
                imageUrl = product.imageUrl,
                category = product.effectiveCategory,
                contentDescription = product.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
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

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = product.price.toRupiahFormat(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BtnDarkChocolate
                )

                Spacer(modifier = Modifier.height(6.dp))

                // BADGES: KATEGORI, SISA STOK & INDIKATOR TERJUAL
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = BgWarmTan.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = product.effectiveCategory,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BtnDarkChocolate,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GreenSuccessContainer
                    ) {
                        Text(
                            text = "Terjual: ${product.totalSold} pcs",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenSuccess,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isOutOfStock) RedErrorContainer else AmberWarningContainer
                    ) {
                        Text(
                            text = if (isOutOfStock) "Sisa: Habis" else "Sisa: ${product.stock} pcs",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOutOfStock) RedError else AmberWarning,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            IconButton(
                onClick = { showDeleteConfirmDialog = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Hapus Produk",
                    tint = RedError,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Text(
                    text = "Hapus Produk Titipan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = TextDark
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menghapus produk '${product.name}' dari katalog titipan Toko PKK?",
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedError,
                        contentColor = BtnCreamWhite
                    )
                ) {
                    Text("Ya, Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Batal", color = TextMuted)
                }
            },
            containerColor = CardCreamWhite,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Composable
fun AddProductDialog(
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

    val categoryOptions = remember { listOf("Makanan", "Minuman", "Snack", "Paket") }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BtnMocha.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = "Produk Titipan",
                        tint = BtnDarkChocolate,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Tambah Produk Titipan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextDark
                    )
                    Text(
                        text = "Isi detail makanan & kategori menu",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // PHOTO PICKER & PREVIEW
                if (selectedImageUri != null) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Preview Foto Makanan",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                OutlinedButton(
                    onClick = {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BtnDarkChocolate),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Foto",
                        tint = BtnDarkChocolate,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedImageUri != null) "Ganti Foto Makanan" else "Pilih Foto Makanan (Opsional)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BtnDarkChocolate
                    )
                }

                SipekaTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = "Nama Produk",
                    placeholder = "Contoh: Nasi Goreng Spesial"
                )

                // DROPDOWN KATEGORI PRODUK & PRESET CHIPS
                SipekaDropdownField(
                    options = categoryOptions,
                    selectedOption = categoryInput,
                    onOptionSelected = { categoryInput = it },
                    label = "Kategori Produk",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = "Kategori",
                            tint = BtnDarkChocolate,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )

                // Quick Category Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categoryOptions.forEach { cat ->
                        val isSelected = categoryInput.equals(cat, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) BtnDarkChocolate else CardCreamWhite,
                            border = BorderStroke(1.dp, if (isSelected) BtnDarkChocolate else OutlineWarm.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { categoryInput = cat }
                        ) {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) BtnCreamWhite else TextDark,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }

                SipekaTextField(
                    value = priceInput,
                    onValueChange = { priceInput = it.filter { char -> char.isDigit() } },
                    label = "Harga Jual (Rp)",
                    placeholder = "Contoh: 15000",
                    keyboardType = KeyboardType.Number
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BgWarmTan.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "💡 Margin kas PKK sekolah adalah Rp 1.000 per porsi. Pendapatan bersih Anda = (Harga Jual - Rp 1.000) × Terjual.",
                        fontSize = 11.sp,
                        color = BtnDarkChocolate,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                SipekaTextField(
                    value = descInput,
                    onValueChange = { descInput = it },
                    label = "Deskripsi Makanan",
                    placeholder = "Contoh: Pedas gurih dengan topping telur"
                )

                SipekaTextField(
                    value = stockInput,
                    onValueChange = { stockInput = it.filter { char -> char.isDigit() } },
                    label = "Stok Awal Titipan (pcs)",
                    placeholder = "Contoh: 20",
                    keyboardType = KeyboardType.Number
                )
            }
        },
        confirmButton = {
            PrimaryButton(
                text = "Simpan Produk",
                isLoading = isLoading,
                enabled = nameInput.isNotBlank() && priceInput.isNotBlank() && stockInput.isNotBlank(),
                onClick = {
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
fun EditProfilPenitipDialog(
    initialName: String,
    initialNip: String,
    initialShop: String,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSave: (name: String, nip: String, shop: String, newPassword: String?) -> Unit
) {
    var nameInput by remember(initialName) { mutableStateOf(initialName) }
    var nipInput by remember(initialNip) { mutableStateOf(initialNip) }
    var shopInput by remember(initialShop) { mutableStateOf(initialShop) }
    var passwordInput by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = BtnDarkChocolate,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Edit Informasi Akun Penitip",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = TextDark
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Perbarui data pemilik, nama brand titipan, dan kata sandi akun Anda.",
                    fontSize = 12.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(text = "Nama Pemilik / Penanggung Jawab", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it; formError = null },
                    singleLine = true,
                    placeholder = { Text("Contoh: Ibu Sari", fontSize = 13.sp) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BtnDarkChocolate,
                        unfocusedBorderColor = OutlineWarm.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Nama Toko / Usaha Titipan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = shopInput,
                    onValueChange = { shopInput = it; formError = null },
                    singleLine = true,
                    placeholder = { Text("Contoh: Kantin PKK Sejahtera", fontSize = 13.sp) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BtnDarkChocolate,
                        unfocusedBorderColor = OutlineWarm.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "NIP / Nomor Kontak", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = nipInput,
                    onValueChange = { nipInput = it; formError = null },
                    singleLine = true,
                    placeholder = { Text("Contoh: PNT-8821 atau 0812...", fontSize = 13.sp) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BtnDarkChocolate,
                        unfocusedBorderColor = OutlineWarm.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

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
                        focusedBorderColor = BtnDarkChocolate,
                        unfocusedBorderColor = OutlineWarm.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                formError?.let { err ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = err, color = RedError, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nameInput.isBlank()) {
                        formError = "Nama pemilik tidak boleh kosong."
                        return@Button
                    }
                    if (passwordInput.isNotBlank() && passwordInput.length < 6) {
                        formError = "Password minimal 6 karakter."
                        return@Button
                    }
                    onSave(nameInput, nipInput, shopInput, passwordInput.ifBlank { null })
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = BtnDarkChocolate),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = BtnCreamWhite, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text("Simpan Perubahan", fontWeight = FontWeight.Bold, color = BtnCreamWhite)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Batal", color = TextMuted)
            }
        },
        containerColor = CardCreamWhite,
        shape = RoundedCornerShape(18.dp)
    )
}
