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

    /**
     * Retorna el ID del usuario actualmente autenticado, o cadena vacía si no hay sesión.
     */
    fun getCurrentUserId(): String

    /**
     * Garantiza que el usuario autenticado exista en Room, incluso si tenía sesión activa
     * antes de que se implementara la persistencia local. Retorna el userId.
     */
    suspend fun ensureCurrentUserInRoom(): String
}
