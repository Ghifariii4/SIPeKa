package com.smkn8jkt.sipeka

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.smkn8jkt.sipeka.data.model.BaseResponse
import com.smkn8jkt.sipeka.data.model.ClockInRequest
import com.smkn8jkt.sipeka.data.model.LoginRequest
import com.smkn8jkt.sipeka.data.model.OrderItemRequest
import com.smkn8jkt.sipeka.data.model.OrderRequest
import com.smkn8jkt.sipeka.data.model.ProductResponse
import com.smkn8jkt.sipeka.data.model.RegisterRequest
import com.smkn8jkt.sipeka.data.remote.ApiClient
import com.smkn8jkt.sipeka.data.remote.ApiService
import com.smkn8jkt.sipeka.data.remote.TokenManager

class MainViewModel(private val tokenManager: TokenManager) : ViewModel() {

    private val apiService: ApiService
        get() = ApiClient.apiService

    private val _apiLog = MutableStateFlow(
        "Dashboard Pengujian API SIPeKa Siap.\n" +
                "Base URL aktif: http://47.129.118.194/api/v1/\n"
    )
    val apiLog: StateFlow<String> = _apiLog.asStateFlow()

    val currentToken: StateFlow<String?> = tokenManager.tokenFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun clearLog() {
        _apiLog.value = "Log dibersihkan."
    }

    fun clearToken() {
        viewModelScope.launch {
            tokenManager.clearToken()
            _apiLog.value = "Token lokal berhasil dihapus dari DataStore."
        }
    }

    // 1. Auth & Register
    fun register() {
        viewModelScope.launch {
            val endpoint = "POST auth/register"
            logRequest(endpoint, "Mengirim data registrasi...")
            try {
                val dummyRequest = RegisterRequest(
                    nisnNip = "12345678",
                    name = "Siswa Test",
                    password = "password123",
                    role = "siswa"
                )
                val response = apiService.register(dummyRequest)
                _apiLog.value = formatResponse(endpoint, response)
            } catch (e: Exception) {
                _apiLog.value = formatException(endpoint, e)
            }
        }
    }

    fun login() {
        viewModelScope.launch {
            val endpoint = "POST auth/login"
            logRequest(endpoint, "Mengirim credential login...")
            try {
                val dummyRequest = LoginRequest(
                    nisnNip = "12345678",
                    password = "password123"
                )
                val response = apiService.login(dummyRequest)
                val logText = formatResponse(endpoint, response)

                if (response.isSuccessful) {
                    val token = response.body()?.data?.token
                    if (!token.isNullOrBlank()) {
                        tokenManager.saveToken(token)
                        _apiLog.value = "$logText\n\n>>> TOKEN DIPEROLAH & DISIMPAN KE DATASTORE:\n$token"
                    } else {
                        _apiLog.value = "$logText\n\n>>> Login berhasil tetapi token tidak ditemukan dalam response."
                    }
                } else {
                    _apiLog.value = logText
                }
            } catch (e: Exception) {
                _apiLog.value = formatException(endpoint, e)
            }
        }
    }

    // 2. Products
    fun getProducts() {
        viewModelScope.launch {
            val endpoint = "GET products"
            logRequest(endpoint, "Mengambil daftar produk...")
            try {
                val response = apiService.getProducts()
                _apiLog.value = formatResponse(endpoint, response)
            } catch (e: Exception) {
                _apiLog.value = formatException(endpoint, e)
            }
        }
    }

    fun getProductById(id: String = "1") {
        viewModelScope.launch {
            val endpoint = "GET products/$id"
            logRequest(endpoint, "Mengambil detail produk ID: $id...")
            try {
                val response = apiService.getProductById(id)
                _apiLog.value = formatResponse(endpoint, response)
            } catch (e: Exception) {
                _apiLog.value = formatException(endpoint, e)
            }
        }
    }

    // 3. Admin (User Management)
    fun getUsers() {
        viewModelScope.launch {
            val endpoint = "GET admin/users"
            logRequest(endpoint, "Mengambil daftar user (Membutuhkan Token Admin)...")
            try {
                val response = apiService.getUsers()
                _apiLog.value = formatResponse(endpoint, response)
            } catch (e: Exception) {
                _apiLog.value = formatException(endpoint, e)
            }
        }
    }

    fun createUser() {
        viewModelScope.launch {
            val endpoint = "POST admin/users"
            logRequest(endpoint, "Membuat user baru via Admin...")
            try {
                val dummyRequest = RegisterRequest(
                    nisnNip = "87654321",
                    name = "Admin Baru",
                    password = "adminpassword",
                    role = "admin"
                )
                val response = apiService.createUser(dummyRequest)
                _apiLog.value = formatResponse(endpoint, response)
            } catch (e: Exception) {
                _apiLog.value = formatException(endpoint, e)
            }
        }
    }

