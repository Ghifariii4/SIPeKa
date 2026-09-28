package com.smkn8jkt.sipeka.ui.screens.penitip

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchDashboard()
    }

    LaunchedEffect(isAddSuccess) {
        if (isAddSuccess) {
            showAddDialog = false
            viewModel.resetAddSuccess()
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
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Produk Titipan"
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

            // HEADER CARD: PENDAPATAN BELUM DICAIRKAN (Dark Espresso Gradient)
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

            Spacer(modifier = Modifier.height(18.dp))

            // SECTION HEADER: DAFTAR PRODUK TITIPAN
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

            Spacer(modifier = Modifier.height(10.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Memuat data produk titipan...", color = TextMuted, fontSize = 14.sp)
                }
            } else if (products.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
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
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(products, key = { it.id }) { product ->
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
    }

    // DIALOG TAMBAH PRODUK TITIPAN
    if (showAddDialog) {
        AddProductDialog(
            isLoading = isAddLoading,
            onDismiss = { showAddDialog = false },
            onConfirmUpload = { imageUri, name, price, desc, stock ->
                viewModel.uploadProduct(context, imageUri, name, price, desc, stock)
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
                category = product.category,
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

                // BADGES: SISA STOK & INDIKATOR TERJUAL
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
    onConfirmUpload: (imageUri: Uri?, name: String, price: String, desc: String, stock: String) -> Unit
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var nameInput by remember { mutableStateOf("") }
    var priceInput by remember { mutableStateOf("") }
    var descInput by remember { mutableStateOf("") }
    var stockInput by remember { mutableStateOf("") }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Tambah Produk Titipan",
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
                        text = if (selectedImageUri != null) "Ganti Foto Makanan" else "Pilih Foto Makanan",
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

                SipekaTextField(
                    value = priceInput,
                    onValueChange = { priceInput = it.filter { char -> char.isDigit() } },
                    label = "Harga Jual (Rp)",
                    placeholder = "Contoh: 15000",
                    keyboardType = KeyboardType.Number
                )

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
                        stockInput
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
