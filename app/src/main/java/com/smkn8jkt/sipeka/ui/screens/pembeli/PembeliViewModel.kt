package com.smkn8jkt.sipeka.ui.screens.pembeli

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.smkn8jkt.sipeka.data.model.CartItem
import com.smkn8jkt.sipeka.data.model.OrderData
import com.smkn8jkt.sipeka.data.model.OrderItemData
import com.smkn8jkt.sipeka.data.model.OrderItemRequest
import com.smkn8jkt.sipeka.data.model.OrderRequest
import com.smkn8jkt.sipeka.data.model.ProductResponse
import com.smkn8jkt.sipeka.data.model.UserData
import com.smkn8jkt.sipeka.data.remote.ApiClient
import com.smkn8jkt.sipeka.data.remote.ApiService
import com.smkn8jkt.sipeka.data.remote.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class PembeliViewModel(
    private val apiService: ApiService = ApiClient.apiService,
    private val tokenManager: TokenManager? = null
) : ViewModel() {

    // Identitas User Pembeli
    val userName: StateFlow<String> = (tokenManager?.userNameFlow?.map { it ?: "Raysha Putra" } ?: MutableStateFlow("Raysha Putra"))
        .stateIn(viewModelScope, SharingStarted.Eagerly, tokenManager?.getUserNameSync() ?: "Raysha Putra")

    val userClass: StateFlow<String> = (tokenManager?.userClassFlow?.map { it ?: "XII RPL" } ?: MutableStateFlow("XII RPL"))
        .stateIn(viewModelScope, SharingStarted.Eagerly, tokenManager?.getUserClassSync() ?: "XII RPL")

    val userNisn: StateFlow<String> = (tokenManager?.userNisnFlow?.map { it ?: "54321" } ?: MutableStateFlow("54321"))
        .stateIn(viewModelScope, SharingStarted.Eagerly, tokenManager?.getUserNisnSync() ?: "54321")

    val userId: StateFlow<String?> = (tokenManager?.userIdFlow ?: MutableStateFlow(null))
        .stateIn(viewModelScope, SharingStarted.Eagerly, tokenManager?.getUserIdSync())

    // 1. Data Produk & Katalog
    private val _products = MutableStateFlow<List<ProductResponse>>(emptyList())
    val products: StateFlow<List<ProductResponse>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Filter produk berdasarkan Kategori dan Search Query
    val filteredProducts: StateFlow<List<ProductResponse>> = combine(
        _products,
        _selectedCategory,
        _searchQuery
    ) { prods, category, query ->
        prods.filter { product ->
            val effectiveCat = product.effectiveCategory
            val matchCategory = when {
                category.equals("Semua Menu", ignoreCase = true) || category.equals("Semua", ignoreCase = true) -> true
                category.equals("Paket", ignoreCase = true) -> effectiveCat.equals("Paket", ignoreCase = true) || (product.name ?: "").contains("paket", ignoreCase = true)
                else -> effectiveCat.equals(category, ignoreCase = true)
            }

            val matchQuery = if (query.isBlank()) {
                true
            } else {
                (product.name ?: "").contains(query, ignoreCase = true) ||
                        (product.description ?: "").contains(query, ignoreCase = true) ||
                        effectiveCat.contains(query, ignoreCase = true)
            }

            matchCategory && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // 2. Keranjang Belanja Pembeli
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    val totalCartPrice: StateFlow<Double> = combine(_cartItems) { items ->
        items.first().sumOf { it.subtotal }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val totalCartCount: StateFlow<Int> = combine(_cartItems) { items ->
        items.first().sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    // 3. Checkout Pre-Order & Layar Tiket QR Code
    private val _isCheckoutLoading = MutableStateFlow(false)
    val isCheckoutLoading: StateFlow<Boolean> = _isCheckoutLoading.asStateFlow()

    private val _activeTicketOrder = MutableStateFlow<OrderData?>(null)
    val activeTicketOrder: StateFlow<OrderData?> = _activeTicketOrder.asStateFlow()

    // 4. Riwayat Pesanan Saya (HANYA MILIK USER YANG SEDANG LOGIN)
    private val _myOrders = MutableStateFlow<List<OrderData>>(emptyList())
    val myOrders: StateFlow<List<OrderData>> = _myOrders.asStateFlow()

    private val _isOrdersLoading = MutableStateFlow(false)
    val isOrdersLoading: StateFlow<Boolean> = _isOrdersLoading.asStateFlow()

    // 5. Update Profil Siswa
    private val _isUpdatingProfile = MutableStateFlow(false)
    val isUpdatingProfile: StateFlow<Boolean> = _isUpdatingProfile.asStateFlow()

    private val _updateProfileSuccess = MutableStateFlow<String?>(null)
    val updateProfileSuccess: StateFlow<String?> = _updateProfileSuccess.asStateFlow()

    init {
        fetchProducts()
        fetchMyOrders()
        viewModelScope.launch {
            tokenManager?.sharedOrdersFlow?.collect {
                fetchMyOrders()
            }
        }
    }

    // --- OPERASI KATALOG & KATEGORI ---
    fun fetchProducts(query: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = apiService.getProducts(query)
                if (response.isSuccessful && response.body()?.data != null) {
                    _products.value = response.body()?.data ?: emptyList()
                } else {
                    _products.value = emptyList()
                }
            } catch (e: Exception) {
                _products.value = emptyList()
                _errorMessage.value = e.localizedMessage ?: "Gagal memuat katalog menu"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // --- OPERASI KERANJANG BELANJA SESUAI ACTIVITY DIAGRAM ---
    fun addToCart(product: ProductResponse) {
        if (product.stock <= 0) return

        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            val existing = currentList[index]
            if (existing.quantity < product.stock) {
                currentList[index] = existing.copy(quantity = existing.quantity + 1)
                _cartItems.value = currentList
            }
        } else {
            currentList.add(CartItem(product = product, quantity = 1))
            _cartItems.value = currentList
        }
    }

    fun increaseQuantity(productId: String) {
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == productId }
        if (index >= 0) {
            val existing = currentList[index]
            if (existing.quantity < existing.product.stock) {
                currentList[index] = existing.copy(quantity = existing.quantity + 1)
                _cartItems.value = currentList
            }
        }
    }

    fun decreaseQuantity(productId: String) {
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == productId }
        if (index >= 0) {
            val existing = currentList[index]
            if (existing.quantity > 1) {
                currentList[index] = existing.copy(quantity = existing.quantity - 1)
            } else {
                currentList.removeAt(index)
            }
            _cartItems.value = currentList
        }
    }

    fun removeFromCart(productId: String) {
        _cartItems.value = _cartItems.value.filter { it.product.id != productId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    // --- CHECKOUT PRE-ORDER ---
    // --- CHECKOUT PRE-ORDER (VALIDASI DILAKUKAN PENUH DI BACKEND) ---
    fun checkoutPreOrder() {
        val items = _cartItems.value
        if (items.isEmpty()) {
            _errorMessage.value = "Keranjang belanja Anda masih kosong."
            return
        }

        viewModelScope.launch {
            _isCheckoutLoading.value = true
            _errorMessage.value = null

            try {
                val currentUserId = tokenManager?.getUserIdSync().orEmpty()
                val currentName = tokenManager?.getUserNameSync() ?: "Raysha Putra"
                val currentClass = tokenManager?.getUserClassSync() ?: "XII RPL"
                val currentNisn = tokenManager?.getUserNisnSync() ?: "54321"

                val orderItems = items.map { item ->
                    OrderItemRequest(
                        productId = item.product.id,
                        quantity = item.quantity
                    )
                }
                val request = OrderRequest(
                    items = orderItems,
                    pembeliId = currentUserId.ifBlank { null },
                    userId = currentUserId.ifBlank { null },
                    customerName = currentName,
                    orderType = "PRE-ORDER"
                )

                // Panggil endpoint backend POST /api/v1/orders
                val response = apiService.createOrder(request)

                if (response.isSuccessful && response.body()?.data != null) {
                    val serverOrder = response.body()!!.data!!
                    val finalTicket = serverOrder.copy(
                        customerName = currentName,
                        customerClass = currentClass,
                        customerNisn = currentNisn
                    )

                    // Simpan ke shared bus agar kasir langsung melihat tiket di antrean kasir secara real-time
                    tokenManager?.saveSharedOrder(finalTicket)

                    // Simpan ID & QR ke set pesanan akun siswa
                    finalTicket.id?.let { tokenManager?.saveUserOrder(it) }
                    finalTicket.qrCode?.let { tokenManager?.saveUserOrder(it) }

                    _activeTicketOrder.value = finalTicket
                    _cartItems.value = emptyList()

                    // Refresh katalog stok terkini dan riwayat pesanan
                    fetchProducts()
                    fetchMyOrders()
                } else {
                    // Tangkap pesan validasi terpusat dari backend
                    val errorMsg = try {
                        val errorBody = response.errorBody()?.string()
                        if (!errorBody.isNullOrBlank()) {
                            org.json.JSONObject(errorBody).optString("message", "Gagal membuat pesanan pre-order.")
                        } else {
                            "Gagal membuat pesanan pre-order (HTTP ${response.code()})."
                        }
                    } catch (_: Exception) {
                        "Gagal membuat pesanan pre-order (HTTP ${response.code()})."
                    }
                    _errorMessage.value = errorMsg
                }
            } catch (e: Exception) {
                _errorMessage.value = "Koneksi bermasalah: ${e.localizedMessage ?: "Tidak dapat menghubungi server kantin."}"
            } finally {
                _isCheckoutLoading.value = false
            }
        }
    }

    // --- BATALKAN PRE-ORDER (HANYA JIKA STATUS MASIH PENDING) ---
    fun cancelPreOrder(orderIdOrQr: String) {
        viewModelScope.launch {
            _isOrdersLoading.value = true
            try {
                val (success, message) = tokenManager?.cancelSharedOrder(orderIdOrQr)
                    ?: Pair(false, "Sistem penyimpanan tidak dapat diakses.")
                if (success) {
                    _updateProfileSuccess.value = message
                    fetchMyOrders()
                    fetchProducts()
                    if (_activeTicketOrder.value?.id == orderIdOrQr || _activeTicketOrder.value?.qrCode == orderIdOrQr) {
                        _activeTicketOrder.value = _activeTicketOrder.value?.copy(status = "CANCELLED")
                    }
                } else {
                    _errorMessage.value = message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal membatalkan pesanan: ${e.localizedMessage}"
            } finally {
                _isOrdersLoading.value = false
            }
        }
    }

    // --- RIWAYAT PESANAN SAYA (STRICT FILTERING HANYA USER YANG LOGIN) ---
    fun fetchMyOrders() {
        viewModelScope.launch {
            _isOrdersLoading.value = true
            try {
                val currentUserId = tokenManager?.getUserIdSync().orEmpty()
                val currentNisn = tokenManager?.getUserNisnSync().orEmpty()
                val currentName = tokenManager?.getUserNameSync().orEmpty()
                val mySavedOrderIds = tokenManager?.getUserOrdersSync() ?: emptySet()
                val sharedOrders = tokenManager?.getSharedOrdersSync() ?: emptyList()

                // Filter shared orders yang milik siswa yang sedang login
                val mySharedOrders = sharedOrders.filter { order ->
                    mySavedOrderIds.contains(order.id) || mySavedOrderIds.contains(order.qrCode) ||
                    (currentUserId.isNotBlank() && (order.pembeliId == currentUserId || order.userId == currentUserId)) ||
                    (currentNisn.isNotBlank() && (order.customerNisn == currentNisn || order.user?.nisnNip == currentNisn)) ||
                    (currentName.isNotBlank() && (order.customerName.equals(currentName, ignoreCase = true) || order.userName.equals(currentName, ignoreCase = true)))
                }

                val response = try {
                    apiService.getOrders()
                } catch (_: Exception) {
                    null
                }

                val serverOrders = if (response?.isSuccessful == true && response.body()?.data != null) {
                    response.body()?.data ?: emptyList()
                } else {
                    emptyList()
                }

                val userFilteredServer = serverOrders.filter { order ->
                    val matchUserId = currentUserId.isNotBlank() && (
                        order.pembeliId == currentUserId ||
                        order.userId == currentUserId ||
                        order.user?.id == currentUserId
                    )
                    val matchNisn = currentNisn.isNotBlank() && (
                        order.user?.nisnNip.equals(currentNisn, ignoreCase = true) ||
                        (order.customerNisn != null && order.customerNisn.equals(currentNisn, ignoreCase = true))
                    )
                    val matchName = currentName.isNotBlank() && (
                        order.userName.equals(currentName, ignoreCase = true) ||
                        order.customerName.equals(currentName, ignoreCase = true) ||
                        order.user?.name.equals(currentName, ignoreCase = true)
                    )
                    val matchLocalCreated = mySavedOrderIds.contains(order.id) ||
                                            mySavedOrderIds.contains(order.qrCode)

                    matchUserId || matchNisn || matchName || matchLocalCreated
                }

                // Utamakan data dari shared orders karena menyimpan status verifikasi kasir
                val merged = (mySharedOrders + _myOrders.value + userFilteredServer).distinctBy { it.id ?: it.qrCode }
                _myOrders.value = merged

                // Sinkronisasi status tiket aktif secara real-time
                _activeTicketOrder.value?.let { currentTicket ->
                    val updated = merged.find { it.id == currentTicket.id || it.qrCode == currentTicket.qrCode }
                    if (updated != null) {
                        _activeTicketOrder.value = updated
                    }
                }
            } catch (_: Exception) {
                // Biarkan list lokal yang tersimpan
            } finally {
                _isOrdersLoading.value = false
            }
        }
    }

    // --- EDIT & PERBARUI SEMUA INFORMASI AKUN SENDIRI ---
    fun updateProfile(
        name: String,
        nisn: String,
        kelas: String,
        newPassword: String? = null
    ) {
        val cleanName = name.trim()
        val cleanNisn = nisn.trim()
        val cleanKelas = kelas.trim()
        val cleanPassword = newPassword?.trim()

        if (cleanName.isBlank()) {
            _errorMessage.value = "Nama lengkap tidak boleh kosong."
            return
        }
        if (cleanNisn.isBlank()) {
            _errorMessage.value = "NISN tidak boleh kosong."
            return
        }
        if (cleanKelas.isBlank()) {
            _errorMessage.value = "Kelas tidak boleh kosong."
            return
        }
        if (!cleanPassword.isNullOrBlank() && cleanPassword.length < 6) {
            _errorMessage.value = "Kata sandi baru minimal harus 6 karakter."
            return
        }

        viewModelScope.launch {
            _isUpdatingProfile.value = true
            _errorMessage.value = null
            _updateProfileSuccess.value = null

            try {
                val currentId = tokenManager?.getUserIdSync()
                val updatePayload = UserData(
                    id = currentId,
                    name = cleanName,
                    nisnNip = cleanNisn,
                    kelas = cleanKelas,
                    password = if (!cleanPassword.isNullOrBlank()) cleanPassword else null,
                    role = "pembeli"
                )

                // 1. Kirim pembaruan ke server backend
                var serverMessage: String? = null

                try {
                    val response = apiService.updateProfile(updatePayload)
                    if (response.isSuccessful) {
                        serverMessage = response.body()?.message
                        val serverUser = response.body()?.data
                        tokenManager?.saveUserProfile(
                            id = serverUser?.id ?: currentId,
                            name = serverUser?.name ?: cleanName,
                            nisn = serverUser?.nisnNip ?: cleanNisn,
                            kelas = serverUser?.kelas ?: cleanKelas
                        )
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errMsg = if (!errorBody.isNullOrBlank()) {
                            try {
                                val json = org.json.JSONObject(errorBody)
                                if (json.has("message")) json.getString("message") else null
                            } catch (_: Exception) { null }
                        } else null

                        _errorMessage.value = errMsg ?: "Gagal memperbarui profil di server (HTTP ${response.code()})."
                        return@launch
                    }
                } catch (netEx: Exception) {
                    // Fallback jika ada kendala koneksi spesifik
                    if (!currentId.isNullOrBlank()) {
                        try {
                            val altResp = apiService.updateUser(currentId, updatePayload)
                            if (altResp.isSuccessful) {
                                serverMessage = altResp.body()?.message
                            }
                        } catch (_: Exception) {}
                    }
                }

                // 2. Simpan perubahan ke DataStore secara persisten
                tokenManager?.saveUserProfile(
                    id = currentId,
                    name = cleanName,
                    nisn = cleanNisn,
                    kelas = cleanKelas
                )

                _updateProfileSuccess.value = serverMessage ?: "Informasi akun Anda berhasil diperbarui!"
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memperbarui profil: ${e.localizedMessage}"
            } finally {
                _isUpdatingProfile.value = false
            }
        }
    }

    fun viewTicket(order: OrderData) {
        _activeTicketOrder.value = order
    }

    fun closeTicket() {
        _activeTicketOrder.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun clearUpdateSuccess() {
        _updateProfileSuccess.value = null
    }
}

class PembeliViewModelFactory(
    private val tokenManager: TokenManager? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PembeliViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PembeliViewModel(tokenManager = tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
