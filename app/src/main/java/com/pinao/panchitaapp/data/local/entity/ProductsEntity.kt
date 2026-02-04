package com.pinao.panchitaapp.data.local.entity

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
            parentColumns = ["id"],
            childColumns = ["idCategory"],
            onDelete = ForeignKey.RESTRICT,
        )
    ],
    indices = [
        Index(value = ["code"] , unique = true),
        Index(value = ["idCategory"])
    ]
)
data class ProductsEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "idCategory")
    val idCategory: String,
    @ColumnInfo(name = "idDetailTicketEntity", defaultValue = "")
    val idDetailTicketEntity: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "sellingPrice")
    val sellingPrice: Double, //precio de venta
    @ColumnInfo(name = "purchasePrice")
    val purchasePrice: Double,// precio de compra
    @ColumnInfo(name = "priceExcludingIGV")
    val priceExcludingIGV: Double, //precio sin igv
    @ColumnInfo(name = "stock")
    val stock: Double,
    @ColumnInfo(name = "code")
    val code: String,
    @ColumnInfo(name = "image")
    val image: String
)
