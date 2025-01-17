package com.pinao.panchitaapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "product",
    indices = [
        Index(value = ["code"], unique = true)
    ]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "price")
    val price: Double,
    @ColumnInfo(name = "stock")
    val stock: Int,
    @ColumnInfo(name = "code")
    val code: String
)
