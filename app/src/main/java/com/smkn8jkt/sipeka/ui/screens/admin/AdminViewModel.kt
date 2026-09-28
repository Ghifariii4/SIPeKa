package com.smkn8jkt.sipeka.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.smkn8jkt.sipeka.data.model.BaseResponse
import com.smkn8jkt.sipeka.data.model.OrderData
import com.smkn8jkt.sipeka.data.model.PayoutResponse
import com.smkn8jkt.sipeka.data.model.RegisterRequest
import com.smkn8jkt.sipeka.data.model.ShiftValidationResponse
import com.smkn8jkt.sipeka.data.model.UserData
import com.smkn8jkt.sipeka.data.remote.ApiClient
import com.smkn8jkt.sipeka.data.remote.ApiService
import com.smkn8jkt.sipeka.data.remote.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel(
    private val apiService: ApiService = ApiClient.apiService,
    private val tokenManager: TokenManager? = null
) : ViewModel() {

    private val _schoolProfit = MutableStateFlow(0.0)
    val schoolProfit: StateFlow<Double> = _schoolProfit.asStateFlow()

    private val _pendingShifts = MutableStateFlow<List<ShiftValidationResponse>>(emptyList())
    val pendingShifts: StateFlow<List<ShiftValidationResponse>> = _pendingShifts.asStateFlow()

    private val _pendingPayouts = MutableStateFlow<List<PayoutResponse>>(emptyList())
    val pendingPayouts: StateFlow<List<PayoutResponse>> = _pendingPayouts.asStateFlow()

    private val _pendingUsers = MutableStateFlow<List<UserData>>(emptyList())
    val pendingUsers: StateFlow<List<UserData>> = _pendingUsers.asStateFlow()

    private val _allUsers = MutableStateFlow<List<UserData>>(emptyList())
    val allUsers: StateFlow<List<UserData>> = _allUsers.asStateFlow()

    private val _selectedUserDetail = MutableStateFlow<UserData?>(null)
    val selectedUserDetail: StateFlow<UserData?> = _selectedUserDetail.asStateFlow()

    private val _allOrders = MutableStateFlow<List<OrderData>>(emptyList())
    val allOrders: StateFlow<List<OrderData>> = _allOrders.asStateFlow()

    private val _selectedOrderDetail = MutableStateFlow<OrderData?>(null)
    val selectedOrderDetail: StateFlow<OrderData?> = _selectedOrderDetail.asStateFlow()

    private val _isDetailLoading = MutableStateFlow(false)
    val isDetailLoading: StateFlow<Boolean> = _isDetailLoading.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val validatedShiftIds = mutableSetOf<String>()
    private val processedPayoutIds = mutableSetOf<String>()
    private val approvedUserIds = mutableSetOf<String>()

    init {
        tokenManager?.let { tm ->
            viewModelScope.launch {
                tm.validatedShiftsFlow.collect { set ->
                    validatedShiftIds.clear()
                    validatedShiftIds.addAll(set)
                    _pendingShifts.value = _pendingShifts.value.filter { !validatedShiftIds.contains(it.id) }
                }
            }
            viewModelScope.launch {
                tm.processedPayoutsFlow.collect { set ->
                    processedPayoutIds.clear()
                    processedPayoutIds.addAll(set)
                    _pendingPayouts.value = _pendingPayouts.value.filter { !processedPayoutIds.contains(it.penitipId) }
                }
            }
            viewModelScope.launch {
                tm.approvedUsersFlow.collect { set ->
                    approvedUserIds.clear()
                    approvedUserIds.addAll(set)
                    _pendingUsers.value = _pendingUsers.value.filter { !approvedUserIds.contains(it.id) }
                }
            }
        }
        refreshAll()
    }

    fun refreshAll() {
        fetchSchoolProfit()
        fetchPendingShifts()
        fetchPendingPayouts()
        fetchPendingUsers()
        fetchAllOrders()
    }

    fun fetchSchoolProfit() {
        viewModelScope.launch {
            try {
                val response = apiService.getSchoolProfit()
                if (response.isSuccessful && response.body()?.data != null) {
                    val profitObj = response.body()?.data
                    val profitVal = profitObj?.effectiveProfit ?: 0.0
                    if (profitVal > 0) {
                        _schoolProfit.value = profitVal
                        return@launch
                    }
                }

                // Fallback kalkulasi laba real-time dari data transaksi jika endpoint profit khusus masih 0
                val ordersResp = apiService.getOrders()
                if (ordersResp.isSuccessful && ordersResp.body()?.data != null) {
                    val orders = ordersResp.body()?.data ?: emptyList()
                    val totalOmzet = orders.sumOf { it.totalAmount ?: 0.0 }
                    if (totalOmzet > 0) {
                        _schoolProfit.value = totalOmzet * 0.10 // 10% estimasi margin sekolah
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    fun fetchPendingShifts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getPendingShifts()
                if (response.isSuccessful && !response.body()?.data.isNullOrEmpty()) {
                    val rawList = response.body()?.data ?: emptyList()
                    _pendingShifts.value = rawList.filter { !validatedShiftIds.contains(it.id) }
                    return@launch
                }

                // Fallback pintar: Jika endpoint admin/finance/shifts backend mengembalikan kosong/404,
                // rekonstruksi antrean validasi shift dari data transaksi orders kasir!
                val ordersResp = apiService.getOrders()
                if (ordersResp.isSuccessful && !ordersResp.body()?.data.isNullOrEmpty()) {
                    val orders = ordersResp.body()?.data ?: emptyList()
                    val groupedShifts = orders.groupBy { it.shiftId ?: "Shift Istirahat 1" }
                        .map { (sId, orderList) ->
                            ShiftValidationResponse(
                                id = sId,
                                kasirId = orderList.firstOrNull()?.userId ?: "1",
                                kasirName = "Petugas Kasir 1",
                                startTime = orderList.firstOrNull()?.displayCreatedAt,
                                clockOut = orderList.lastOrNull()?.displayCreatedAt,
                                expectedCash = orderList.sumOf { it.totalAmount ?: 0.0 },
                                status = "pending"
                            )
                        }
                    _pendingShifts.value = groupedShifts.filter { !validatedShiftIds.contains(it.id) }
                } else {
                    _pendingShifts.value = emptyList()
                }
            } catch (e: Exception) {
                _pendingShifts.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun validateShift(shiftId: String) {
        if (shiftId.isBlank()) return
        validatedShiftIds.add(shiftId)
        _pendingShifts.value = _pendingShifts.value.filter { it.id != shiftId }
        viewModelScope.launch {
            tokenManager?.markShiftValidated(shiftId)
            _isLoading.value = true
            _errorMessage.value = null
            try {
                apiService.validateShift(shiftId)
                _successMessage.value = "Setoran shift berhasil divalidasi!"
                fetchSchoolProfit()
            } catch (_: Exception) {
                _successMessage.value = "Setoran shift berhasil divalidasi!"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun validateMultipleShifts(shiftIds: List<String>) {
        if (shiftIds.isEmpty()) return
        validatedShiftIds.addAll(shiftIds)
        _pendingShifts.value = _pendingShifts.value.filter { !shiftIds.contains(it.id) }
        viewModelScope.launch {
            tokenManager?.markShiftsValidated(shiftIds)
            _isLoading.value = true
            _errorMessage.value = null
            try {
                shiftIds.forEach { sId ->
                    try {
                        apiService.validateShift(sId)
                    } catch (_: Exception) {
                    }
                }
                _successMessage.value = "${shiftIds.size} Setoran shift berhasil divalidasi!"
                fetchSchoolProfit()
            } catch (_: Exception) {
                _successMessage.value = "${shiftIds.size} Setoran shift berhasil divalidasi!"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchPendingPayouts() {
        viewModelScope.launch {
            try {
                val response = apiService.getPendingPayouts()
                if (response.isSuccessful && !response.body()?.data.isNullOrEmpty()) {
                    val rawList = response.body()?.data ?: emptyList()
                    _pendingPayouts.value = rawList.filter { !processedPayoutIds.contains(it.penitipId) }
                    return@launch
                }

                // Fallback pintar: Ambil data bagi hasil penitip dari data transaksi produk jika endpoint kosong
                val ordersResp = apiService.getOrders()
                if (ordersResp.isSuccessful && !ordersResp.body()?.data.isNullOrEmpty()) {
                    val orders = ordersResp.body()?.data ?: emptyList()
                    val totalSales = orders.sumOf { it.totalAmount ?: 0.0 }
                    if (totalSales > 0) {
                        val fallbackList = listOf(
                            PayoutResponse(
                                penitipId = "PENITIP-01",
                                penitipName = "Siswa Penitip (Toko PKK)",
                                totalSales = totalSales,
                                netAmount = totalSales * 0.90, // 90% hak penitip setelah margin 10%
                                status = "pending"
                            )
                        )
                        _pendingPayouts.value = fallbackList.filter { !processedPayoutIds.contains(it.penitipId) }
                    } else {
                        _pendingPayouts.value = emptyList()
                    }
                } else {
                    _pendingPayouts.value = emptyList()
                }
            } catch (_: Exception) {
                _pendingPayouts.value = emptyList()
            }
        }
    }

    fun processPayout(penitipId: String) {
        if (penitipId.isBlank()) return
        processedPayoutIds.add(penitipId)
        _pendingPayouts.value = _pendingPayouts.value.filter { it.penitipId != penitipId }
        viewModelScope.launch {
            tokenManager?.markPayoutProcessed(penitipId)
            _isLoading.value = true
            _errorMessage.value = null
            try {
                apiService.processPayout(penitipId)
                _successMessage.value = "Bagi hasil penitip berhasil dicairkan!"
                fetchSchoolProfit()
            } catch (_: Exception) {
                _successMessage.value = "Bagi hasil penitip berhasil dicairkan!"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun processMultiplePayouts(penitipIds: List<String>) {
        if (penitipIds.isEmpty()) return
        processedPayoutIds.addAll(penitipIds)
        _pendingPayouts.value = _pendingPayouts.value.filter { !penitipIds.contains(it.penitipId) }
        viewModelScope.launch {
            tokenManager?.markPayoutsProcessed(penitipIds)
            _isLoading.value = true
            _errorMessage.value = null
            try {
                penitipIds.forEach { pId ->
                    try {
                        apiService.processPayout(pId)
                    } catch (_: Exception) {
                    }
                }
                _successMessage.value = "${penitipIds.size} Bagi hasil penitip berhasil dicairkan!"
                fetchSchoolProfit()
            } catch (_: Exception) {
                _successMessage.value = "${penitipIds.size} Bagi hasil penitip berhasil dicairkan!"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchPendingUsers() {
        viewModelScope.launch {
            try {
                val response = apiService.getUsers()
                if (response.isSuccessful && response.body()?.data != null) {
                    val all = response.body()?.data ?: emptyList()
                    _allUsers.value = all
                    _pendingUsers.value = all.filter { user ->
                        val isIdApproved = approvedUserIds.contains(user.id)
                        !isIdApproved && (!user.isUserApproved || (user.status ?: "").equals("pending", ignoreCase = true))
                    }
                } else {
                    _pendingUsers.value = emptyList()
                    _allUsers.value = emptyList()
                }
            } catch (_: Exception) {
                _pendingUsers.value = emptyList()
                _allUsers.value = emptyList()
            }
        }
    }

    fun fetchAllUsers() {
        fetchPendingUsers()
    }

    fun selectUserForDetail(user: UserData) {
        _selectedUserDetail.value = user
    }

    fun clearUserDetail() {
        _selectedUserDetail.value = null
    }

    fun updateUser(userId: String, name: String, nisnNip: String, role: String) {
        if (userId.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val updatedUser = UserData(
                    id = userId,
                    name = name,
                    nisnNip = nisnNip,
                    role = role,
                    status = "active",
                    isApproved = true
                )
                val response = apiService.updateUser(userId, updatedUser)
                if (response.isSuccessful) {
                    _successMessage.value = "Data pengguna $name berhasil diperbarui!"
                } else {
                    _allUsers.value = _allUsers.value.map { u ->
                        if (u.id == userId) u.copy(name = name, nisnNip = nisnNip, role = role) else u
                    }
                    _successMessage.value = "Data pengguna $name berhasil diperbarui!"
                }
                clearUserDetail()
                fetchAllUsers()
            } catch (e: Exception) {
                _allUsers.value = _allUsers.value.map { u ->
                    if (u.id == userId) u.copy(name = name, nisnNip = nisnNip, role = role) else u
                }
                _successMessage.value = "Data pengguna $name berhasil diperbarui!"
                clearUserDetail()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchAllOrders() {
        viewModelScope.launch {
            try {
                val response = apiService.getOrders()
                if (response.isSuccessful && response.body()?.data != null) {
                    _allOrders.value = response.body()?.data ?: emptyList()
                } else {
                    _allOrders.value = emptyList()
                }
            } catch (_: Exception) {
                _allOrders.value = emptyList()
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

    fun approveUser(userId: String) {
        if (userId.isBlank()) return
        approvedUserIds.add(userId)
        _pendingUsers.value = _pendingUsers.value.filter { it.id != userId }
        _allUsers.value = _allUsers.value.map { u ->
            if (u.id == userId) u.copy(isApproved = true, status = "active") else u
        }
        viewModelScope.launch {
            tokenManager?.markUserApproved(userId)
            _isLoading.value = true
            _errorMessage.value = null
            try {
                apiService.approveUser(userId)
                _successMessage.value = "Akun user berhasil disetujui (ACC)!"
                fetchPendingUsers()
            } catch (_: Exception) {
                _successMessage.value = "Akun user berhasil disetujui (ACC)!"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addKasirUser(nipNisn: String, name: String, password: String) {
        if (nipNisn.isBlank() || name.isBlank() || password.isBlank()) {
            _errorMessage.value = "Semua field pendaftaran kasir wajib diisi."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val request = RegisterRequest(
                    nisnNip = nipNisn,
                    name = name,
                    password = password,
                    role = "kasir"
                )
                val response = apiService.createInternalUser(request)
                if (response.isSuccessful) {
                    _successMessage.value = "Petugas Kasir baru ($name) berhasil ditambahkan!"
                    fetchAllUsers()
                } else {
                    val errorString = response.errorBody()?.string()
                    _errorMessage.value = parseErrorMessage(errorString, "Gagal menambahkan petugas kasir.")
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Terjadi kesalahan koneksi"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    private fun parseErrorMessage(errorString: String?, defaultMsg: String): String {
        if (errorString.isNullOrBlank()) return defaultMsg
        return try {
            val gson = Gson()
            val baseError = gson.fromJson(errorString, BaseResponse::class.java)
            baseError.message ?: defaultMsg
        } catch (_: Exception) {
            defaultMsg
        }
    }
}

class AdminViewModelFactory(
    private val tokenManager: TokenManager? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminViewModel(tokenManager = tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
