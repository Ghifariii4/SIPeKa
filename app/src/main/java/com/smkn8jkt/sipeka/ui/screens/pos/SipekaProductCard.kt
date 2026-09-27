package com.smkn8jkt.sipeka.ui.screens.pos

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smkn8jkt.sipeka.data.model.ProductResponse
import com.smkn8jkt.sipeka.ui.theme.PrimaryBrown
import com.smkn8jkt.sipeka.ui.theme.SecondaryBrown
import com.smkn8jkt.sipeka.ui.theme.SipekaTheme
import com.smkn8jkt.sipeka.ui.theme.TextOnPrimary
import java.text.NumberFormat
import java.util.Locale

// Data Class Product Dummy (Compatible Wrapper)
data class ProductDummy(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val imageUrl: String = ""
)

fun ProductDummy.toProductResponse() = ProductResponse(
    id = id,
    name = name,
    price = price,
    category = category,
    imageUrl = imageUrl
)

val sampleProducts = listOf(
    ProductResponse("1", "Nasi Goreng Spesial", 15000.0, "Makanan", stock = 20),
    ProductResponse("2", "Es Teh Manis", 5000.0, "Minuman", stock = 50),
    ProductResponse("3", "Roti Bakar Cokelat", 10000.0, "Snack", stock = 15),
    ProductResponse("4", "Mie Ayam Bakso", 14000.0, "Makanan", stock = 25),
    ProductResponse("5", "Es Jeruk Peras", 6000.0, "Minuman", stock = 40),
    ProductResponse("6", "Batagor Bandung", 12000.0, "Snack", stock = 30)
)

// Helper Extension Format Rupiah
fun Double.toRupiahFormat(): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return formatter.format(this).replace("Rp", "Rp ").replace(",00", "")
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(Color(0xFFF0EAE1)),
                contentAlignment = Alignment.Center
            ) {
                val iconVector = when ((product.category ?: "").lowercase()) {
                    "minuman" -> Icons.Default.LocalCafe
                    "snack" -> Icons.Default.Fastfood
                    else -> Icons.Default.Restaurant
                }
                Icon(
                    imageVector = iconVector,
                    contentDescription = product.name,
                    tint = PrimaryBrown,
                    modifier = Modifier.size(40.dp)
                )
            }

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
