package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.local.entity.RechangeEntity
import com.pinao.panchitaapp.domain.model.RechangeModel
import org.junit.Assert.assertEquals
import org.junit.Test

class RechangeMapperTest {

    @Test
    fun `toDomain should map RechangeEntity to RechangeModel correctly`() {
        val entity = RechangeEntity(
            id = "rech_1",
            date = "2023-11-01",
            amount = 50,
            numPhone = "987654321"
        )

        val model = RechangeMapper.toDomain(entity)

        assertEquals(entity.id, model.id)
        assertEquals(entity.date, model.date)
        assertEquals(entity.amount, model.amount)
        assertEquals(entity.numPhone, model.numPhone)
    }

    @Test
    fun `toDatabase should map RechangeModel to RechangeEntity correctly`() {
        val model = RechangeModel(
            id = "rech_1",
            date = "2023-11-01",
            amount = 50,
            numPhone = "987654321"
        )

        val entity = RechangeMapper.toDatabase(model)

        assertEquals(model.id, entity.id)
        assertEquals(model.date, entity.date)
        assertEquals(model.amount, entity.amount)
        assertEquals(model.numPhone, entity.numPhone)
    }
}