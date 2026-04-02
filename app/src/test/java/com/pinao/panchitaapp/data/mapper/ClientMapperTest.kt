package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.source.local.entity.ClientEntity
import com.pinao.panchitaapp.domain.model.ClientModel
import org.junit.Assert.assertEquals
import org.junit.Test

class ClientMapperTest {

    @Test
    fun `toDomain should map ClientEntity to ClientModel correctly`() {
        val entity = ClientEntity(
            clientId = "client_1",
            name = "Juan Perez",
            numDoc = "12345678",
            isActive = true,
            dateCreate = "2023-10-25"
        )

        val model = ClientMapper.toDomain(entity)

        assertEquals(entity.clientId, model.id)
        assertEquals(entity.name, model.name)
        assertEquals(entity.numDoc, model.numDoc)
        assertEquals(entity.isActive, model.active)
        assertEquals(entity.dateCreate, model.dateCreate)
    }

    @Test
    fun `toEntity should map ClientModel to ClientEntity correctly`() {
        val model = ClientModel(
            id = "client_1",
            name = "Juan Perez",
            numDoc = "12345678",
            active = false,
            dateCreate = "2023-10-25"
        )

        val entity = ClientMapper.toEntity(model)

        assertEquals(model.id, entity.clientId)
        assertEquals(model.name, entity.name)
        assertEquals(model.numDoc, entity.numDoc)
        assertEquals(model.active, entity.isActive)
        assertEquals(model.dateCreate, entity.dateCreate)
    }
}