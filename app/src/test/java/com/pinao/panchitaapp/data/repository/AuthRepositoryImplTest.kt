package com.pinao.panchitaapp.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import com.pinao.panchitaapp.data.source.local.SessionManager
import com.pinao.panchitaapp.data.source.local.dao.UserDao
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryImplTest {

    private lateinit var repository: AuthRepositoryImpl
    private val mockSupabaseClient: SupabaseClient = mockk()
    private val mockAuth: Auth = mockk(relaxed = true)
    private val mockSessionManager: SessionManager = mockk(relaxed = true)
    private val mockUserDao: UserDao = mockk(relaxed = true)

    @Before
    fun setup() {
        mockkStatic("io.github.jan.supabase.auth.AuthKt")
        mockkStatic("io.github.jan.supabase.postgrest.PostgrestKt")
        
        every { mockSupabaseClient.auth } returns mockAuth
        every { mockSupabaseClient.postgrest } returns mockk(relaxed = true)

        repository = AuthRepositoryImpl(
            supabaseClient = mockSupabaseClient,
            sessionManager = mockSessionManager,
            userDao = mockUserDao,
            firebaseAuth = mockk(relaxed = true),
            firestore = mockk(relaxed = true)
        )
    }

    @After
    fun tearDown() {
        unmockkStatic("io.github.jan.supabase.auth.AuthKt")
        unmockkStatic("io.github.jan.supabase.postgrest.PostgrestKt")
    }

    @Test
    fun `isUserLoggedIn should return true when currentUser is not null`() = runTest {
        every { mockAuth.sessionStatus } returns kotlinx.coroutines.flow.MutableStateFlow(io.github.jan.supabase.auth.status.SessionStatus.Authenticated(mockk()))
        assertTrue(repository.isUserLoggedIn())
    }

    @Test
    fun `isUserLoggedIn should return false when currentUser is null`() = runTest {
        every { mockAuth.sessionStatus } returns kotlinx.coroutines.flow.MutableStateFlow(io.github.jan.supabase.auth.status.SessionStatus.NotAuthenticated)
        assertFalse(repository.isUserLoggedIn())
    }

    @Test
    fun `signOut should clearSession`() {
        repository.signOut()
        verify(exactly = 1) { mockSessionManager.clearSession() }
    }
}