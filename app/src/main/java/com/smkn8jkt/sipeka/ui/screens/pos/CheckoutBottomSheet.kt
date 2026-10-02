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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import com.smkn8jkt.sipeka.ui.theme.TextMedium
import com.smkn8jkt.sipeka.ui.theme.TextMuted

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
        containerColor = CardCreamWhite,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
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
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BgWarmTan.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Keranjang",
                            tint = BtnDarkChocolate,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Keranjang Belanja",
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark,
                        fontSize = 18.sp
                    )
                }
                Text(
                    text = "$totalCount Item",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BtnMocha
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Cart Items List (Ergonomic Full Height Flow - No Trapped Scroll)
            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Keranjang kosong", color = TextMuted, fontSize = 13.sp)
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    cartItems.forEach { item ->
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
            HorizontalDivider(color = OutlineWarm.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(10.dp))

            // Total Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Pembayaran",
                    fontWeight = FontWeight.Bold,
                    color = TextMedium,
                    fontSize = 15.sp
                )
                Text(
                    text = totalPrice.toRupiahFormat(),
                    fontWeight = FontWeight.ExtraBold,
                    color = BtnDarkChocolate,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Input Cash Received
            Text(
                text = "Uang Diterima (Rp)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = cashReceivedInput,
                onValueChange = { cashReceivedInput = it.filter { char -> char.isDigit() } },
                placeholder = { Text("Nominal Tunai", fontSize = 13.sp, color = TextMuted.copy(alpha = 0.7f)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.PointOfSale,
                        contentDescription = "Cash",
                        tint = BtnMocha
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark,
                    focusedContainerColor = CardCreamWhite,
                    unfocusedContainerColor = CardCreamWhite,
                    focusedBorderColor = BtnDarkChocolate,
                    unfocusedBorderColor = OutlineWarm,
                    cursorColor = BtnDarkChocolate
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Preset Chips
            val quickNominals = remember(totalPrice) {
                val exact = totalPrice.toLong().toDouble()
                listOf(exact, 10000.0, 20000.0, 50000.0, 100000.0)
                    .filter { it >= totalPrice }
                    .distinct()
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(quickNominals) { nominal ->
                    val isExact = nominal == totalPrice
                    val label = if (isExact) "Uang Pas" else nominal.toRupiahFormat()

                    OutlinedButton(
                        onClick = { cashReceivedInput = nominal.toLong().toString() },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.2.dp, BtnMocha),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BtnDarkChocolate
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Kembalian
            if (cashReceived > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (changeAmount >= 0) GreenSuccessContainer else RedErrorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (changeAmount >= 0) "Kembalian:" else "Uang Kurang:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (changeAmount >= 0) GreenSuccess else RedError
                        )
                        Text(
                            text = if (changeAmount >= 0) changeAmount.toRupiahFormat() else (totalPrice - cashReceived).toRupiahFormat(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (changeAmount >= 0) GreenSuccess else RedError
                        )
                    }
                }
            }

            if (!errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(GreenSuccessContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = GreenSuccess,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Transaksi Berhasil!",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = TextDark,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Tagihan:", fontSize = 13.sp, color = TextMuted)
                        Text(lastPaidTotal.toRupiahFormat(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Uang Diterima:", fontSize = 13.sp, color = TextMuted)
                        Text(lastCashReceived.toRupiahFormat(), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = GreenSuccessContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("KEMBALIAN:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GreenSuccess)
                            Text(lastChangeAmount.toRupiahFormat(), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = GreenSuccess)
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
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BtnDarkChocolate, contentColor = BtnCreamWhite),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Transaksi Baru", fontWeight = FontWeight.Bold, color = BtnCreamWhite)
                }
            },
            containerColor = CardCreamWhite,
            shape = RoundedCornerShape(20.dp)
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardCreamWhite),
        border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.4f)),
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
                    color = TextDark
                )
                Text(
                    text = "${cartItem.product.price.toRupiahFormat()} x ${cartItem.quantity} = ${cartItem.subtotal.toRupiahFormat()}",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedIconButton(
                    onClick = onDecrease,
                    modifier = Modifier.size(28.dp),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, BtnMocha)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Kurangi",
                        modifier = Modifier.size(14.dp),
                        tint = BtnDarkChocolate
                    )
                }

                Text(
                    text = cartItem.quantity.toString(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                OutlinedIconButton(
                    onClick = onIncrease,
                    modifier = Modifier.size(28.dp),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, BtnMocha)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah",
                        modifier = Modifier.size(14.dp),
                        tint = BtnDarkChocolate
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
                        tint = RedError,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

