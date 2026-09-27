package com.smkn8jkt.sipeka.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.smkn8jkt.sipeka.data.model.BaseResponse
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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        refreshAll()
    }

    fun refreshAll() {
        fetchSchoolProfit()
        fetchPendingShifts()
        fetchPendingPayouts()
        fetchPendingUsers()
    }

    fun fetchSchoolProfit() {
        viewModelScope.launch {
            try {
                val response = apiService.getSchoolProfit()
                if (response.isSuccessful && response.body()?.data != null) {
                    val profitObj = response.body()?.data
                    _schoolProfit.value = profitObj?.effectiveProfit ?: 0.0
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
                if (response.isSuccessful && response.body()?.data != null) {
                    _pendingShifts.value = response.body()?.data ?: emptyList()
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
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = apiService.validateShift(shiftId)
                if (response.isSuccessful) {
                    _successMessage.value = "Setoran shift berhasil divalidasi!"
                    fetchPendingShifts()
                    fetchSchoolProfit()
                } else {
                    val errorString = response.errorBody()?.string()
                    _errorMessage.value = parseErrorMessage(errorString, "Gagal memvalidasi setoran shift.")
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Terjadi kesalahan koneksi"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchPendingPayouts() {
        viewModelScope.launch {
            try {
                val response = apiService.getPendingPayouts()
                if (response.isSuccessful && response.body()?.data != null) {
                    _pendingPayouts.value = response.body()?.data ?: emptyList()
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
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = apiService.processPayout(penitipId)
                if (response.isSuccessful) {
                    _successMessage.value = "Bagi hasil penitip berhasil dicairkan!"
                    fetchPendingPayouts()
                    fetchSchoolProfit()
                } else {
                    val errorString = response.errorBody()?.string()
                    _errorMessage.value = parseErrorMessage(errorString, "Gagal memproses pencairan dana.")
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Terjadi kesalahan koneksi"
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
                    val allUsers = response.body()?.data ?: emptyList()
                    _pendingUsers.value = allUsers.filter { user ->
                        user.isApproved == false ||
                                (user.status ?: "").equals("pending", ignoreCase = true)
                    }
                } else {
                    _pendingUsers.value = emptyList()
                }
            } catch (_: Exception) {
                _pendingUsers.value = emptyList()
            }
        }
    }

    fun approveUser(userId: String) {
        if (userId.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = apiService.approveUser(userId)
                if (response.isSuccessful) {
                    _successMessage.value = "Akun user berhasil disetujui (ACC)!"
                    fetchPendingUsers()
                } else {
                    val errorString = response.errorBody()?.string()
                    _errorMessage.value = parseErrorMessage(errorString, "Gagal menyetujui akun user.")
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Terjadi kesalahan koneksi"
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
