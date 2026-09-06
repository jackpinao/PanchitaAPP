package com.pinao.panchitaapp.data.source.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.pinao.panchitaapp.domain.repository.PrinterSettingsRepository

/**
 * Gestiona la persistencia de la sesión del usuario.
 * Adaptado para seguir la lógica de 'Multiplatform Settings'.
 */
class SessionManager(private val sharedPreferences: SharedPreferences) : PrinterSettingsRepository {

    fun getStoreId(): String? = sharedPreferences.getString("current_store_id", null)
    fun getUserRole(): String? = sharedPreferences.getString("user_role", null)
    fun getUserId(): String? = sharedPreferences.getString("current_user_id", null)
    fun getUserName(): String? = sharedPreferences.getString("user_name", null)
    fun getPrinterAddress(): String? = sharedPreferences.getString("selected_printer_address", null)

    fun getStoreName(): String? = sharedPreferences.getString("store_name", null)
    fun getStoreRuc(): String? = sharedPreferences.getString("store_ruc", null)
    fun getStoreAddress(): String? = sharedPreferences.getString("store_address", null)
    fun getStorePhone(): String? = sharedPreferences.getString("store_phone", null)
    override fun getPrinterConnectionType(): String =
        sharedPreferences.getString("printer_connection_type", "BLUETOOTH") ?: "BLUETOOTH"

    fun savePrinterAddress(address: String) {
        sharedPreferences.edit().putString("selected_printer_address", address).apply()
    }

    fun savePrinterConnectionType(type: String) {
        sharedPreferences.edit().putString("printer_connection_type", type).apply()
    }

    fun saveStoreDetails(name: String, ruc: String, address: String, phone: String) {
        sharedPreferences.edit().apply {
            putString("store_name", name)
            putString("store_ruc", ruc)
            putString("store_address", address)
            putString("store_phone", phone)
            apply()
        }
    }

    fun saveSession(storeId: String, role: String, userId: String = "", userName: String = "") {
        sharedPreferences.edit().apply {
            putString("current_store_id", storeId)
            putString("user_role", role)
            if (userId.isNotEmpty()) putString("current_user_id", userId)
            if (userName.isNotEmpty()) putString("user_name", userName)
            apply()
        }
    }

    fun clearSession() {
        sharedPreferences.edit { clear() }
    }
}
