package com.smkn8jkt.sipeka.data.remote

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.smkn8jkt.sipeka.data.model.OrderData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class TokenManager(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val ROLE_KEY = stringPreferencesKey("user_role")
        private val STARTING_CASH_KEY = doublePreferencesKey("starting_cash")
        private val VALIDATED_SHIFTS_KEY = stringSetPreferencesKey("validated_shifts")
        private val PROCESSED_PAYOUTS_KEY = stringSetPreferencesKey("processed_payouts")
        private val APPROVED_USERS_KEY = stringSetPreferencesKey("approved_users")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_NISN_KEY = stringPreferencesKey("user_nisn")
        private val USER_CLASS_KEY = stringPreferencesKey("user_class")
        private val USER_ORDERS_KEY = stringSetPreferencesKey("user_orders")
        private val SHARED_ORDERS_KEY = stringPreferencesKey("shared_system_orders_json")
        private val KASIR_NAME_KEY = stringPreferencesKey("kasir_name")
        private val KASIR_NIP_KEY = stringPreferencesKey("kasir_nip")
        private val PENITIP_NAME_KEY = stringPreferencesKey("penitip_name")
        private val PENITIP_NIP_KEY = stringPreferencesKey("penitip_nip")
        private val PENITIP_SHOP_KEY = stringPreferencesKey("penitip_shop")
    }

    private val gson = Gson()

    val tokenFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    val roleFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[ROLE_KEY]
    }

    val startingCashFlow: Flow<Double> = context.dataStore.data.map { preferences ->
        preferences[STARTING_CASH_KEY] ?: 0.0
    }

    val validatedShiftsFlow: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[VALIDATED_SHIFTS_KEY] ?: emptySet()
    }

    val processedPayoutsFlow: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[PROCESSED_PAYOUTS_KEY] ?: emptySet()
    }

    val approvedUsersFlow: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[APPROVED_USERS_KEY] ?: emptySet()
    }

    val userIdFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }

    val userNameFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME_KEY] ?: "Raysha Putra"
    }

    val userNisnFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_NISN_KEY] ?: "54321"
    }

    val userClassFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_CLASS_KEY] ?: "XII RPL"
    }

    val userOrdersFlow: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[USER_ORDERS_KEY] ?: emptySet()
    }

    val kasirNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KASIR_NAME_KEY] ?: preferences[USER_NAME_KEY] ?: "Petugas Kasir 1"
    }

    val kasirNipFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KASIR_NIP_KEY] ?: preferences[USER_NISN_KEY] ?: "19820512"
    }

    val penitipNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PENITIP_NAME_KEY] ?: preferences[USER_NAME_KEY] ?: "Ibu Sari (Dapur PKK)"
    }

    val penitipNipFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PENITIP_NIP_KEY] ?: preferences[USER_NISN_KEY] ?: "19780415"
    }

    val penitipShopFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PENITIP_SHOP_KEY] ?: "Kantin PKK Sejahtera"
    }

    val sharedOrdersFlow: Flow<List<OrderData>> = context.dataStore.data.map { preferences ->
        val json = preferences[SHARED_ORDERS_KEY]
        if (json.isNullOrBlank()) {
            emptyList()
        } else {
            try {
                val type = object : TypeToken<List<OrderData>>() {}.type
                gson.fromJson<List<OrderData>>(json, type) ?: emptyList()
            } catch (_: Exception) {
                emptyList()
            }
        }
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun saveAuthData(token: String, role: String, userId: String? = null, userName: String? = null, userNisn: String? = null) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[ROLE_KEY] = role
            if (!userId.isNullOrBlank()) preferences[USER_ID_KEY] = userId
            if (!userName.isNullOrBlank()) preferences[USER_NAME_KEY] = userName
            if (!userNisn.isNullOrBlank()) preferences[USER_NISN_KEY] = userNisn
        }
    }

    suspend fun saveUserProfile(id: String? = null, name: String? = null, nisn: String? = null, kelas: String? = null) {
        context.dataStore.edit { preferences ->
            if (!id.isNullOrBlank()) preferences[USER_ID_KEY] = id
            if (!name.isNullOrBlank()) preferences[USER_NAME_KEY] = name
            if (!nisn.isNullOrBlank()) preferences[USER_NISN_KEY] = nisn
            if (!kelas.isNullOrBlank()) preferences[USER_CLASS_KEY] = kelas
        }
    }

    suspend fun saveUserOrder(orderId: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[USER_ORDERS_KEY] ?: emptySet()
            preferences[USER_ORDERS_KEY] = current + orderId
        }
    }

    suspend fun saveStartingCash(cash: Double) {
        context.dataStore.edit { preferences ->
            preferences[STARTING_CASH_KEY] = cash
        }
    }

    suspend fun clearStartingCash() {
        context.dataStore.edit { preferences ->
            preferences.remove(STARTING_CASH_KEY)
        }
    }

    suspend fun markShiftValidated(shiftId: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[VALIDATED_SHIFTS_KEY] ?: emptySet()
            preferences[VALIDATED_SHIFTS_KEY] = current + shiftId
        }
    }

    suspend fun markShiftsValidated(shiftIds: List<String>) {
        context.dataStore.edit { preferences ->
            val current = preferences[VALIDATED_SHIFTS_KEY] ?: emptySet()
            preferences[VALIDATED_SHIFTS_KEY] = current + shiftIds
        }
    }

    suspend fun markPayoutProcessed(penitipId: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[PROCESSED_PAYOUTS_KEY] ?: emptySet()
            preferences[PROCESSED_PAYOUTS_KEY] = current + penitipId
        }
    }

    suspend fun markPayoutsProcessed(penitipIds: List<String>) {
        context.dataStore.edit { preferences ->
            val current = preferences[PROCESSED_PAYOUTS_KEY] ?: emptySet()
            preferences[PROCESSED_PAYOUTS_KEY] = current + penitipIds
        }
    }

    suspend fun markUserApproved(userId: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[APPROVED_USERS_KEY] ?: emptySet()
            preferences[APPROVED_USERS_KEY] = current + userId
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(ROLE_KEY)
            preferences.remove(STARTING_CASH_KEY)
        }
    }

    fun getTokenSync(): String? = runBlocking {
        tokenFlow.firstOrNull()
    }

    fun getRoleSync(): String? = runBlocking {
        roleFlow.firstOrNull()
    }

    fun getUserIdSync(): String? = runBlocking {
        userIdFlow.firstOrNull()
    }

    fun getUserNameSync(): String? = runBlocking {
        userNameFlow.firstOrNull()
    }

    fun getUserNisnSync(): String? = runBlocking {
        userNisnFlow.firstOrNull()
    }

    fun getUserClassSync(): String? = runBlocking {
        userClassFlow.firstOrNull()
    }

    fun getUserOrdersSync(): Set<String> = runBlocking {
        userOrdersFlow.firstOrNull() ?: emptySet()
    }

    suspend fun saveSharedOrder(order: OrderData) {
        context.dataStore.edit { preferences ->
            val json = preferences[SHARED_ORDERS_KEY]
            val currentList = if (!json.isNullOrBlank()) {
                try {
                    val type = object : TypeToken<List<OrderData>>() {}.type
                    gson.fromJson<List<OrderData>>(json, type)?.toMutableList() ?: mutableListOf()
                } catch (_: Exception) {
                    mutableListOf()
                }
            } else {
                mutableListOf()
            }

            val index = currentList.indexOfFirst { it.id == order.id || (it.qrCode != null && it.qrCode == order.qrCode) }
            if (index >= 0) {
                currentList[index] = order
            } else {
                currentList.add(0, order)
            }

            preferences[SHARED_ORDERS_KEY] = gson.toJson(currentList)
        }
    }

    suspend fun completeSharedOrder(
        orderIdOrQr: String, 
        kasirName: String, 
        shiftId: String? = null
    ): Pair<Boolean, String> {
        var resultPair = Pair(false, "Pesanan tidak ditemukan.")
        val nowIso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(Date())

        context.dataStore.edit { preferences ->
            val json = preferences[SHARED_ORDERS_KEY]
            val currentList = if (!json.isNullOrBlank()) {
                try {
                    val type = object : TypeToken<List<OrderData>>() {}.type
                    gson.fromJson<List<OrderData>>(json, type)?.toMutableList() ?: mutableListOf()
                } catch (_: Exception) {
                    mutableListOf()
                }
            } else {
                mutableListOf()
            }

            val index = currentList.indexOfFirst { 
                (it.id != null && it.id.equals(orderIdOrQr, ignoreCase = true)) || 
                (it.qrCode != null && it.qrCode.equals(orderIdOrQr, ignoreCase = true)) 
            }

            if (index >= 0) {
                val existing = currentList[index]
                if (existing.isCompleted) {
                    // KEAMANAN TINGKAT TINGGI: Cegah pengambilan ganda (Anti-Jahil)
                    resultPair = Pair(false, "⛔ PERINGATAN KEAMANAN: Pesanan #${existing.id?.take(8)} atas nama ${existing.displayCustomerName} SUDAH PERNAH DIAMBIL sebelumnya! Tolong jangan serahkan makanan ganda.")
                } else if (existing.isCancelled) {
                    resultPair = Pair(false, "⚠️ Pesanan #${existing.id?.take(8)} telah dibatalkan oleh siswa.")
                } else {
                    val finalShiftId = if (!shiftId.isNullOrBlank()) shiftId else existing.shiftId
                    val updated = existing.copy(
                        status = "COMPLETED",
                        completedAt = nowIso,
                        kasirName = kasirName,
                        shiftId = finalShiftId
                    )
                    currentList[index] = updated
                    preferences[SHARED_ORDERS_KEY] = gson.toJson(currentList)
                    resultPair = Pair(true, "✅ Verifikasi Berhasil! Pesanan #${updated.id?.take(8)} atas nama ${updated.displayCustomerName} (${updated.displayCustomerClass}) telah diserahkan.")
                }
            } else {
                resultPair = Pair(false, "❌ QR Code / ID Pesanan tidak ditemukan dalam sistem.")
            }
        }

        return resultPair
    }

    suspend fun cancelSharedOrder(orderIdOrQr: String): Pair<Boolean, String> {
        var resultPair = Pair(false, "Pesanan tidak ditemukan.")
        context.dataStore.edit { preferences ->
            val json = preferences[SHARED_ORDERS_KEY]
            val currentList = if (!json.isNullOrBlank()) {
                try {
                    val type = object : TypeToken<List<OrderData>>() {}.type
                    gson.fromJson<List<OrderData>>(json, type)?.toMutableList() ?: mutableListOf()
                } catch (_: Exception) {
                    mutableListOf()
                }
            } else {
                mutableListOf()
            }

            val index = currentList.indexOfFirst {
                (it.id != null && it.id.equals(orderIdOrQr, ignoreCase = true)) ||
                (it.qrCode != null && it.qrCode.equals(orderIdOrQr, ignoreCase = true))
            }

            if (index >= 0) {
                val existing = currentList[index]
                if (existing.isCompleted) {
                    resultPair = Pair(false, "Pesanan sudah selesai diserahkan dan tidak dapat dibatalkan.")
                } else if (existing.isCancelled) {
                    resultPair = Pair(false, "Pesanan ini sudah dibatalkan sebelumnya.")
                } else {
                    val updated = existing.copy(status = "CANCELLED")
                    currentList[index] = updated
                    preferences[SHARED_ORDERS_KEY] = gson.toJson(currentList)
                    resultPair = Pair(true, "Pesanan berhasil dibatalkan.")
                }
            } else {
                resultPair = Pair(false, "Pesanan tidak ditemukan.")
            }
        }
        return resultPair
    }

    suspend fun saveKasirProfile(name: String?, nip: String?) {
        context.dataStore.edit { preferences ->
            if (!name.isNullOrBlank()) preferences[KASIR_NAME_KEY] = name
            if (!nip.isNullOrBlank()) preferences[KASIR_NIP_KEY] = nip
        }
    }

    suspend fun savePenitipProfile(name: String?, nip: String?, shopName: String?) {
        context.dataStore.edit { preferences ->
            if (!name.isNullOrBlank()) preferences[PENITIP_NAME_KEY] = name
            if (!nip.isNullOrBlank()) preferences[PENITIP_NIP_KEY] = nip
            if (!shopName.isNullOrBlank()) preferences[PENITIP_SHOP_KEY] = shopName
        }
    }

    fun getSharedOrdersSync(): List<OrderData> = runBlocking {
        sharedOrdersFlow.firstOrNull() ?: emptyList()
    }

    fun getKasirNameSync(): String = runBlocking {
        kasirNameFlow.firstOrNull() ?: "Petugas Kasir 1"
    }

    fun getPenitipNameSync(): String = runBlocking {
        penitipNameFlow.firstOrNull() ?: "Ibu Sari (Dapur PKK)"
    }

    fun getPenitipShopSync(): String = runBlocking {
        penitipShopFlow.firstOrNull() ?: "Kantin PKK Sejahtera"
    }

    fun getStartingCashSync(): Double = runBlocking {
        startingCashFlow.firstOrNull() ?: 0.0
    }

    fun getApprovedUsersSync(): Set<String> = runBlocking {
        approvedUsersFlow.firstOrNull() ?: emptySet()
    }
}
