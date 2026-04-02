package com.pinao.panchitaapp.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sale_detail",
    foreignKeys = [
        ForeignKey(
            entity = SaleEntity::class,
            parentColumns = ["sale_id"],
            childColumns = ["sale_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductsEntity::class,
            parentColumns = ["product_id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["sale_id"]),
        Index(value = ["product_id"])
    ]
)
data class SaleDetailEntity(
    @PrimaryKey
    @ColumnInfo(name = "sale_detail_id")
    val saleDetailId: String,
    @ColumnInfo(name = "sale_id")
    val saleId: String,
    @ColumnInfo(name = "product_id")
    val productId: String,
    @ColumnInfo(name = "quantity")
    val quantity: Double,
    @ColumnInfo(name = "price_at_sale")
    val priceAtSale: Double,
    @ColumnInfo(name = "subtotal")
    val subtotal: Double,
)
