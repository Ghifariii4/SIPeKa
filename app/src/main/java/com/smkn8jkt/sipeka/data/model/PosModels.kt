package com.smkn8jkt.sipeka.data.model

import com.google.gson.annotations.SerializedName

// Product Response Model
data class ProductResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Double,
    @SerializedName("category") val category: String? = "Umum",
    @SerializedName("image_url") val imageUrl: String? = "",
    @SerializedName("description") val description: String? = "",
    @SerializedName("penitip_id") val penitipId: String? = null,
    @SerializedName("school_margin") val schoolMargin: Double? = 1000.0,
    @SerializedName("stock") val stock: Int = 0,
    @SerializedName("sold_count") val soldCount: Int? = 0,
    @SerializedName("sold_qty") val soldQty: Int? = 0,
    @SerializedName("terjual") val terjual: Int? = 0
) {
    val totalSold: Int
        get() = soldCount ?: soldQty ?: terjual ?: 0

    val effectiveCategory: String
        get() {
            val cat = category?.trim() ?: ""
            if (cat.equals("Makanan", ignoreCase = true)) return "Makanan"
            if (cat.equals("Minuman", ignoreCase = true)) return "Minuman"
            if (cat.equals("Snack", ignoreCase = true) || cat.equals("Camilan", ignoreCase = true) || cat.equals("Cemilan", ignoreCase = true)) return "Snack"
            if (cat.contains("Paket", ignoreCase = true) || cat.contains("Combo", ignoreCase = true)) return "Paket"
            if (cat.equals("Roti", ignoreCase = true) || cat.contains("Pastry", ignoreCase = true)) return "Snack"

            val lowerName = name.lowercase()
            return when {
                listOf("paket", "combo", "bundling", "hemat").any { lowerName.contains(it) } -> "Paket"
                listOf("es ", " es", "teh", "kopi", "jus", "susu", "air", "drink", "boba", "lemon", "mineral", "sirup", "tea", "coffee", "latte", "matcha", "fanta", "coca", "sprite", "milo", "nutrisari").any { lowerName.contains(it) } -> "Minuman"
                listOf("snack", "keripik", "ciki", "risol", "pastel", "gorengan", "kentang", "roti", "kue", "biskuit", "wafer", "bakwan", "tahu", "tempe", "cireng", "siomay", "dimsum", "pisang", "donat", "toast", "pudding", "puding", "chips", "molen", "lemper", "lumpia").any { lowerName.contains(it) } -> "Snack"
                cat.isNotBlank() && !cat.equals("Umum", ignoreCase = true) -> cat.replaceFirstChar { it.uppercase() }
                else -> "Makanan"
            }
        }
}

data class PenitipDashboardResponse(
    @SerializedName("products") val products: List<ProductResponse>? = emptyList(),
    @SerializedName("total_unpaid_earnings") val totalUnpaidEarnings: Double? = 0.0,
    @SerializedName("unpaid_earnings") val unpaidEarningsAlt: Double? = 0.0,
    @SerializedName("total_earnings") val totalEarningsAlt: Double? = 0.0
) {
    val effectiveEarnings: Double
        get() = totalUnpaidEarnings ?: unpaidEarningsAlt ?: totalEarningsAlt ?: 0.0
}

data class AddProductRequest(
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Double,
    @SerializedName("category") val category: String? = "Makanan",
    @SerializedName("school_margin") val schoolMargin: Double = 1000.0,
    @SerializedName("stock") val stock: Int
)

// Clock-In Request Model
data class ClockInRequest(
    @SerializedName("starting_cash") val startingCash: Double
)

// Order / Transaction Request Models
data class OrderItemRequest(
    @SerializedName("product_id") val productId: String,
    @SerializedName("quantity") val quantity: Int
)

data class OrderRequest(
    @SerializedName("items") val items: List<OrderItemRequest>,
    @SerializedName("pembeli_id") val pembeliId: String? = null,
    @SerializedName("user_id") val userId: String? = null,
    @SerializedName("customer_name") val customerName: String? = null,
    @SerializedName("order_type") val orderType: String? = "PRE-ORDER"
)

