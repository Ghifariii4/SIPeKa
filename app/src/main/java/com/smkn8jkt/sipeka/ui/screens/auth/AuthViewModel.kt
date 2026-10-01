package com.smkn8jkt.sipeka.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.smkn8jkt.sipeka.data.model.BaseResponse
import com.smkn8jkt.sipeka.data.model.LoginRequest
import com.smkn8jkt.sipeka.data.model.RegisterRequest
import com.smkn8jkt.sipeka.data.remote.ApiClient
import com.smkn8jkt.sipeka.data.remote.ApiService
import com.smkn8jkt.sipeka.data.remote.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val apiService: ApiService = ApiClient.apiService,
    private val tokenManager: TokenManager? = null
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess.asStateFlow()

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole.asStateFlow()

    fun login(nisnNip: String, password: String) {
        val cleanNisnNip = nisnNip.trim()
        val cleanPassword = password.trim()

        if (cleanNisnNip.isBlank()) {
            _errorMessage.value = "Mohon masukkan nomor NISN / NIP Anda."
            return
        }
        if (cleanNisnNip.length < 5) {
            _errorMessage.value = "NISN / NIP harus terdiri dari minimal 5 digit angka."
            return
        }
        if (cleanPassword.isBlank()) {
            _errorMessage.value = "Mohon masukkan kata sandi Anda."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _loginSuccess.value = false

            try {
                val request = LoginRequest(nisnNip = cleanNisnNip, password = cleanPassword)
                val response = apiService.login(request)

                if (response.isSuccessful) {
                    val body = response.body()
                    val token = body?.data?.token
                    val role = body?.data?.role ?: body?.data?.user?.role ?: "admin"
                    val userData = body?.data?.user

                    // Pendaftaran Penitip/Penjual/Siswa Harus Menunggu ACC Admin
                    val isPendingApproval = (role.equals("penitip", ignoreCase = true) ||
                            role.equals("penjual", ignoreCase = true) ||
                            role.equals("pembeli", ignoreCase = true) ||
                            role.equals("siswa", ignoreCase = true)) &&
                            (userData?.isApproved == false ||
                                    (userData?.status ?: "").equals("pending", ignoreCase = true))

                    if (isPendingApproval) {
                        _errorMessage.value = "Pendaftaran akun Anda (${userData?.name ?: "User"}) sedang dalam antrean. Mohon tunggu persetujuan (ACC) dari Admin / Guru Pembina."
                        _loginSuccess.value = false
                        return@launch
                    }

                    _userRole.value = role

                    if (!token.isNullOrBlank() && tokenManager != null) {
                        tokenManager.saveAuthData(
                            token = token,
                            role = role,
                            userId = userData?.id,
                            userName = userData?.name,
                            userNisn = userData?.nisnNip ?: cleanNisnNip
                        )
                    }
                    _loginSuccess.value = true
                } else {
                    val errorString = response.errorBody()?.string()
                    val parsedError = if (!errorString.isNullOrBlank()) {
                        try {
                            val gson = Gson()
                            val baseError = gson.fromJson(errorString, BaseResponse::class.java)
                            formatFriendlyErrorMessage(baseError.message ?: "Kombinasi NISN/NIP atau kata sandi tidak cocok.")
                        } catch (e: Exception) {
                            "Autentikasi gagal. Silakan periksa kembali NISN/NIP dan kata sandi Anda."
                        }
                    } else {
                        "Gagal masuk ke akun. Silakan coba beberapa saat lagi."
                    }
                    _errorMessage.value = parsedError
                }
            } catch (e: Exception) {
                _errorMessage.value = "Koneksi terputus. Pastikan perangkat terhubung ke internet."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(
        nisnNip: String,
        name: String,
        password: String,
        confirmPassword: String,
        role: String
    ) {
        val cleanNisnNip = nisnNip.trim()
        val cleanName = name.trim()
        val cleanPassword = password.trim()
        val cleanConfirmPassword = confirmPassword.trim()
        val cleanRole = role.trim()

        if (cleanNisnNip.isBlank()) {
            _errorMessage.value = "Mohon isi nomor NISN / NIP terlebih dahulu."
            return
        }
        if (cleanNisnNip.length < 5) {
            _errorMessage.value = "Nomor NISN / NIP harus berisi minimal 5 digit angka."
            return
        }
        if (cleanName.isBlank()) {
            _errorMessage.value = "Mohon isi nama lengkap Anda."
            return
        }
        if (cleanRole.isBlank()) {
            _errorMessage.value = "Pilih peran (Role) terlebih dahulu."
            return
        }
        if (cleanPassword.isBlank()) {
            _errorMessage.value = "Mohon buat kata sandi untuk akun Anda."
            return
        }
        if (cleanPassword.length < 6) {
            _errorMessage.value = "Kata sandi harus terdiri dari minimal 6 karakter."
            return
        }
        val hasLetter = cleanPassword.any { it.isLetter() }
        val hasDigit = cleanPassword.any { it.isDigit() }
        if (!hasLetter || !hasDigit) {
            _errorMessage.value = "Kata sandi harus mengandung kombinasi huruf dan angka."
            return
        }
        if (cleanConfirmPassword.isBlank()) {
            _errorMessage.value = "Mohon isi konfirmasi kata sandi Anda."
            return
        }
        if (cleanPassword != cleanConfirmPassword) {
            _errorMessage.value = "Konfirmasi kata sandi tidak cocok. Mohon periksa kembali."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _registerSuccess.value = false

            try {
                val request = RegisterRequest(
                    nisnNip = cleanNisnNip,
                    name = cleanName,
                    password = cleanPassword,
                    role = cleanRole
                )
                val response = apiService.register(request)

                if (response.isSuccessful) {
                    _registerSuccess.value = true
                } else {
                    val errorString = response.errorBody()?.string()
                    val parsedError = if (!errorString.isNullOrBlank()) {
                        try {
                            val gson = Gson()
                            val baseError = gson.fromJson(errorString, BaseResponse::class.java)
                            formatFriendlyErrorMessage(baseError.message ?: "Gagal mendaftarkan akun. Periksa data Anda.")
                        } catch (e: Exception) {
                            "Pendaftaran gagal. Silakan periksa kembali kelengkapan formulir Anda."
                        }
                    } else {
                        "Terjadi kendala saat pendaftaran. Silakan coba beberapa saat lagi."
                    }
                    _errorMessage.value = parsedError
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal terhubung ke server. Periksa koneksi internet Anda."
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun formatFriendlyErrorMessage(rawMessage: String): String {
        return when {
            rawMessage.contains("nisn_nip", ignoreCase = true) || rawMessage.contains("nisn", ignoreCase = true) ->
                "Mohon pastikan nomor NISN / NIP diisi dengan benar."
            rawMessage.contains("password", ignoreCase = true) ->
                "Kata sandi tidak memenuhi kriteria. Gunakan minimal 6 karakter dan kombinasi huruf serta angka."
            rawMessage.contains("role", ignoreCase = true) ->
                "Pilih salah satu peran (Role) yang tersedia."
            rawMessage.contains("exist", ignoreCase = true) || rawMessage.contains("sudah", ignoreCase = true) || rawMessage.contains("registered", ignoreCase = true) ->
                "Nomor NISN / NIP ini sudah terdaftar. Silakan lakukan Login atau gunakan nomor lain."
            else -> rawMessage
        }
    }

    fun resetError() {
        _errorMessage.value = null
    }

    fun resetState() {
        _errorMessage.value = null
        _loginSuccess.value = false
        _registerSuccess.value = false
        _isLoading.value = false
        _userRole.value = null
    }
}

class AuthViewModelFactory(
    private val tokenManager: TokenManager? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(tokenManager = tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
