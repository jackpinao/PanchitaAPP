package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.local.entity.SaleDetailEntity
import com.pinao.panchitaapp.domain.model.SaleDetailModel
import org.junit.Assert.assertEquals
import org.junit.Test

class SaleDetailMapperTest {

    @Test
    fun `toDomain should map SaleDetailEntity to SaleDetailModel correctly`() {
        val entity = SaleDetailEntity(
            saleDetailId = "detail_1",
            saleId = "sale_1",
            productId = "prod_1",
            quantity = 2.0,
            priceAtSale = 15.0,
            subtotal = 30.0
        )

        val model = SaleDetailMapper.toDomain(entity)

        assertEquals(entity.saleDetailId, model.saleDetailId)
        assertEquals(entity.saleId, model.saleId)
        assertEquals(entity.productId, model.productId)
        assertEquals(entity.quantity, model.quantity, 0.0)
        assertEquals(entity.priceAtSale, model.priceAtSale, 0.0)
        assertEquals(entity.subtotal, model.subtotal, 0.0)
    }

    @Test
    fun `toDatabase should map SaleDetailModel to SaleDetailEntity correctly`() {
        val model = SaleDetailModel(
            saleDetailId = "detail_1",
            saleId = "sale_1",
            productId = "prod_1",
            quantity = 2.0,
            priceAtSale = 15.0,
            subtotal = 30.0
        )

        val entity = SaleDetailMapper.toDatabase(model)

        assertEquals(model.saleDetailId, entity.saleDetailId)
        assertEquals(model.saleId, entity.saleId)
        assertEquals(model.productId, entity.productId)
        assertEquals(model.quantity, entity.quantity, 0.0)
        assertEquals(model.priceAtSale, entity.priceAtSale, 0.0)
        assertEquals(model.subtotal, entity.subtotal, 0.0)
    }
}