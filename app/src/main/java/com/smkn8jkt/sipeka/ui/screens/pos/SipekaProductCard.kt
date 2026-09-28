package com.smkn8jkt.sipeka.ui.screens.pos

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.smkn8jkt.sipeka.R
import com.smkn8jkt.sipeka.data.model.ProductResponse
import com.smkn8jkt.sipeka.ui.theme.PrimaryBrown
import com.smkn8jkt.sipeka.ui.theme.SecondaryBrown
import com.smkn8jkt.sipeka.ui.theme.SipekaTheme
import com.smkn8jkt.sipeka.ui.theme.TextOnPrimary
import java.text.NumberFormat
import java.util.Locale

// Helper Extension Format Rupiah
fun Double.toRupiahFormat(): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return formatter.format(this).replace("Rp", "Rp ").replace(",00", "")
}

fun buildFullImageUrl(rawUrl: String?): String? {
    if (rawUrl.isNullOrBlank()) return null
    val clean = rawUrl.trim()
    if (clean.startsWith("http://", ignoreCase = true) || clean.startsWith("https://", ignoreCase = true)) {
        return clean
    }
    val fileName = clean.removePrefix("/uploads/").removePrefix("uploads/").removePrefix("/")
    return "http://47.129.118.194:8081/uploads/$fileName"
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

    if (fullUrl != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(fullUrl)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.background(Color(0xFFF0EAE1)),
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
                tint = PrimaryBrown.copy(alpha = 0.7f),
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
fun SipekaProductCard(
    product: ProductResponse,
    onAddToCart: (ProductResponse) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            ProductImage(
                imageUrl = product.imageUrl,
                category = product.category,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = SecondaryBrown,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.price.toRupiahFormat(),
                        style = MaterialTheme.typography.labelLarge,
                        color = PrimaryBrown,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = { onAddToCart(product) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBrown,
                            contentColor = TextOnPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Tambah",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
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
                product = ProductResponse("1", "Nasi Goreng Spesial", 15000.0, "Makanan"),
                onAddToCart = {}
            )
        }
    }
}
