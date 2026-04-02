package com.pinao.panchitaapp.data.repository

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.pinao.panchitaapp.data.source.local.SessionManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryImplTest {

    private lateinit var repository: AuthRepositoryImpl
    private val mockFirebaseAuth: FirebaseAuth = mockk()
    private val mockFirestore: FirebaseFirestore = mockk()
    private val mockSessionManager: SessionManager = mockk(relaxed = true)

    private val mockAuthResult: AuthResult = mockk()
    private val mockFirebaseUser: FirebaseUser = mockk()
    
    private val mockCollection: CollectionReference = mockk()
    private val mockDocument: DocumentReference = mockk()
    private val mockSnapshot: DocumentSnapshot = mockk()

    @Before
    fun setup() {
        repository = AuthRepositoryImpl(mockFirebaseAuth, mockFirestore, mockSessionManager)
    }

    @Test
    fun `signIn should return success with mapped UserModel when auth and firestore succeed`() = runTest {
        // Arrange Auth
        val email = "test@test.com"
        val password = "password123"
        val uid = "user_uid_123"

        every { mockFirebaseUser.uid } returns uid
        every { mockAuthResult.user } returns mockFirebaseUser
        every { mockFirebaseAuth.signInWithEmailAndPassword(email, password) } returns Tasks.forResult(mockAuthResult)

        // Arrange Firestore
        every { mockFirestore.collection("users") } returns mockCollection
        every { mockCollection.document(uid) } returns mockDocument
        
        every { mockSnapshot.getString("role") } returns "admin"
        every { mockSnapshot.getString("store_id") } returns "store_01"
        every { mockSnapshot.getString("email") } returns email
        every { mockSnapshot.getString("name") } returns "Admin User"
        every { mockSnapshot.getBoolean("is_active") } returns true
        
        every { mockDocument.get() } returns Tasks.forResult(mockSnapshot)

        // Act
        val result = repository.signIn(email, password)

        // Assert
        assertTrue(result.isSuccess)
        val userModel = result.getOrNull()
        
        assertEquals(uid, userModel?.userId)
        assertEquals("store_01", userModel?.storeId)
        assertEquals("admin", userModel?.role)
        assertEquals(email, userModel?.email)
        assertEquals("Admin User", userModel?.name)
        assertEquals(true, userModel?.active)

        verify(exactly = 1) { mockSessionManager.saveSession("store_01", "admin") }
    }

    @Test
    fun `signIn should return failure when auth throws exception`() = runTest {
        // Arrange
        val exception = Exception("Invalid credentials")
        every { mockFirebaseAuth.signInWithEmailAndPassword(any(), any()) } returns Tasks.forException(exception)

        // Act
        val result = repository.signIn("wrong@test.com", "wrong")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Invalid credentials", result.exceptionOrNull()?.message)
        verify(exactly = 0) { mockSessionManager.saveSession(any(), any()) }
    }

    @Test
    fun `isUserLoggedIn should return true when currentUser is not null`() {
        every { mockFirebaseAuth.currentUser } returns mockFirebaseUser
        assertTrue(repository.isUserLoggedIn())
    }

    @Test
    fun `isUserLoggedIn should return false when currentUser is null`() {
        every { mockFirebaseAuth.currentUser } returns null
        assertFalse(repository.isUserLoggedIn())
    }

    @Test
    fun `signOut should call firebaseAuth signOut and clearSession`() {
        every { mockFirebaseAuth.signOut() } returns Unit

        repository.signOut()

        verify(exactly = 1) { mockFirebaseAuth.signOut() }
        verify(exactly = 1) { mockSessionManager.clearSession() }
    }
}