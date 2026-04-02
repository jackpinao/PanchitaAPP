package com.pinao.panchitaapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pinao.panchitaapp.data.source.local.SessionManager
import com.pinao.panchitaapp.domain.model.UserModel
import com.pinao.panchitaapp.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val sessionManager: SessionManager
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

            // Persistimos en tu SessionManager
            sessionManager.saveSession(storeId, role)

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

    override fun signOut() {
        firebaseAuth.signOut()
        sessionManager.clearSession()
    }
}
