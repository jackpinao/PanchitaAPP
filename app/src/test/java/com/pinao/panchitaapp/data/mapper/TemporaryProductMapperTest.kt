package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.source.local.entity.TemporaryProductEntity
import com.pinao.panchitaapp.domain.model.TemporaryProductModel
import org.junit.Assert.assertEquals
import org.junit.Test

class TemporaryProductMapperTest {

    @Test
    fun `toDomain should map TemporaryProductEntity to TemporaryProductModel correctly`() {
        val entity = TemporaryProductEntity(
            id = "temp_1",
            productId = "prod_1",
            name = "Inca Kola",
            code = "12345",
            price = 5.0,
            quantity = 2.0,
            priceExcludingIGV = 4.2
        )

        val model = TemporaryProductMapper.toDomain(entity)

        assertEquals(entity.id, model.id)
        assertEquals(entity.productId, model.productId)
        assertEquals(entity.name, model.name)
        assertEquals(entity.code, model.code)
        assertEquals(entity.price, model.price, 0.0)
        assertEquals(entity.quantity, model.quantity, 0.0)
        assertEquals(entity.priceExcludingIGV, model.priceExcludingIGV, 0.0)
    }

    @Test
    fun `toEntity should map TemporaryProductModel to TemporaryProductEntity correctly`() {
        val model = TemporaryProductModel(
            id = "temp_1",
            productId = "prod_1",
            name = "Inca Kola",
            code = "12345",
            price = 5.0,
            quantity = 2.0,
            priceExcludingIGV = 4.2
        )

        val entity = TemporaryProductMapper.toEntity(model)

        assertEquals(model.id, entity.id)
        assertEquals(model.productId, entity.productId)
        assertEquals(model.name, entity.name)
        assertEquals(model.code, entity.code)
        assertEquals(model.price, entity.price, 0.0)
        assertEquals(model.quantity, entity.quantity, 0.0)
        assertEquals(model.priceExcludingIGV, entity.priceExcludingIGV, 0.0)
    }
}