    fun approveUser(id: String = "1") {
        viewModelScope.launch {
            val endpoint = "PUT admin/users/$id/approve"
            logRequest(endpoint, "Menyetujui user ID: $id...")
            try {
                val response = apiService.approveUser(id)
                _apiLog.value = formatResponse(endpoint, response)
            } catch (e: Exception) {
                _apiLog.value = formatException(endpoint, e)
            }
        }
    }

    // 4. Shifts
    fun clockIn() {
        viewModelScope.launch {
            val endpoint = "POST shifts/clock-in"
            logRequest(endpoint, "Melakukan Clock In Kasir...")
            try {
                val response = apiService.clockIn(ClockInRequest(100000.0))
                _apiLog.value = formatResponse(endpoint, response)
            } catch (e: Exception) {
                _apiLog.value = formatException(endpoint, e)
            }
        }
    }

    fun getCurrentShift() {
        viewModelScope.launch {
            val endpoint = "GET shifts/current"
            logRequest(endpoint, "Mengambil data shift aktif...")
            try {
                val response = apiService.getCurrentShift()
                _apiLog.value = formatResponse(endpoint, response)
            } catch (e: Exception) {
                _apiLog.value = formatException(endpoint, e)
            }
        }
    }

    // 5. POS & Orders
    fun createTransaction() {
        viewModelScope.launch {
            val endpoint = "POST pos/transaction"
            logRequest(endpoint, "Membuat transaksi POS baru...")
            try {
                val dummyRequest = OrderRequest(
                    items = listOf(
                        OrderItemRequest(productId = "1", quantity = 2),
                        OrderItemRequest(productId = "2", quantity = 1)
                    )
                )
                val response = apiService.createTransaction(dummyRequest)
                _apiLog.value = formatResponse(endpoint, response)
            } catch (e: Exception) {
                _apiLog.value = formatException(endpoint, e)
            }
        }
    }

    fun scanQrCode(qrCode: String = "QR-TEST-12345") {
        viewModelScope.launch {
            val endpoint = "PUT pos/scan/$qrCode"
            logRequest(endpoint, "Scanning QR Code: $qrCode...")
            try {
                val response = apiService.scanQrCode(qrCode)
                _apiLog.value = formatResponse(endpoint, response)
            } catch (e: Exception) {
                _apiLog.value = formatException(endpoint, e)
            }
        }
    }

    fun getOrders() {
        viewModelScope.launch {
            val endpoint = "GET orders"
            logRequest(endpoint, "Mengambil daftar order/transaksi...")
            try {
                val response = apiService.getOrders()
                _apiLog.value = formatResponse(endpoint, response)
            } catch (e: Exception) {
                _apiLog.value = formatException(endpoint, e)
            }
        }
    }

    fun getOrderById(id: String = "1") {
        viewModelScope.launch {
            val endpoint = "GET orders/$id"
            logRequest(endpoint, "Mengambil detail order ID: $id...")
            try {
                val response = apiService.getOrderById(id)
                _apiLog.value = formatResponse(endpoint, response)
            } catch (e: Exception) {
                _apiLog.value = formatException(endpoint, e)
            }
        }
    }

    private fun logRequest(endpoint: String, message: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        _apiLog.value = "[$time] Sending Request -> $endpoint\n$message\nMohon tunggu..."
    }

    private fun <T> formatResponse(endpoint: String, response: Response<T>): String {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val builder = StringBuilder()
        builder.append("[$time] === ENDPOINT: $endpoint ===\n")
        builder.append("HTTP Status: ${response.code()} ${response.message()}\n\n")

        if (response.isSuccessful) {
            val body = response.body()
            builder.append("RESPONSE BODY:\n")
            if (body != null) {
                builder.append(gson.toJson(body))
            } else {
                builder.append("{\n  \"message\": \"Body null (204 No Content / Empty Body)\"\n}")
            }
        } else {
            builder.append("ERROR RESPONSE:\n")
            val errorString = response.errorBody()?.string()
            if (!errorString.isNullOrBlank()) {
                try {
                    val jsonElement = JsonParser.parseString(errorString)
                    builder.append(gson.toJson(jsonElement))
                } catch (e: Exception) {
                    builder.append(errorString)
                }
            } else {
                builder.append("{\n  \"error\": \"Empty or null error body\"\n}")
            }
        }
        return builder.toString()
    }

    private fun formatException(endpoint: String, throwable: Throwable): String {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        return "[$time] === ENDPOINT: $endpoint ===\n" +
                "ERROR TYPE: ${throwable.javaClass.simpleName}\n" +
                "MESSAGE: ${throwable.localizedMessage ?: "Gagal terhubung ke server."}\n"
    }
}

class MainViewModelFactory(private val tokenManager: TokenManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
