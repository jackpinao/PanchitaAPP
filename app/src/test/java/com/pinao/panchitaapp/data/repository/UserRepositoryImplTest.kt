package com.pinao.panchitaapp.data.repository

import app.cash.turbine.test
import com.pinao.panchitaapp.data.local.dao.UserDao
import com.pinao.panchitaapp.data.local.entity.UserEntity
import com.pinao.panchitaapp.domain.model.UserModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryImplTest {

    private lateinit var repository: UserRepositoryImpl
    private val mockDao: UserDao = mockk()

    @Before
    fun setup() {
        repository = UserRepositoryImpl(mockDao)
    }

    @Test
    fun `save should insert when userId is empty`() = runTest {
        val model = UserModel(userId = "", storeId = "s1", name = "New User", email = "new@test.com", password = "p", role = "USER", active = true)
        coEvery { mockDao.insert(any()) } returns 10L

        val result = repository.save(model)

        assertEquals(10, result)
        coVerify(exactly = 1) { mockDao.insert(withArg { assertEquals("", it.userId) }) }
    }

    @Test
    fun `save should update when userId is not empty`() = runTest {
        val model = UserModel(userId = "u1", storeId = "s1", name = "Old User", email = "old@test.com", password = "p", role = "USER", active = true)
        coEvery { mockDao.update(any()) } returns 5

        val result = repository.save(model)

        assertEquals(5, result)
        coVerify(exactly = 1) { mockDao.update(withArg { assertEquals("u1", it.userId) }) }
    }

    @Test
    fun `delete should call delete on Dao`() = runTest {
        val model = UserModel(userId = "u1", storeId = "s1", name = "User", email = "u@test.com", password = "p", role = "USER", active = true)
        coEvery { mockDao.delete(any()) } returns 1

        val result = repository.delete(model)

        assertEquals(1, result)
        coVerify(exactly = 1) { mockDao.delete(any()) }
    }

    @Test
    fun `getUser should return mapped user if exists`() = runTest {
        val entity = UserEntity("u1", "s1", "User", "u@test.com", "p", "USER", 1)
        coEvery { mockDao.getUser("u@test.com") } returns entity

        val result = repository.getUser("u@test.com")

        assertNotNull(result)
        assertEquals("u1", result?.userId)
        assertEquals("u@test.com", result?.email)
    }

    @Test
    fun `getUser should return null if user does not exist`() = runTest {
        coEvery { mockDao.getUser("wrong@test.com") } returns null

        val result = repository.getUser("wrong@test.com")

        assertNull(result)
    }

    @Test
    fun `listDate should emit mapped list from Dao`() = runTest {
        val entities = listOf(
            UserEntity("u1", "s1", "User One", "u1@test.com", "p", "USER", 1)
        )
        every { mockDao.listForName("User One") } returns flowOf(entities)

        repository.listDate("User One").test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("User One", items[0].name)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
}