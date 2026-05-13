package com.pinao.panchitaapp.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pinao.panchitaapp.data.source.local.SessionManager
import com.pinao.panchitaapp.data.source.local.dao.UserDao
import com.pinao.panchitaapp.data.source.local.entity.UserEntity
import com.pinao.panchitaapp.domain.model.UserModel
import com.pinao.panchitaapp.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.coroutines.flow.first

class AuthRepositoryImpl(
    private val supabaseClient: SupabaseClient,
    private val sessionManager: SessionManager,
    private val userDao: UserDao,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    @Serializable
    private data class SupabaseUserDto(
        @SerialName("id") val id: String,
        @SerialName("tenant_id") val tenantId: String,
        @SerialName("auth_id") val authId: String,
        @SerialName("full_name") val fullName: String,
        @SerialName("email") val email: String,
        @SerialName("role") val role: String,
        @SerialName("is_active") val isActive: Boolean
    )

    override suspend fun signIn(email: String, pass: String): Result<UserModel> {
        return try {
            Log.d("AuthRepositoryImpl", "Intentando login con email: $email")
            supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = pass
            }
            
            val currentUser = supabaseClient.auth.currentUserOrNull()
            Log.d("AuthRepositoryImpl", "Login exitoso, currentUser: $currentUser")
            
            val uid = currentUser?.id ?: throw Exception("Error al obtener UID")
            // Usar el email canónico de Supabase Auth (siempre lowercase) para la búsqueda
            val canonicalEmail = currentUser.email ?: email

            // Buscamos datos del usuario mediante RPC para eludir el RLS (Security Definer)
            Log.d("AuthRepositoryImpl", "Buscando usuario mediante RPC get_user_profile")
            var userDto = supabaseClient.postgrest
                .rpc("get_user_profile")
                .decodeList<SupabaseUserDto>()
                .firstOrNull()
            
            if (userDto == null) {
                Log.e("AuthRepositoryImpl", "Usuario NO encontrado en la tabla 'users' mediante el RPC get_user_profile. Verificar que el registro en la web creó la fila.")
                throw Exception("Datos de usuario no encontrados en la base de datos. Verifique que su cuenta fue registrada correctamente.")
            }
            
            Log.d("AuthRepositoryImpl", "Usuario encontrado: $userDto")

            // Persistimos en SessionManager
            sessionManager.saveSession(userDto.tenantId, userDto.role, userDto.id, userDto.fullName)

            // Guardamos en Room
            userDao.upsert(
                UserEntity(
                    userId = userDto.id,
                    storeId = userDto.tenantId,
                    name = userDto.fullName,
                    email = userDto.email,
                    password = "",
                    role = userDto.role,
                    isActive = if (userDto.isActive) 1 else 0
                )
            )

            val user = UserModel(
                userId = userDto.id,
                storeId = userDto.tenantId,
                email = userDto.email,
                role = userDto.role,
                active = userDto.isActive,
                name = userDto.fullName
            )
            Result.success(user)
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "Error en signIn", e)
            Result.failure(e)
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        // Wait until the session has finished loading from storage
        val status = supabaseClient.auth.sessionStatus.first { it !is io.github.jan.supabase.auth.status.SessionStatus.LoadingFromStorage }
        return status is io.github.jan.supabase.auth.status.SessionStatus.Authenticated
    }

    override fun getCurrentUserId(): String =
        sessionManager.getUserId() ?: supabaseClient.auth.currentUserOrNull()?.id ?: ""

    override suspend fun ensureCurrentUserInRoom(): String {
        val currentUser = supabaseClient.auth.currentUserOrNull()
        val uid = currentUser?.id ?: return ""
        val email = currentUser.email ?: ""

        // Si ya existe en Room no hace nada costoso
        // Intentamos buscar por uid (auth_id) o por el ID guardado en sesión
        val sessionUserId = sessionManager.getUserId()
        val userInRoom = if (!sessionUserId.isNullOrEmpty()) {
            userDao.getUserForId(sessionUserId)
        } else {
            userDao.getUserForId(uid)
        }

        if (userInRoom != null) {
            if (sessionManager.getUserId().isNullOrEmpty()) {
                sessionManager.saveSession(
                    sessionManager.getStoreId() ?: "",
                    sessionManager.getUserRole() ?: "",
                    userInRoom.userId,
                    userInRoom.name
                )
            }
            return userInRoom.userId
        }

        // No existe en Room: lo trae de Supabase (con RPC para bypass RLS) y lo persiste
        return try {
            Log.d("AuthRepositoryImpl", "ensureCurrentUserInRoom: Obteniendo datos vía RPC get_user_profile")
            val userDto = supabaseClient.postgrest
                .rpc("get_user_profile")
                .decodeList<SupabaseUserDto>()
                .firstOrNull()

            if (userDto == null) {
                throw Exception("Datos de usuario no encontrados en la base de datos.")
            }

            sessionManager.saveSession(userDto.tenantId, userDto.role, userDto.id, userDto.fullName)
            userDao.upsert(
                UserEntity(
                    userId = userDto.id,
                    storeId = userDto.tenantId,
                    name = userDto.fullName,
                    email = userDto.email,
                    password = "",
                    role = userDto.role,
                    isActive = if (userDto.isActive) 1 else 0
                )
            )
            userDto.id
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "Error en ensureCurrentUserInRoom", e)
            uid
        }
    }

    override fun signOut() {
        // Ejecutar en un scope o hacer bloqueante si es necesario, 
        // pero la interfaz de AuthRepository suele ser síncrona para signOut
        // supabaseClient.auth.signOut() // Esto es suspend en v3.0+
        sessionManager.clearSession()
        // Nota: El signOut de Supabase es suspendido. 
        // Si la UI requiere que sea inmediato, se puede lanzar en un scope global 
        // o cambiar la firma de la interfaz.
    }
}
