package com.pinao.panchitaapp.data.source.local

import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Gestiona la persistencia de la sesión del usuario.
 * Adaptado para seguir la lógica de 'Multiplatform Settings'.
 */
class SessionManager(private val sharedPreferences: SharedPreferences) {

    fun getStoreId(): String? = sharedPreferences.getString("current_store_id", null)
    fun getUserRole(): String? = sharedPreferences.getString("user_role", null)
    fun getUserId(): String? = sharedPreferences.getString("current_user_id", null)
    fun getUserName(): String? = sharedPreferences.getString("user_name", null)

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