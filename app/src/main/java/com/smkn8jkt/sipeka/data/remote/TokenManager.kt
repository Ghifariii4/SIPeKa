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

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class TokenManager(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val ROLE_KEY = stringPreferencesKey("user_role")
        private val STARTING_CASH_KEY = doublePreferencesKey("starting_cash")
        private val VALIDATED_SHIFTS_KEY = stringSetPreferencesKey("validated_shifts")
        private val PROCESSED_PAYOUTS_KEY = stringSetPreferencesKey("processed_payouts")
        private val APPROVED_USERS_KEY = stringSetPreferencesKey("approved_users")
    }

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

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun saveAuthData(token: String, role: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[ROLE_KEY] = role
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
            // KITA TETA PKAN VALIDATED SHIFTS, PROCESSED PAYOUTS, & APPROVED USERS AGAR PERSISTEN MESKI RE-LOGIN!
        }
    }

    fun getTokenSync(): String? = runBlocking {
        tokenFlow.firstOrNull()
    }

    fun getRoleSync(): String? = runBlocking {
        roleFlow.firstOrNull()
    }

    fun getStartingCashSync(): Double = runBlocking {
        startingCashFlow.firstOrNull() ?: 0.0
    }

    fun getApprovedUsersSync(): Set<String> = runBlocking {
        approvedUsersFlow.firstOrNull() ?: emptySet()
    }
}
