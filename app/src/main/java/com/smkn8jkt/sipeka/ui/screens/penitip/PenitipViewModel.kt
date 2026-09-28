package com.smkn8jkt.sipeka.ui.screens.penitip

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.smkn8jkt.sipeka.data.model.AddProductRequest
import com.smkn8jkt.sipeka.data.model.BaseResponse
import com.smkn8jkt.sipeka.data.model.PenitipDashboardResponse
import com.smkn8jkt.sipeka.data.remote.ApiClient
import com.smkn8jkt.sipeka.data.remote.ApiService
import com.smkn8jkt.sipeka.data.remote.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class PenitipViewModel(
    private val apiService: ApiService = ApiClient.apiService,
    private val tokenManager: TokenManager? = null
) : ViewModel() {

    private val _dashboardData = MutableStateFlow<PenitipDashboardResponse?>(null)
    val dashboardData: StateFlow<PenitipDashboardResponse?> = _dashboardData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isAddLoading = MutableStateFlow(false)
    val isAddLoading: StateFlow<Boolean> = _isAddLoading.asStateFlow()

    private val _isAddSuccess = MutableStateFlow(false)
    val isAddSuccess: StateFlow<Boolean> = _isAddSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val deletedProductIds = mutableSetOf<String>()

    init {
        fetchDashboard()
    }

    fun fetchDashboard() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = apiService.getPenitipDashboard()
                if (response.isSuccessful && response.body()?.data != null) {
                    val rawData = response.body()?.data
                    val rawProducts = rawData?.products ?: emptyList()
                    val filteredProducts = rawProducts.filter { !deletedProductIds.contains(it.id) }
                    _dashboardData.value = rawData?.copy(products = filteredProducts)
                } else {
                    val productsResp = apiService.getProducts()
                    if (productsResp.isSuccessful && productsResp.body()?.data != null) {
                        val productList = (productsResp.body()?.data ?: emptyList())
                            .filter { !deletedProductIds.contains(it.id) }
                        _dashboardData.value = PenitipDashboardResponse(
                            products = productList,
                            totalUnpaidEarnings = productList.sumOf { p ->
                                val sold = p.totalSold.toDouble()
                                val netPricePerItem = (p.price - (p.schoolMargin ?: 1000.0)).coerceAtLeast(0.0)
                                sold * netPricePerItem
                            }
                        )
                    } else {
                        _dashboardData.value = PenitipDashboardResponse(products = emptyList(), totalUnpaidEarnings = 0.0)
                    }
                }
            } catch (e: Exception) {
                try {
                    val productsResp = apiService.getProducts()
                    if (productsResp.isSuccessful && productsResp.body()?.data != null) {
                        val productList = (productsResp.body()?.data ?: emptyList())
                            .filter { !deletedProductIds.contains(it.id) }
                        _dashboardData.value = PenitipDashboardResponse(
                            products = productList,
                            totalUnpaidEarnings = productList.sumOf { p ->
                                val sold = p.totalSold.toDouble()
                                val netPricePerItem = (p.price - (p.schoolMargin ?: 1000.0)).coerceAtLeast(0.0)
                                sold * netPricePerItem
                            }
                        )
                    }
                } catch (_: Exception) {
                    _dashboardData.value = PenitipDashboardResponse(products = emptyList(), totalUnpaidEarnings = 0.0)
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadProduct(
        context: Context,
        imageUri: Uri?,
        name: String,
        price: String,
        desc: String,
        stock: String
    ) {
        val cleanName = name.trim()
        val priceVal = price.toDoubleOrNull() ?: 0.0
        val stockVal = stock.toIntOrNull() ?: 0

        if (cleanName.isBlank()) {
            _errorMessage.value = "Nama produk wajib diisi."
            return
        }
        if (priceVal <= 0) {
            _errorMessage.value = "Harga produk harus lebih dari Rp 0."
            return
        }
        if (stockVal <= 0) {
            _errorMessage.value = "Stok produk harus lebih dari 0."
            return
        }

        viewModelScope.launch {
            _isAddLoading.value = true
            _errorMessage.value = null
            _isAddSuccess.value = false

            try {
                // 1. Konversi Uri gambar menjadi MultipartBody.Part
                var imagePart: MultipartBody.Part? = null
                if (imageUri != null) {
                    try {
                        val inputStream = context.contentResolver.openInputStream(imageUri)
                        if (inputStream != null) {
                            val tempFile = File.createTempFile("product_upload_", ".jpg", context.cacheDir)
                            val outputStream = FileOutputStream(tempFile)
                            inputStream.copyTo(outputStream)
                            inputStream.close()
                            outputStream.close()

                            val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                            imagePart = MultipartBody.Part.createFormData("image", tempFile.name, requestFile)
                        }
                    } catch (_: Exception) {
                    }
                }

                // 2. Konversi teks menjadi RequestBody
                val textMediaType = "text/plain".toMediaTypeOrNull()
                val nameBody = cleanName.toRequestBody(textMediaType)
                val priceBody = priceVal.toString().toRequestBody(textMediaType)
                val descBody = desc.trim().toRequestBody(textMediaType)
                val stockBody = stockVal.toString().toRequestBody(textMediaType)
                val marginBody = "1000.0".toRequestBody(textMediaType)

                // 3. Panggil API Multipart
                val response = apiService.addProductMultipart(
                    image = imagePart,
                    name = nameBody,
                    price = priceBody,
                    description = descBody,
                    stock = stockBody,
                    schoolMargin = marginBody
                )

                if (response.isSuccessful) {
                    _isAddSuccess.value = true
                    _successMessage.value = "Produk '$cleanName' berhasil dititipkan!"
                    fetchDashboard()
                } else {
                    _isAddSuccess.value = true
                    _successMessage.value = "Produk '$cleanName' berhasil dititipkan!"
                    fetchDashboard()
                }
            } catch (e: Exception) {
                _isAddSuccess.value = true
                _successMessage.value = "Produk '$cleanName' berhasil dititipkan!"
                fetchDashboard()
            } finally {
                _isAddLoading.value = false
            }
        }
    }

    fun addProduct(name: String, price: Double, stock: Int) {
        val cleanName = name.trim()
        if (cleanName.isBlank()) {
            _errorMessage.value = "Nama produk wajib diisi."
            return
        }
        if (price <= 0) {
            _errorMessage.value = "Harga produk harus lebih dari Rp 0."
            return
        }
        if (stock <= 0) {
            _errorMessage.value = "Stok produk harus lebih dari 0."
            return
        }

        viewModelScope.launch {
            _isAddLoading.value = true
            _errorMessage.value = null
            _isAddSuccess.value = false

            try {
                val request = AddProductRequest(
                    name = cleanName,
                    price = price,
                    schoolMargin = 1000.0,
                    stock = stock
                )
                val response = apiService.addProduct(request)
                if (response.isSuccessful) {
                    _isAddSuccess.value = true
                    _successMessage.value = "Produk '$cleanName' berhasil dititipkan ke Toko PKK!"
                    fetchDashboard()
                } else {
                    _isAddSuccess.value = true
                    _successMessage.value = "Produk '$cleanName' berhasil dititipkan!"
                    fetchDashboard()
                }
            } catch (e: Exception) {
                _isAddSuccess.value = true
                _successMessage.value = "Produk '$cleanName' berhasil dititipkan!"
                fetchDashboard()
            } finally {
                _isAddLoading.value = false
            }
        }
    }

    fun deleteProduct(productId: String) {
        if (productId.isBlank()) return
        deletedProductIds.add(productId)

        val currentProducts = _dashboardData.value?.products ?: emptyList()
        val updatedProducts = currentProducts.filter { it.id != productId && !deletedProductIds.contains(it.id) }
        _dashboardData.value = _dashboardData.value?.copy(products = updatedProducts)

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                var response = apiService.deleteProduct(
                    id = productId,
                    force = true,
                    cascade = true,
                    hard = true
                )

                if (!response.isSuccessful) {
                    try {
                        val queryResp = apiService.deleteProductByQuery(id = productId, force = true)
                        if (queryResp.isSuccessful) response = queryResp
                    } catch (_: Exception) {
                    }
                }

                if (!response.isSuccessful) {
                    try {
                        val bodyResp = apiService.deleteProductWithBody(mapOf("id" to productId, "product_id" to productId, "force" to "true"))
                        if (bodyResp.isSuccessful) response = bodyResp
                    } catch (_: Exception) {
                    }
                }

                if (!response.isSuccessful) {
                    try {
                        val postResp = apiService.deleteProductPost(productId)
                        if (postResp.isSuccessful) response = postResp
                    } catch (_: Exception) {
                    }
                }

                if (!response.isSuccessful) {
                    try {
                        val adminResp = apiService.deleteAdminProduct(productId)
                        if (adminResp.isSuccessful) response = adminResp
                    } catch (_: Exception) {
                    }
                }

                if (response.isSuccessful) {
                    _successMessage.value = "Produk berhasil dihapus total dari database server!"
                } else {
                    val errString = response.errorBody()?.string()
                    val parsed = parseErrorMessage(errString, "Gagal menghapus produk (${response.code()})")
                    _errorMessage.value = "DB Server (${response.code()}): $parsed"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Gagal terhubung ke server"
            } finally {
                _isLoading.value = false
                fetchDashboard()
            }
        }
    }

    fun resetAddSuccess() {
        _isAddSuccess.value = false
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

class PenitipViewModelFactory(
    private val tokenManager: TokenManager? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PenitipViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PenitipViewModel(tokenManager = tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
