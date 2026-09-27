package com.smkn8jkt.sipeka.ui.screens.pos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smkn8jkt.sipeka.data.model.CartItem
import com.smkn8jkt.sipeka.ui.components.PrimaryButton
import com.smkn8jkt.sipeka.ui.theme.BackgroundNeutral
import com.smkn8jkt.sipeka.ui.theme.PrimaryBrown
import com.smkn8jkt.sipeka.ui.theme.SecondaryBrown

private val DarkBrown = Color(0xFF3E2723)
private val PrimaryOrange = Color(0xFFFF7043)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutBottomSheet(
    viewModel: PosViewModel,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()
    val isCheckoutLoading by viewModel.isCheckoutLoading.collectAsState()
    val checkoutSuccess by viewModel.checkoutSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var cashReceivedInput by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var lastPaidTotal by remember { mutableDoubleStateOf(0.0) }
    var lastCashReceived by remember { mutableDoubleStateOf(0.0) }
    var lastChangeAmount by remember { mutableDoubleStateOf(0.0) }

    val cashReceived = cashReceivedInput.toDoubleOrNull() ?: 0.0
    val changeAmount = cashReceived - totalPrice
    val isPaymentValid = cashReceived >= totalPrice && cartItems.isNotEmpty()

    LaunchedEffect(checkoutSuccess) {
        if (checkoutSuccess) {
            lastPaidTotal = totalPrice
            lastCashReceived = cashReceived
            lastChangeAmount = if (cashReceived >= totalPrice) cashReceived - totalPrice else 0.0
            showSuccessDialog = true
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BackgroundNeutral,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Keranjang",
                        tint = PrimaryBrown,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Keranjang Belanja",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryBrown,
                        fontSize = 18.sp
                    )
                }
                Text(
                    text = "$totalCount Item",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cart Items List
            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Keranjang kosong", color = Color.Gray, fontSize = 13.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cartItems, key = { it.product.id }) { item ->
                        CartItemRow(
                            cartItem = item,
                            onIncrease = { viewModel.increaseQuantity(item.product.id) },
                            onDecrease = { viewModel.decreaseQuantity(item.product.id) },
                            onRemove = { viewModel.removeFromCart(item.product.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFE0E0E0))
            Spacer(modifier = Modifier.height(10.dp))

            // Total Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Pembayaran",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SecondaryBrown
                )
                Text(
                    text = totalPrice.toRupiahFormat(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBrown,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Input Cash Received
            Text(
                text = "Uang Diterima (Rp)",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkBrown
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = cashReceivedInput,
                onValueChange = { cashReceivedInput = it.filter { char -> char.isDigit() } },
                placeholder = { Text("Nominal Tunai", fontSize = 13.sp, color = Color.Gray) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.PointOfSale,
                        contentDescription = "Cash",
                        tint = PrimaryBrown
                    )
                },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = PrimaryBrown,
                    unfocusedBorderColor = Color(0xFFCCCCCC)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Preset Chips
            val quickNominals = remember(totalPrice) {
                val exact = totalPrice.toLong().toDouble()
                listOf(exact, 10000.0, 20000.0, 50000.0, 100000.0)
                    .filter { it >= totalPrice }
                    .distinct()
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(quickNominals) { nominal ->
                    val isExact = nominal == totalPrice
                    val label = if (isExact) "Uang Pas" else nominal.toRupiahFormat()

                    OutlinedButton(
                        onClick = { cashReceivedInput = nominal.toLong().toString() },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, PrimaryBrown),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBrown
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Kembalian
            if (cashReceived > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (changeAmount >= 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (changeAmount >= 0) "Kembalian:" else "Uang Kurang:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (changeAmount >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                        Text(
                            text = if (changeAmount >= 0) changeAmount.toRupiahFormat() else (totalPrice - cashReceived).toRupiahFormat(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (changeAmount >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    }
                }
            }

            if (!errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            PrimaryButton(
                text = "BAYAR & SELESAI",
                onClick = { viewModel.checkout() },
                isLoading = isCheckoutLoading,
                enabled = isPaymentValid
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                viewModel.resetCheckoutState()
                onDismiss()
            },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Transaksi Berhasil!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = DarkBrown,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total:", fontSize = 12.sp, color = Color.Gray)
                        Text(lastPaidTotal.toRupiahFormat(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkBrown)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Diterima:", fontSize = 12.sp, color = Color.Gray)
                        Text(lastCashReceived.toRupiahFormat(), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkBrown)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE8F5E9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("KEMBALIAN:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            Text(lastChangeAmount.toRupiahFormat(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        viewModel.resetCheckoutState()
                        onDismiss()
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Transaksi Baru", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Composable
fun CartItemRow(
    cartItem: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                    text = cartItem.product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = SecondaryBrown
                )
                Text(
                    text = "${cartItem.product.price.toRupiahFormat()} x ${cartItem.quantity} = ${cartItem.subtotal.toRupiahFormat()}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedIconButton(
                    onClick = onDecrease,
                    modifier = Modifier.size(28.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Kurangi",
                        modifier = Modifier.size(14.dp),
                        tint = PrimaryBrown
                    )
                }

                Text(
                    text = cartItem.quantity.toString(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SecondaryBrown,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                OutlinedIconButton(
                    onClick = onIncrease,
                    modifier = Modifier.size(28.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah",
                        modifier = Modifier.size(14.dp),
                        tint = PrimaryBrown
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
