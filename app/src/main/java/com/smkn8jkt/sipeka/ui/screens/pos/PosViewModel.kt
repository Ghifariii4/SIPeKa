package com.smkn8jkt.sipeka.ui.screens.pos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.smkn8jkt.sipeka.data.model.BaseResponse
import com.smkn8jkt.sipeka.data.model.CartItem
import com.smkn8jkt.sipeka.data.model.ClockInRequest
import com.smkn8jkt.sipeka.data.model.OrderData
import com.smkn8jkt.sipeka.data.model.OrderItemRequest
import com.smkn8jkt.sipeka.data.model.OrderRequest
import com.smkn8jkt.sipeka.data.model.ProductResponse
import com.smkn8jkt.sipeka.data.model.ShiftData
import com.smkn8jkt.sipeka.data.remote.ApiClient
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

    // --- History & QR States ---
    private val _orderHistory = MutableStateFlow<List<OrderData>>(emptyList())
    val orderHistory: StateFlow<List<OrderData>> = _orderHistory.asStateFlow()

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

    private val _selectedCategory = MutableStateFlow("Semua Menu")
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
        tokenManager?.let { tm ->
            viewModelScope.launch {
                tm.startingCashFlow.collect { cash ->
                    if (cash > 0) {
                        _startingCash.value = cash
                    }
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
                val response = apiService.getOrders()
                if (response.isSuccessful && response.body()?.data != null) {
                    _orderHistory.value = response.body()?.data ?: emptyList()
                } else {
                    _orderHistory.value = emptyList()
                }
            } catch (e: Exception) {
                _orderHistory.value = emptyList()
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

    fun processQrScan(qrCode: String) {
        if (qrCode.isBlank()) return
        viewModelScope.launch {
            try {
                val response = apiService.scanQrCode(qrCode)
                if (response.isSuccessful) {
                    val msg = response.body()?.data?.message ?: "Pesanan Pre-order ($qrCode) berhasil diproses"
                    _qrScanMessage.value = msg
                } else {
                    val errorString = response.errorBody()?.string()
                    val parsedError = if (!errorString.isNullOrBlank()) {
                        try {
                            val gson = Gson()
                            val baseError = gson.fromJson(errorString, BaseResponse::class.java)
                            baseError.message ?: "Gagal memproses QR."
                        } catch (e: Exception) {
                            "Gagal memproses QR (${response.code()})."
                        }
                    } else {
                        "Gagal memproses QR."
                    }
                    _qrScanMessage.value = parsedError
                }
            } catch (e: Exception) {
                _qrScanMessage.value = e.localizedMessage ?: "Terjadi kesalahan koneksi"
            }
        }
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

                val response = apiService.createTransaction(request)
                if (response.isSuccessful) {
                    _cartItems.value = emptyList()
                    _checkoutSuccess.value = true
                } else {
                    val errorString = response.errorBody()?.string()
                    val parsedError = if (!errorString.isNullOrBlank()) {
                        try {
                            val gson = Gson()
                            val baseError = gson.fromJson(errorString, BaseResponse::class.java)
                            baseError.message ?: "Gagal memproses pesanan."
                        } catch (e: Exception) {
                            "Transaksi gagal diproses (${response.code()})."
                        }
                    } else {
                        "Transaksi gagal diproses oleh server."
                    }
                    _errorMessage.value = parsedError
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Terjadi kesalahan koneksi"
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
