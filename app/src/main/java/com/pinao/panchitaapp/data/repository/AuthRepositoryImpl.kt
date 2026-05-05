package com.pinao.panchitaapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pinao.panchitaapp.data.source.local.SessionManager
import com.pinao.panchitaapp.data.source.local.dao.UserDao
import com.pinao.panchitaapp.data.source.local.entity.UserEntity
import com.pinao.panchitaapp.domain.model.UserModel
import com.pinao.panchitaapp.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await

import io.github.jan_tennert.supabase.SupabaseClient
import io.github.jan_tennert.supabase.auth.auth
import io.github.jan_tennert.supabase.auth.providers.builtin.Email
import io.github.jan_tennert.supabase.postgrest.postgrest
import io.github.jan_tennert.supabase.postgrest.query.Columns
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

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
            supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = pass
            }
            
            val uid = supabaseClient.auth.currentUserOrNull()?.id ?: throw Exception("Error al obtener UID")

            // Buscamos datos adicionales en la tabla 'users' de Supabase
            val userDto = supabaseClient.postgrest["users"]
                .select(columns = Columns.ALL) {
                    filter {
                        eq("auth_id", uid)
                    }
                }
                .decodeSingle<SupabaseUserDto>()

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
            Result.failure(e)
        }
    }

    override fun isUserLoggedIn(): Boolean = supabaseClient.auth.currentUserOrNull() != null

    override fun getCurrentUserId(): String =
        sessionManager.getUserId() ?: supabaseClient.auth.currentUserOrNull()?.id ?: ""

    override suspend fun ensureCurrentUserInRoom(): String {
        val uid = supabaseClient.auth.currentUserOrNull()?.id ?: return ""
        // Si ya existe en Room no hace nada costoso
        if (userDao.getUserForId(uid) != null) {
            if (sessionManager.getUserId().isNullOrEmpty()) {
                val user = userDao.getUserForId(uid)
                sessionManager.saveSession(
                    sessionManager.getStoreId() ?: "",
                    sessionManager.getUserRole() ?: "",
                    uid,
                    user?.name ?: ""
                )
            }
            return uid
        }
        // No existe en Room: lo trae de Supabase y lo persiste
        return try {
            val userDto = supabaseClient.postgrest["users"]
                .select(columns = Columns.ALL) {
                    filter {
                        eq("auth_id", uid)
                    }
                }
                .decodeSingle<SupabaseUserDto>()

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
