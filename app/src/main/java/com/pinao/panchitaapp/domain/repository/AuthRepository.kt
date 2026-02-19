package com.pinao.panchitaapp.domain.repository

import com.pinao.panchitaapp.domain.model.UserModel

interface AuthRepository {
    /**
     * Realiza el inicio de sesión y persiste los datos en SessionManager.
     */
    suspend fun signIn(email: String, pass: String): Result<UserModel>

    /**
     * Verifica si el usuario ya está autenticado.
     */
    fun isUserLoggedIn(): Boolean

    /**
     * Cierra la sesión del usuario.
     */
    fun signOut()
}
