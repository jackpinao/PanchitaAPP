package com.pinao.panchitaapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pinao.panchitaapp.data.source.local.SessionManager
import com.pinao.panchitaapp.data.source.local.dao.UserDao
import com.pinao.panchitaapp.data.source.local.entity.UserEntity
import com.pinao.panchitaapp.domain.model.UserModel
import com.pinao.panchitaapp.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val sessionManager: SessionManager,
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun signIn(email: String, pass: String): Result<UserModel> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: throw Exception("Error al obtener UID")

            // Buscamos datos adicionales en Firestore
            val userDoc = firestore.collection("users").document(uid).get().await()

            val role = userDoc.getString("role") ?: "vendedor"
            val storeId = userDoc.getString("store_id") ?: ""
            val emailValue = userDoc.getString("email") ?: ""
            val name = userDoc.getString("name") ?: ""
            val active = userDoc.getBoolean("is_active") ?: false

            // Persistimos en SessionManager (incluido el userId y userName)
            sessionManager.saveSession(storeId, role, uid, name)

            // Guardamos el usuario en Room para satisfacer la FK de ventas
            userDao.upsert(
                UserEntity(
                    userId = uid,
                    storeId = storeId,
                    name = name,
                    email = emailValue,
                    password = "",
                    role = role,
                    isActive = if (active) 1 else 0
                )
            )

            val user = UserModel(
                userId = uid,
                storeId = storeId,
                email = emailValue,
                role = role,
                active = active,
                name = name
            )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null

    override fun getCurrentUserId(): String =
        sessionManager.getUserId() ?: firebaseAuth.currentUser?.uid ?: ""

    override suspend fun ensureCurrentUserInRoom(): String {
        val uid = firebaseAuth.currentUser?.uid ?: return ""
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
        // No existe en Room: lo trae de Firestore y lo persiste
        return try {
            val userDoc = firestore.collection("users").document(uid).get().await()
            val role = userDoc.getString("role") ?: "vendedor"
            val storeId = userDoc.getString("store_id") ?: ""
            val emailValue = userDoc.getString("email") ?: ""
            val name = userDoc.getString("name") ?: ""
            val active = userDoc.getBoolean("is_active") ?: false
            sessionManager.saveSession(storeId, role, uid, name)
            userDao.upsert(
                UserEntity(
                    userId = uid,
                    storeId = storeId,
                    name = name,
                    email = emailValue,
                    password = "",
                    role = role,
                    isActive = if (active) 1 else 0
                )
            )
            uid
        } catch (e: Exception) {
            // Si Firestore falla, retornamos uid de todas formas para dar error claro al caller
            uid
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
        sessionManager.clearSession()
    }
}