// Cart Model for ViewModel State
data class CartItem(
    val product: ProductResponse,
    var quantity: Int
) {
    val subtotal: Double
        get() = product.price * quantity
}

data class TransactionItemRequest(
    @SerializedName("product_id") val productId: String,
    @SerializedName("quantity") val quantity: Int
)

data class TransactionRequest(
    @SerializedName("items") val items: List<TransactionItemRequest>
)

data class UserData(
    @SerializedName("id") val id: String? = null,
    @SerializedName("nisn_nip") val nisnNip: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("kelas") val kelas: String? = null,
    @SerializedName("password") val password: String? = null,
    @SerializedName("is_approved") val isApproved: Boolean? = null,
    @SerializedName("isApproved") val isApprovedAlt: Boolean? = null,
    @SerializedName("approved") val approved: Boolean? = null
) {
    val isUserApproved: Boolean
        get() = isApproved == true || isApprovedAlt == true || approved == true || status.equals("active", ignoreCase = true) || status.equals("approved", ignoreCase = true)
}

data class ShiftData(
    @SerializedName("id") val id: String? = null,
    @SerializedName("kasir_id") val kasirId: String? = null,
    @SerializedName("user_id") val userId: String? = null,
    @SerializedName("start_time") val startTime: String? = null,
    @SerializedName("clock_in") val clockIn: String? = null,
    @SerializedName("end_time") val endTime: String? = null,
    @SerializedName("clock_out") val clockOut: String? = null,
    @SerializedName("starting_cash") val startingCash: Double? = null,
    @SerializedName("expected_cash") val expectedCash: Double? = null,
    @SerializedName("status") val status: String? = null
) {
    val displayStartingCash: Double
        get() = startingCash ?: expectedCash ?: 0.0

    val displayStartTime: String
        get() = startTime ?: clockIn ?: "-"

    val displayKasirId: String
        get() = kasirId ?: userId ?: "-"

    val isShiftActive: Boolean
        get() = status.equals("active", ignoreCase = true) || status.equals("open", ignoreCase = true)
}

data class TransactionData(
    @SerializedName("id") val id: String? = null,
    @SerializedName("order_id") val orderId: String? = null,
    @SerializedName("total_amount") val totalAmount: Double? = null,
    @SerializedName("qr_code") val qrCode: String? = null,
    @SerializedName("status") val status: String? = null
)

data class ScanData(
    @SerializedName("id") val id: String? = null,
    @SerializedName("order_id") val orderId: String? = null,
    @SerializedName("scanned_at") val scannedAt: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null
)

data class OrderData(
    @SerializedName("id") val id: String? = null,
    @SerializedName("pembeli_id") val pembeliId: String? = null,
    @SerializedName("user_id") val userId: String? = null,
    @SerializedName("user_name") val userName: String? = null,
    @SerializedName("customer_name") val customerName: String? = null,
    @SerializedName("customer_class") val customerClass: String? = null,
    @SerializedName("customer_nisn") val customerNisn: String? = null,
    @SerializedName("user") val user: UserData? = null,
    @SerializedName("pembeli") val pembeli: UserData? = null,
    @SerializedName("shift_id") val shiftId: String? = null,
    @SerializedName("kasir_name") val kasirName: String? = null,
    @SerializedName("qr_code") val qrCode: String? = null,
    @SerializedName("total_amount") val totalAmount: Double? = null,
    @SerializedName("order_type") val orderType: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("createdAt") val createdAtAlt: String? = null,
    @SerializedName("completed_at") val completedAt: String? = null,
    @SerializedName("items") val items: List<OrderItemData>? = null,
    @SerializedName("OrderItems") val orderItems: List<OrderItemData>? = null,
    @SerializedName("order_items") val orderItemsSnake: List<OrderItemData>? = null
) {
    val displayCreatedAt: String
        get() = createdAt ?: createdAtAlt ?: ""

    val displayCustomerName: String
        get() = customerName ?: userName ?: user?.name ?: pembeli?.name ?: "Siswa SMKN 8"

    val displayCustomerClass: String
        get() = customerClass ?: user?.kelas ?: pembeli?.kelas ?: "XII RPL"

    val displayCustomerNisn: String
        get() = customerNisn ?: user?.nisnNip ?: pembeli?.nisnNip ?: "-"

    val displayItems: List<OrderItemData>
        get() = items ?: orderItems ?: orderItemsSnake ?: emptyList()

    val isCancelled: Boolean
        get() = status.equals("cancelled", ignoreCase = true) ||
                status.equals("batal", ignoreCase = true)

    val isCompleted: Boolean
        get() = status.equals("completed", ignoreCase = true) ||
                status.equals("sukses", ignoreCase = true) ||
                status.equals("selesai", ignoreCase = true) ||
                (!completedAt.isNullOrBlank() && !isCancelled)

    val isPending: Boolean
        get() = !isCompleted && !isCancelled
}

