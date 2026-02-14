package com.pinao.panchitaapp.data.local

import android.content.SharedPreferences

/**
 * Gestiona la persistencia de la sesión del usuario.
 * Adaptado para seguir la lógica de 'Multiplatform Settings'.
 */
class SessionManager(private val sharedPreferences: SharedPreferences) {

    fun getStoreId(): String? = sharedPreferences.getString("current_store_id", null)
    fun getUserRole(): String? = sharedPreferences.getString("user_role", null)

    fun saveSession(storeId: String, role: String) {
        sharedPreferences.edit().apply {
            putString("current_store_id", storeId)
            putString("user_role", role)
            apply()
        }
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}