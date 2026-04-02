package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.source.local.entity.UserEntity
import com.pinao.panchitaapp.domain.model.UserModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserMapperTest {

    @Test
    fun `toDomain should map UserEntity to UserModel correctly`() {
        val entity = UserEntity(
            userId = "u1",
            storeId = "s1",
            name = "Admin User",
            email = "admin@test.com",
            password = "secure_pass",
            role = "ADMIN",
            isActive = 1
        )

        val model = UserMapper.toDomain(entity)

        assertEquals(entity.userId, model.userId)
        assertEquals(entity.storeId, model.storeId)
        assertEquals(entity.name, model.name)
        assertEquals(entity.email, model.email)
        assertEquals(entity.password, model.password)
        assertEquals(entity.role, model.role)
        assertTrue(model.active)
    }

    @Test
    fun `toDomain should map isActive correctly when 0`() {
        val entity = UserEntity(
            userId = "u1", storeId = "s1", name = "Test", email = "t@t.com",
            password = "p", role = "USER", isActive = 0
        )
        val model = UserMapper.toDomain(entity)
        assertFalse(model.active)
    }

    @Test
    fun `toDatabase should map UserModel to UserEntity correctly`() {
        val model = UserModel(
            userId = "u1",
            storeId = "s1",
            name = "Admin User",
            email = "admin@test.com",
            password = "secure_pass",
            role = "ADMIN",
            active = true
        )

        val entity = UserMapper.toDatabase(model)

        assertEquals(model.userId, entity.userId)
        assertEquals(model.storeId, entity.storeId)
        assertEquals(model.name, entity.name)
        assertEquals(model.email, entity.email)
        assertEquals(model.password, entity.password)
        assertEquals(model.role, entity.role)
        assertEquals(1, entity.isActive)
    }

    @Test
    fun `toDatabase should map isActive correctly when false`() {
        val model = UserModel(
            userId = "u1", storeId = "s1", name = "Test", email = "t@t.com",
            password = "p", role = "USER", active = false
        )
        val entity = UserMapper.toDatabase(model)
        assertEquals(0, entity.isActive)
    }
}