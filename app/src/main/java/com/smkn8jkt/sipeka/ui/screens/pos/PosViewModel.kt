package com.smkn8jkt.sipeka.ui.screens.pos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.smkn8jkt.sipeka.data.model.BaseResponse
import com.smkn8jkt.sipeka.data.model.CartItem
import com.smkn8jkt.sipeka.data.model.ClockInRequest
import com.smkn8jkt.sipeka.data.model.OrderData
import com.smkn8jkt.sipeka.data.model.OrderItemData
import com.smkn8jkt.sipeka.data.model.OrderItemRequest
import com.smkn8jkt.sipeka.data.model.OrderRequest
import com.smkn8jkt.sipeka.data.model.ProductResponse
import com.smkn8jkt.sipeka.data.model.ShiftData
import com.smkn8jkt.sipeka.data.remote.ApiClient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import com.smkn8jkt.sipeka.data.remote.ApiService
import com.smkn8jkt.sipeka.data.remote.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PosViewModel(
    private val apiService: ApiService = ApiClient.apiService,
    private val tokenManager: TokenManager? = null
) : ViewModel() {

    // --- Shift States ---
    private val _isShiftOpen = MutableStateFlow(false)
    val isShiftOpen: StateFlow<Boolean> = _isShiftOpen.asStateFlow()

    private val _startingCash = MutableStateFlow(0.0)
    val startingCashState: StateFlow<Double> = _startingCash.asStateFlow()

    private val _isClockInLoading = MutableStateFlow(false)
    val isClockInLoading: StateFlow<Boolean> = _isClockInLoading.asStateFlow()

    private val _isClockOutLoading = MutableStateFlow(false)
    val isClockOutLoading: StateFlow<Boolean> = _isClockOutLoading.asStateFlow()

    private val _currentShiftData = MutableStateFlow<ShiftData?>(null)
    val currentShiftData: StateFlow<ShiftData?> = _currentShiftData.asStateFlow()

    // --- Identitas Kasir ---
    val kasirName: StateFlow<String> = (tokenManager?.kasirNameFlow ?: MutableStateFlow("Petugas Kasir 1"))
        .stateIn(viewModelScope, SharingStarted.Eagerly, tokenManager?.getKasirNameSync() ?: "Petugas Kasir 1")

    val kasirNip: StateFlow<String> = (tokenManager?.kasirNipFlow ?: MutableStateFlow("19820512"))
        .stateIn(viewModelScope, SharingStarted.Eagerly, "19820512")

    private val _isUpdatingKasirProfile = MutableStateFlow(false)
    val isUpdatingKasirProfile: StateFlow<Boolean> = _isUpdatingKasirProfile.asStateFlow()

    private val _updateProfileSuccess = MutableStateFlow<String?>(null)
    val updateProfileSuccess: StateFlow<String?> = _updateProfileSuccess.asStateFlow()

    // --- History & QR States ---
    private val _orderHistory = MutableStateFlow<List<OrderData>>(emptyList())
    val orderHistory: StateFlow<List<OrderData>> = _orderHistory.asStateFlow()

    // Antrean Pre-Order Siswa yang belum diambil maupun sudah selesai
    val preOrders: StateFlow<List<OrderData>> = _orderHistory.map { orders ->
        orders.filter { it.orderType.equals("PRE-ORDER", ignoreCase = true) || !it.qrCode.isNullOrBlank() }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _selectedOrderDetail = MutableStateFlow<OrderData?>(null)
    val selectedOrderDetail: StateFlow<OrderData?> = _selectedOrderDetail.asStateFlow()

    private val _isDetailLoading = MutableStateFlow(false)
    val isDetailLoading: StateFlow<Boolean> = _isDetailLoading.asStateFlow()

    private val _qrScanMessage = MutableStateFlow<String?>(null)
    val qrScanMessage: StateFlow<String?> = _qrScanMessage.asStateFlow()

    private val _clockOutSuccess = MutableStateFlow(false)
    val clockOutSuccess: StateFlow<Boolean> = _clockOutSuccess.asStateFlow()

    // --- Search & Category States ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    // --- Product States ---
    private val _products = MutableStateFlow<List<ProductResponse>>(emptyList())
    val products: StateFlow<List<ProductResponse>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // --- Cart States ---
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    val totalPrice: StateFlow<Double> = _cartItems.map { items ->
        items.sumOf { it.subtotal }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val cartTotal: StateFlow<Double> = totalPrice

    val totalCount: StateFlow<Int> = _cartItems.map { items ->
        items.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // --- Checkout States ---
    private val _isCheckoutLoading = MutableStateFlow(false)
    val isCheckoutLoading: StateFlow<Boolean> = _isCheckoutLoading.asStateFlow()

    private val _checkoutSuccess = MutableStateFlow(false)
    val checkoutSuccess: StateFlow<Boolean> = _checkoutSuccess.asStateFlow()

    init {
        fetchProducts()
        checkCurrentShift()
        fetchHistory()
        tokenManager?.let { tm ->
            viewModelScope.launch {
                tm.startingCashFlow.collect { cash ->
                    if (cash > 0) {
                        _startingCash.value = cash
                    }
                }
            }
            viewModelScope.launch {
                tm.sharedOrdersFlow.collect {
                    fetchHistory()
                }
            }
        }
    }

    fun checkCurrentShift() {
        viewModelScope.launch {
            try {
                val response = apiService.getCurrentShift()
                if (response.isSuccessful && response.body()?.data != null) {
                    val shift = response.body()?.data
                    _currentShiftData.value = shift
                    _isShiftOpen.value = shift?.isShiftActive == true
                    if (shift?.startingCash != null && shift.startingCash > 0) {
                        _startingCash.value = shift.startingCash
                        tokenManager?.saveStartingCash(shift.startingCash)
                    }
                } else {
                    _currentShiftData.value = null
                    _isShiftOpen.value = false
                }
            } catch (_: Exception) {
                _currentShiftData.value = null
                _isShiftOpen.value = false
            }
        }
    }

    fun fetchCurrentShift() {
        checkCurrentShift()
    }

    fun clockIn(startingCash: Double) {
        _startingCash.value = startingCash
        viewModelScope.launch {
            tokenManager?.saveStartingCash(startingCash)
            _isClockInLoading.value = true
            _errorMessage.value = null
            try {
                val response = apiService.clockIn(ClockInRequest(startingCash))
                if (response.isSuccessful) {
                    _isShiftOpen.value = true
                    fetchCurrentShift()
                } else {
                    _isShiftOpen.value = true
                }
            } catch (e: Exception) {
                _isShiftOpen.value = true
            } finally {
                _isClockInLoading.value = false
            }
        }
    }

    fun clockOut() {
        viewModelScope.launch {
            _isClockOutLoading.value = true
            _errorMessage.value = null
            try {
                apiService.clockOut()
                tokenManager?.clearStartingCash()
                _isShiftOpen.value = false
                _currentShiftData.value = null
                _startingCash.value = 0.0
                _clockOutSuccess.value = true
            } catch (e: Exception) {
                tokenManager?.clearStartingCash()
                _isShiftOpen.value = false
                _currentShiftData.value = null
                _startingCash.value = 0.0
                _clockOutSuccess.value = true
            } finally {
                _isClockOutLoading.value = false
            }
        }
    }

    fun fetchHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val serverOrders = try {
                    val response = apiService.getOrders()
                    if (response.isSuccessful && response.body()?.data != null) {
                        response.body()?.data ?: emptyList()
                    } else emptyList()
                } catch (_: Exception) {
                    emptyList()
                }

                val sharedOrders = tokenManager?.getSharedOrdersSync() ?: emptyList()

                // Gabungkan pesanan shared dan pesanan server (prioritaskan status shared terbaru)
                val merged = (sharedOrders + serverOrders).distinctBy { it.id ?: it.qrCode }
                _orderHistory.value = merged
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Gagal memuat riwayat"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectOrderForDetail(order: OrderData) {
        _selectedOrderDetail.value = order
        if (order.id != null && (order.items == null || order.items.isEmpty())) {
            viewModelScope.launch {
                _isDetailLoading.value = true
                try {
                    val response = apiService.getOrderById(order.id)
                    if (response.isSuccessful && response.body()?.data != null) {
                        _selectedOrderDetail.value = response.body()?.data
                    }
                } catch (_: Exception) {
                } finally {
                    _isDetailLoading.value = false
                }
            }
        }
    }

    fun clearOrderDetail() {
        _selectedOrderDetail.value = null
    }

    // --- KEAMANAN & VERIFIKASI PENGAMBILAN PRE-ORDER (ANTI-FRAUD / ANTI-JAHIL) ---
    fun verifyAndCompletePreOrder(orderIdOrQr: String) {
        val cleanQuery = orderIdOrQr.trim()
        if (cleanQuery.isBlank()) {
            _qrScanMessage.value = "Mohon masukkan nomor pesanan atau scan QR Code."
            return
        }

        viewModelScope.launch {
            _isDetailLoading.value = true
            _qrScanMessage.value = null

            try {
                val currentKasir = tokenManager?.getKasirNameSync() ?: "Petugas Kasir 1"
                val activeShiftId = _currentShiftData.value?.id ?: "SHIFT-ACTIVE"

                // 1. Kirim verifikasi ke endpoint backend PUT /api/v1/pos/scan/:qr_code
                var backendMsg: String? = null
                var isBackendSuccess = false
                try {
                    val response = apiService.scanQrCode(cleanQuery)
                    if (response.isSuccessful) {
                        isBackendSuccess = true
                        backendMsg = response.body()?.message ?: "Pesanan pre-order berhasil diverifikasi dan diserahkan!"
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (!errorBody.isNullOrBlank()) {
                            val json = org.json.JSONObject(errorBody)
                            backendMsg = if (json.has("message")) json.getString("message") else null
                        }
                    }
                } catch (_: Exception) {
                    // Jika offline/jaringan terkendala, lanjutkan pengecekan lokal
                }

                // 2. Eksekusi verifikasi anti-fraud di sistem penyimpanan bersama dengan menyematkan shiftId aktif
                val (success, localMessage) = tokenManager?.completeSharedOrder(cleanQuery, currentKasir, activeShiftId)
                    ?: Pair(false, "Sistem penyimpanan pesanan tidak dapat diakses.")

                _qrScanMessage.value = backendMsg ?: localMessage
                fetchHistory()
                fetchProducts() // Update ketersediaan stok
            } catch (e: Exception) {
                _qrScanMessage.value = "Terjadi kendala saat memproses pesanan: ${e.localizedMessage}"
            } finally {
                _isDetailLoading.value = false
            }
        }
    }

    fun processQrScan(qrCode: String) {
        verifyAndCompletePreOrder(qrCode)
    }

    // --- UPDATE PROFIL KASIR SENDIRI ---
    fun updateKasirProfile(name: String, nip: String, newPassword: String? = null) {
        val cleanName = name.trim()
        val cleanNip = nip.trim()
        val cleanPw = newPassword?.trim()

        if (cleanName.isBlank()) {
            _errorMessage.value = "Nama kasir tidak boleh kosong."
            return
        }
        if (cleanNip.isBlank()) {
            _errorMessage.value = "NIP kasir tidak boleh kosong."
            return
        }
        if (!cleanPw.isNullOrBlank() && cleanPw.length < 6) {
            _errorMessage.value = "Kata sandi baru minimal 6 karakter."
            return
        }

        viewModelScope.launch {
            _isUpdatingKasirProfile.value = true
            _errorMessage.value = null
            _updateProfileSuccess.value = null

            try {
                val currentId = tokenManager?.getUserIdSync()
                val updatePayload = com.smkn8jkt.sipeka.data.model.UserData(
                    id = currentId,
                    name = cleanName,
                    nisnNip = cleanNip,
                    password = if (!cleanPw.isNullOrBlank()) cleanPw else null,
                    role = "kasir"
                )

                var serverMessage: String? = null

                try {
                    val response = apiService.updateProfile(updatePayload)
                    if (response.isSuccessful) {
                        serverMessage = response.body()?.message
                        val serverUser = response.body()?.data
                        tokenManager?.saveKasirProfile(serverUser?.name ?: cleanName, serverUser?.nisnNip ?: cleanNip)
                        tokenManager?.saveUserProfile(
                            id = serverUser?.id ?: currentId,
                            name = serverUser?.name ?: cleanName,
                            nisn = serverUser?.nisnNip ?: cleanNip
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
                    if (!currentId.isNullOrBlank()) {
                        try {
                            val altResp = apiService.updateUser(currentId, updatePayload)
                            if (altResp.isSuccessful) serverMessage = altResp.body()?.message
                        } catch (_: Exception) {}
                    }
                }

                tokenManager?.saveKasirProfile(cleanName, cleanNip)
                tokenManager?.saveUserProfile(id = currentId, name = cleanName, nisn = cleanNip)

                _updateProfileSuccess.value = serverMessage ?: "Informasi akun kasir berhasil diperbarui!"
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memperbarui profil: ${e.localizedMessage}"
            } finally {
                _isUpdatingKasirProfile.value = false
            }
        }
    }

    fun clearUpdateSuccess() {
        _updateProfileSuccess.value = null
    }

    fun clearQrScanMessage() {
        _qrScanMessage.value = null
    }

    fun resetClockOutState() {
        _clockOutSuccess.value = false
    }

    fun fetchProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = apiService.getProducts()
                if (response.isSuccessful && response.body()?.data != null) {
                    _products.value = response.body()?.data ?: emptyList()
                } else {
                    _products.value = emptyList()
                }
            } catch (e: Exception) {
                _products.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToCart(product: ProductResponse) {
        _cartItems.value = _cartItems.value.toMutableList().apply {
            val index = indexOfFirst { it.product.id == product.id }
            if (index >= 0) {
                val existing = this[index]
                this[index] = existing.copy(quantity = existing.quantity + 1)
            } else {
                add(CartItem(product = product, quantity = 1))
            }
        }
    }

    fun increaseQuantity(productId: String) {
        _cartItems.value = _cartItems.value.toMutableList().apply {
            val index = indexOfFirst { it.product.id == productId }
            if (index >= 0) {
                val existing = this[index]
                this[index] = existing.copy(quantity = existing.quantity + 1)
            }
        }
    }

    fun decreaseQuantity(productId: String) {
        _cartItems.value = _cartItems.value.toMutableList().apply {
            val index = indexOfFirst { it.product.id == productId }
            if (index >= 0) {
                val existing = this[index]
                if (existing.quantity > 1) {
                    this[index] = existing.copy(quantity = existing.quantity - 1)
                } else {
                    removeAt(index)
                }
            }
        }
    }

    fun removeFromCart(productId: String) {
        _cartItems.value = _cartItems.value.filter { it.product.id != productId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun checkout() {
        if (_cartItems.value.isEmpty()) {
            _errorMessage.value = "Keranjang belanja Anda masih kosong."
            return
        }

        viewModelScope.launch {
            _isCheckoutLoading.value = true
            _errorMessage.value = null
            _checkoutSuccess.value = false

            try {
                val orderItems = _cartItems.value.map { item ->
                    OrderItemRequest(
                        productId = item.product.id,
                        quantity = item.quantity
                    )
                }
                val request = OrderRequest(items = orderItems)

                val currentItems = _cartItems.value.toList()
                val totalCart = currentItems.sumOf { it.subtotal }
                val currentKasir = tokenManager?.getKasirNameSync() ?: "Petugas Kasir 1"
                val activeShiftId = _currentShiftData.value?.id ?: "SHIFT-ACTIVE"
                val nowIso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(Date())
                val generatedOrderId = UUID.randomUUID().toString()

                val cashOrder = OrderData(
                    id = generatedOrderId,
                    orderType = "KASIR-TUNAI",
                    status = "COMPLETED",
                    totalAmount = totalCart,
                    createdAt = nowIso,
                    completedAt = nowIso,
                    shiftId = activeShiftId,
                    kasirName = currentKasir,
                    customerName = "Pelanggan Kasir Tunai",
                    items = currentItems.map {
                        OrderItemData(
                            id = UUID.randomUUID().toString(),
                            productId = it.product.id,
                            productName = it.product.name,
                            price = it.product.price,
                            quantity = it.quantity
                        )
                    }
                )

                val response = apiService.createTransaction(request)
                if (response.isSuccessful) {
                    // Simpan transaksi kasir langsung ke shared orders agar seketika masuk riwayat kasir & rekap shift
                    tokenManager?.saveSharedOrder(cashOrder)
                    _cartItems.value = emptyList()
                    _checkoutSuccess.value = true
                    fetchHistory()
                    fetchProducts()
                } else {
                    val errorMsg = try {
                        val errorBody = response.errorBody()?.string()
                        if (!errorBody.isNullOrBlank()) {
                            org.json.JSONObject(errorBody).optString("message", "Gagal memproses transaksi kasir.")
                        } else {
                            "Gagal memproses transaksi kasir (HTTP ${response.code()})."
                        }
                    } catch (_: Exception) {
                        "Gagal memproses transaksi kasir (HTTP ${response.code()})."
                    }
                    _errorMessage.value = errorMsg
                }
            } catch (e: Exception) {
                _errorMessage.value = "Koneksi bermasalah: ${e.localizedMessage ?: "Terjadi kesalahan memproses pesanan."}"
            } finally {
                _isCheckoutLoading.value = false
            }
        }
    }

    fun resetCheckoutState() {
        _checkoutSuccess.value = false
        _errorMessage.value = null
    }
}

class PosViewModelFactory(
    private val tokenManager: TokenManager? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PosViewModel(tokenManager = tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
