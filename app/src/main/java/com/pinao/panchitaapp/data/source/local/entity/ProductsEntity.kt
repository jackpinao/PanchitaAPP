package com.pinao.panchitaapp.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "product",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["category_id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.RESTRICT,
        )
    ],
    indices = [
        Index(value = ["barcode"], unique = true),
        Index(value = ["category_id"])
    ]
)
data class ProductsEntity(
    @PrimaryKey
    @ColumnInfo(name = "product_id")
    val productId: String,
    @ColumnInfo(name = "store_id")
    val storeId: String,
    @ColumnInfo(name = "category_id")
    val categoryId: String,
    @ColumnInfo(name = "detailTicketEntity_id", defaultValue = "")
    val detailTicketEntityId: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "price_Buy")
    val priceBuy: Double,// precio de compra
    @ColumnInfo(name = "price_sell")
    val priceSell: Double, //precio de venta
    @ColumnInfo(name = "price_excluding_igv")
    val priceExcludingIGV: Double, //precio sin igv
    @ColumnInfo(name = "revenue", defaultValue = "0.0")
    val revenue: Double,
    @ColumnInfo(name = "stock_quantity")
    val stockQuantity: Double,
    @ColumnInfo(name = "stock_min", defaultValue = "5.0")
    val stockMin: Double,
    @ColumnInfo(name = "barcode")
    val barcode: String,
    @ColumnInfo(name = "image")
    val image: String,
    @ColumnInfo(name = "last_updated")
    val lastUpdated: String,
    @ColumnInfo(name = "expiry_date")
    val expiryDate: String? = null,
    @ColumnInfo(name = "is_synced", defaultValue = "0")
    val isSynced: Int = 0,
    @ColumnInfo(name = "is_deleted", defaultValue = "0")
    val isDeleted: Int = 0
)
