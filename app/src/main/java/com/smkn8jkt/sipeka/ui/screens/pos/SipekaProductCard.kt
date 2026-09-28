package com.smkn8jkt.sipeka.ui.screens.pos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.smkn8jkt.sipeka.data.model.ProductResponse
import com.smkn8jkt.sipeka.ui.theme.AmberWarning
import com.smkn8jkt.sipeka.ui.theme.AmberWarningContainer
import com.smkn8jkt.sipeka.ui.theme.BorderStitch
import com.smkn8jkt.sipeka.ui.theme.BtnCreamWhite
import com.smkn8jkt.sipeka.ui.theme.BtnDarkChocolate
import com.smkn8jkt.sipeka.ui.theme.BtnMocha
import com.smkn8jkt.sipeka.ui.theme.CardCreamWhite
import com.smkn8jkt.sipeka.ui.theme.GreenSuccess
import com.smkn8jkt.sipeka.ui.theme.GreenSuccessContainer
import com.smkn8jkt.sipeka.ui.theme.PlaceholderWarm
import com.smkn8jkt.sipeka.ui.theme.RedError
import com.smkn8jkt.sipeka.ui.theme.RedErrorContainer
import com.smkn8jkt.sipeka.ui.theme.SegmentBg
import com.smkn8jkt.sipeka.ui.theme.SipekaTheme
import com.smkn8jkt.sipeka.ui.theme.TextDark
import com.smkn8jkt.sipeka.ui.theme.TextMuted
import java.text.NumberFormat
import java.util.Locale

// Helper Extension Format Rupiah
fun Double.toRupiahFormat(): String {
    val localeId = Locale.Builder().setLanguage("id").setRegion("ID").build()
    val formatter = NumberFormat.getCurrencyInstance(localeId)
    return formatter.format(this).replace("Rp", "Rp ").replace(",00", "")
}

fun buildFullImageUrl(rawUrl: String?): String? {
    if (rawUrl.isNullOrBlank()) return null
    val clean = rawUrl.trim()
    if (clean.startsWith("http://", ignoreCase = true) || clean.startsWith("https://", ignoreCase = true)) {
        return clean
    }
    val fileName = clean.removePrefix("/uploads/").removePrefix("uploads/").removePrefix("/")
    return "http://47.129.118.194/uploads/$fileName"
}

// COMPOSABLE KOMPONEN GAMBAR PRODUK REAL-TIME DENGAN COIL & FALLBACK
@Composable
fun ProductImage(
    imageUrl: String?,
    category: String?,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val fullUrl = remember(imageUrl) {
        buildFullImageUrl(imageUrl)
    }

    if (!fullUrl.isNullOrBlank()) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(fullUrl)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier,
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PlaceholderWarm),
                    contentAlignment = Alignment.Center
                ) {
                    val iconVector = when ((category ?: "").lowercase()) {
                        "minuman" -> Icons.Default.LocalCafe
                        "snack" -> Icons.Default.Fastfood
                        else -> Icons.Default.Restaurant
                    }
                    Icon(
                        imageVector = iconVector,
                        contentDescription = contentDescription,
                        tint = BtnMocha,
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PlaceholderWarm),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = contentDescription,
                        tint = BtnMocha.copy(alpha = 0.5f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        )
    } else {
        Box(
            modifier = modifier.background(PlaceholderWarm),
            contentAlignment = Alignment.Center
        ) {
            val iconVector = when ((category ?: "").lowercase()) {
                "minuman" -> Icons.Default.LocalCafe
                "snack" -> Icons.Default.Fastfood
                else -> Icons.Default.Restaurant
            }
            Icon(
                imageVector = iconVector,
                contentDescription = contentDescription,
                tint = BtnMocha,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

/**
 * Kartu Produk POS Sesuai Mockup Stitch (stitch_pkk_school_pos_dashboard)
 * 2-Kolom dengan thumbnail 4:3, badge stok, toko mitra, dan stepper counter aktif
 */
@Composable
fun SipekaProductCard(
    product: ProductResponse,
    onAddToCart: (ProductResponse) -> Unit,
    modifier: Modifier = Modifier,
    cartQuantity: Int = 0,
    onRemoveFromCart: ((ProductResponse) -> Unit)? = null
) {
    val isOutOfStock = product.stock <= 0

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardCreamWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, BorderStitch)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // Thumbnail Container 4:3 dengan Badge Stok Overlaid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PlaceholderWarm)
            ) {
                ProductImage(
                    imageUrl = product.imageUrl,
                    category = product.category,
                    contentDescription = product.name ?: "",
                    modifier = Modifier.fillMaxSize()
                )

                // Stock Badge Pill (Top-Left seperti stitch mockup)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when {
                        isOutOfStock -> RedError
                        product.stock <= 3 -> AmberWarning
                        else -> GreenSuccess.copy(alpha = 0.9f)
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                ) {
                    Text(
                        text = when {
                            isOutOfStock -> "Habis"
                            product.stock <= 3 -> "Sisa ${product.stock} (Kritis)"
                            else -> "Sisa ${product.stock}"
                        },
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = BtnCreamWhite,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Vendor / Kategori Tag
            Text(
                text = (product.category ?: "PKK").uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = BtnMocha,
                letterSpacing = 0.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Nama Produk (2 Lines Max)
            Text(
                text = product.name ?: "",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                lineHeight = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(34.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Row Harga & Stepper Counter / Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.price.toRupiahFormat(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BtnDarkChocolate
                )

                if (cartQuantity > 0 && onRemoveFromCart != null) {
                    // Counter Stepper Active State (Stitch style)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SegmentBg
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(2.dp)
                        ) {
                            IconButton(
                                onClick = { onRemoveFromCart(product) },
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(CardCreamWhite, RoundedCornerShape(6.dp))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Kurang",
                                    tint = TextDark,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            Text(
                                text = "$cartQuantity",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BtnDarkChocolate,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )

                            IconButton(
                                onClick = { onAddToCart(product) },
                                enabled = product.stock > cartQuantity,
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(BtnDarkChocolate, RoundedCornerShape(6.dp))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Tambah",
                                    tint = BtnCreamWhite,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                } else {
                    // Quick Add Button
                    Button(
                        onClick = { onAddToCart(product) },
                        enabled = !isOutOfStock,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BtnDarkChocolate,
                            contentColor = BtnCreamWhite,
                            disabledContainerColor = PlaceholderWarm,
                            disabledContentColor = TextMuted
                        ),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah",
                            tint = BtnCreamWhite,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SipekaProductCardPreview() {
    SipekaTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SipekaProductCard(
                product = ProductResponse("1", "Risoles Mayo Keju", 7000.0, "XII IPA 1"),
                onAddToCart = {},
                cartQuantity = 2
            )
        }
    }
}
