package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.source.local.entity.ProductsEntity
import com.pinao.panchitaapp.data.source.remote.dto.FirebaseProductDto
import com.pinao.panchitaapp.data.source.remote.dto.SupabaseProductDto
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.utils.DateUtils

object ProductMapper {
    fun toDomain(entity: ProductsEntity): ProductModel {
        return ProductModel(
            productId = entity.productId,
            storeId = entity.storeId,
            categoryId = entity.categoryId,
            detailTicketEntityId = entity.detailTicketEntityId,
            name = entity.name,
            priceSell = entity.priceSell,
            priceBuy = entity.priceBuy,
            priceExcludingIGV = entity.priceExcludingIGV,
            revenue = entity.revenue,
            description = entity.description,
            image = entity.image,
            stockQuantity = entity.stockQuantity,
            barcode = entity.barcode,
            lastUpdated = entity.lastUpdated,
            stockMin = entity.stockMin,
            expiryDate = entity.expiryDate,
            isSynced = entity.isSynced == 1,
            isDeleted = entity.isDeleted == 1
        )
    }

    fun toDatabase(model: ProductModel): ProductsEntity {
        return ProductsEntity(
            productId = model.productId,
            storeId = model.storeId,
            categoryId = model.categoryId,
            detailTicketEntityId = model.detailTicketEntityId.toString(),
            name = model.name,
            priceSell = model.priceSell,
            priceBuy = model.priceBuy,
            priceExcludingIGV = model.priceExcludingIGV,
            revenue = model.revenue,
            description = model.description,
            image = model.image,
            stockQuantity = model.stockQuantity,
            barcode = model.barcode,
            lastUpdated = model.lastUpdated,
            stockMin = model.stockMin,
            expiryDate = model.expiryDate,
            isSynced = if (model.isSynced) 1 else 0,
            isDeleted = if (model.isDeleted) 1 else 0
        )
    }

    fun toDomain(dto: FirebaseProductDto): ProductModel {
        return ProductModel(
            productId = dto.productId,
            storeId = dto.storeId,
            categoryId = dto.categoryId,
            name = dto.name,
            description = dto.description,
            priceBuy = dto.priceBuy,
            priceSell = dto.priceSell,
            priceExcludingIGV = dto.priceExcludingIGV,
            stockQuantity = dto.stockQuantity,
            stockMin = dto.stockMin,
            barcode = dto.barcode,
            image = dto.image,
            lastUpdated = dto.lastUpdated,
            isDeleted = dto.isDeleted,
            isSynced = true
        )
    }

    fun toFirebaseDto(model: ProductModel): FirebaseProductDto {
        return FirebaseProductDto(
            productId = model.productId,
            storeId = model.storeId,
            categoryId = model.categoryId,
            name = model.name,
            description = model.description,
            priceBuy = model.priceBuy,
            priceSell = model.priceSell,
            priceExcludingIGV = model.priceExcludingIGV,
            stockQuantity = model.stockQuantity,
            stockMin = model.stockMin,
            barcode = model.barcode,
            image = model.image,
            lastUpdated = model.lastUpdated,
            isDeleted = model.isDeleted
        )
    }

    fun toDomain(dto: SupabaseProductDto): ProductModel {
        return ProductModel(
            productId = dto.id,
            storeId = dto.tenantId,
            categoryId = dto.categoryId ?: "",
            name = dto.name,
            description = dto.description ?: "",
            priceBuy = dto.purchasePrice,
            priceSell = dto.salePrice,
            priceExcludingIGV = dto.salePrice / 1.18, // Approximation if not stored
            revenue = dto.revenue,
            stockQuantity = dto.currentStock,
            stockMin = dto.minStock,
            barcode = dto.barcode ?: "",
            image = dto.imageUrl ?: "",
            lastUpdated = dto.updatedAt ?: "",
            expiryDate = DateUtils.toDisplayFormat(dto.expiryDate),
            isSynced = true,
            isDeleted = !dto.isActive
        )
    }

    fun toSupabaseDto(model: ProductModel): SupabaseProductDto {
        return SupabaseProductDto(
            id = model.productId,
            tenantId = model.storeId,
            categoryId = model.categoryId.takeIf { it.isNotEmpty() },
            name = model.name,
            description = model.description.takeIf { it.isNotEmpty() },
            purchasePrice = model.priceBuy,
            salePrice = model.priceSell,
            revenue = model.revenue,
            currentStock = model.stockQuantity,
            minStock = model.stockMin,
            barcode = model.barcode.takeIf { it.isNotEmpty() },
            imageUrl = model.image.takeIf { it.isNotEmpty() },
            expiryDate = DateUtils.toDatabaseFormat(model.expiryDate),
            isActive = !model.isDeleted
        )
    }
}
