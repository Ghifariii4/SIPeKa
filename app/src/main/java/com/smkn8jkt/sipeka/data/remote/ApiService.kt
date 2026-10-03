package com.smkn8jkt.sipeka.data.remote

import com.smkn8jkt.sipeka.data.model.AddProductRequest
import com.smkn8jkt.sipeka.data.model.BaseResponse
import com.smkn8jkt.sipeka.data.model.ClockInRequest
import com.smkn8jkt.sipeka.data.model.LoginRequest
import com.smkn8jkt.sipeka.data.model.LoginResponse
import com.smkn8jkt.sipeka.data.model.OrderData
import com.smkn8jkt.sipeka.data.model.OrderRequest
import com.smkn8jkt.sipeka.data.model.PayoutResponse
import com.smkn8jkt.sipeka.data.model.PenitipDashboardResponse
import com.smkn8jkt.sipeka.data.model.ProductResponse
import com.smkn8jkt.sipeka.data.model.ProfitResponse
import com.smkn8jkt.sipeka.data.model.RegisterRequest
import com.smkn8jkt.sipeka.data.model.ScanData
import com.smkn8jkt.sipeka.data.model.ShiftData
import com.smkn8jkt.sipeka.data.model.ShiftValidationResponse
import com.smkn8jkt.sipeka.data.model.UserData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

// Retrofit API Service Interface
interface ApiService {

    // Auth Login
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<BaseResponse<LoginResponse>>

    // Auth Register
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<BaseResponse<Any>>

    // Products
    @GET("products")
    suspend fun getProducts(
        @Query("q") query: String? = null
    ): Response<BaseResponse<List<ProductResponse>>>

    @GET("products/{id}")
    suspend fun getProductById(
        @Path("id") id: String
    ): Response<BaseResponse<ProductResponse>>

    @DELETE("products/{id}")
    suspend fun deleteProduct(
        @Path("id") id: String,
        @Query("force") force: Boolean = true,
        @Query("cascade") cascade: Boolean = true,
        @Query("hard") hard: Boolean = true
    ): Response<BaseResponse<Any>>

    @DELETE("products")
    suspend fun deleteProductByQuery(
        @Query("id") id: String,
        @Query("force") force: Boolean = true
    ): Response<BaseResponse<Any>>

    @POST("products/delete")
    suspend fun deleteProductWithBody(
        @Body body: Map<String, String>
    ): Response<BaseResponse<Any>>

    @POST("products/{id}/delete")
    suspend fun deleteProductPost(
        @Path("id") id: String
    ): Response<BaseResponse<Any>>

    @DELETE("admin/products/{id}")
    suspend fun deleteAdminProduct(
        @Path("id") id: String
    ): Response<BaseResponse<Any>>

    @POST("products")
    suspend fun createProduct(
        @Body request: ProductResponse
    ): Response<BaseResponse<ProductResponse>>

    // Orders / Checkout
    @POST("orders")
    suspend fun createOrder(
        @Body request: OrderRequest
    ): Response<BaseResponse<OrderData>>

    // Admin (Manajemen User & Pendaftaran)
    @GET("admin/users")
    suspend fun getUsers(): Response<BaseResponse<List<UserData>>>

    @POST("admin/users")
    suspend fun createUser(
        @Body request: RegisterRequest
    ): Response<BaseResponse<UserData>>

    @POST("admin/users/internal")
    suspend fun createInternalUser(
        @Body request: RegisterRequest
    ): Response<BaseResponse<UserData>>

    @PUT("admin/users/{id}/approve")
    suspend fun approveUser(
        @Path("id") id: String
    ): Response<BaseResponse<UserData>>

    @PUT("auth/profile")
    suspend fun updateProfile(
        @Body request: UserData
    ): Response<BaseResponse<UserData>>

    @PUT("admin/users/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Body request: UserData
    ): Response<BaseResponse<UserData>>

    // Admin Finance (Laba, Validasi Setoran, Bagi Hasil)
    @GET("admin/finance/profit")
    suspend fun getSchoolProfit(): Response<BaseResponse<ProfitResponse>>

    @GET("admin/finance/shifts")
    suspend fun getPendingShifts(): Response<BaseResponse<List<ShiftValidationResponse>>>

    @PUT("admin/finance/shifts/{id}/validate")
    suspend fun validateShift(
        @Path("id") shiftId: String
    ): Response<BaseResponse<Any>>

    @GET("admin/finance/payouts")
    suspend fun getPendingPayouts(): Response<BaseResponse<List<PayoutResponse>>>

    @PUT("admin/finance/payouts/{penitip_id}")
    suspend fun processPayout(
        @Path("penitip_id") penitipId: String
    ): Response<BaseResponse<Any>>

    // Penitip Endpoints
    @GET("penitip/dashboard")
    suspend fun getPenitipDashboard(): Response<BaseResponse<PenitipDashboardResponse>>

    @POST("products")
    suspend fun addProduct(
        @Body request: AddProductRequest
    ): Response<BaseResponse<Any>>

    @Multipart
    @POST("products")
    suspend fun addProductMultipart(
        @Part image: MultipartBody.Part?,
        @Part("name") name: RequestBody,
        @Part("price") price: RequestBody,
        @Part("category") category: RequestBody? = null,
        @Part("description") description: RequestBody,
        @Part("stock") stock: RequestBody,
        @Part("school_margin") schoolMargin: RequestBody
    ): Response<BaseResponse<Any>>

    // Shifts (Kasir)
    @POST("shifts/clock-in")
    suspend fun clockIn(
        @Body request: ClockInRequest
    ): Response<BaseResponse<Any>>

    @GET("shifts/current")
    suspend fun getCurrentShift(): Response<BaseResponse<ShiftData>>

    @POST("shifts/clock-out")
    suspend fun clockOut(): Response<BaseResponse<Any>>

    // POS & Orders (Transaksi)
    @POST("pos/transaction")
    suspend fun createTransaction(
        @Body request: OrderRequest
    ): Response<BaseResponse<Any>>

    @PUT("pos/scan/{qr_code}")
    suspend fun scanQrCode(
        @Path("qr_code") qrCode: String
    ): Response<BaseResponse<ScanData>>

    @PUT("pos/scan/{qr_code}")
    suspend fun scanQrOrder(
        @Path("qr_code") qrCode: String
    ): Response<BaseResponse<Any>>

    @GET("orders")
    suspend fun getOrders(): Response<BaseResponse<List<OrderData>>>

    @GET("orders/{id}")
    suspend fun getOrderById(
        @Path("id") id: String
    ): Response<BaseResponse<OrderData>>
}