data class OrderItemData(
    @SerializedName("id") val id: String? = null,
    @SerializedName("order_id") val orderId: String? = null,
    @SerializedName("product_id") val productId: String? = null,
    @SerializedName("product_name") val productName: String? = null,
    @SerializedName("price") val price: Double? = null,
    @SerializedName("price_snapshot") val priceSnapshot: Double? = null,
    @SerializedName("margin_snapshot") val marginSnapshot: Double? = null,
    @SerializedName("quantity") val quantity: Int? = null,
    @SerializedName("Product") val product: ProductResponse? = null,
    @SerializedName("product") val productSnake: ProductResponse? = null
) {
    val displayProductName: String
        get() = productName ?: product?.name ?: productSnake?.name ?: "Produk PKK"

    val displayPrice: Double
        get() = price ?: priceSnapshot ?: product?.price ?: productSnake?.price ?: 0.0
}

// Admin Finance Response Models
data class ProfitResponse(
    @SerializedName("total_profit") val totalProfit: Double? = 0.0,
    @SerializedName("school_profit") val schoolProfit: Double? = 0.0,
    @SerializedName("profit") val profit: Double? = 0.0,
    @SerializedName("net_profit") val netProfit: Double? = 0.0,
    @SerializedName("date") val date: String? = null
) {
    val effectiveProfit: Double
        get() = schoolProfit ?: totalProfit ?: profit ?: netProfit ?: 0.0
}

data class ShiftValidationResponse(
    @SerializedName("id") val id: String,
    @SerializedName("kasir_id") val kasirId: String? = null,
    @SerializedName("kasir_name") val kasirName: String? = null,
    @SerializedName("kasir") val kasir: UserData? = null,
    @SerializedName("user") val user: UserData? = null,
    @SerializedName("start_time") val startTime: String? = null,
    @SerializedName("end_time") val endTime: String? = null,
    @SerializedName("clock_out") val clockOut: String? = null,
    @SerializedName("starting_cash") val startingCash: Double? = null,
    @SerializedName("expected_cash") val expectedCash: Double? = null,
    @SerializedName("status") val status: String? = null
) {
    val displayKasirName: String
        get() = kasirName ?: kasir?.name ?: user?.name ?: "Petugas Kasir ${kasirId ?: ""}"

    val displayTime: String
        get() = endTime ?: clockOut ?: startTime ?: "-"
}

data class PayoutResponse(
    @SerializedName("penitip_id") val penitipId: String,
    @SerializedName("penitip_name") val penitipName: String? = null,
    @SerializedName("penitip") val penitip: UserData? = null,
    @SerializedName("total_sales") val totalSales: Double? = null,
    @SerializedName("net_amount") val netAmount: Double? = null,
    @SerializedName("payout_amount") val payoutAmount: Double? = null,
    @SerializedName("total_payout") val totalPayout: Double? = null,
    @SerializedName("status") val status: String? = null
) {
    val displayPenitipName: String
        get() = penitipName ?: penitip?.name ?: "Penitip ${penitipId}"

    val displayAmount: Double
        get() = netAmount ?: payoutAmount ?: totalPayout ?: totalSales ?: 0.0
}
