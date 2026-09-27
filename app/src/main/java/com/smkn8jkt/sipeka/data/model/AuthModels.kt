package com.smkn8jkt.sipeka.data.model

import com.google.gson.annotations.SerializedName

// Generic Base Response Wrapper
data class BaseResponse<T>(
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: T? = null
)

// Request Data Classes
data class LoginRequest(
    @SerializedName("nisn_nip") val nisnNip: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("nisn_nip") val nisnNip: String,
    @SerializedName("name") val name: String,
    @SerializedName("password") val password: String,
    @SerializedName("role") val role: String
)

// Response Data Classes
data class LoginResponse(
    @SerializedName("token") val token: String? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("user") val user: UserData? = null
)

data class UserModel(
    @SerializedName("id") val id: String? = null,
    @SerializedName("nisn_nip") val nisnNip: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("status") val status: String? = null
)
