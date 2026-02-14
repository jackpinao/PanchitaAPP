package com.pinao.panchitaapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "brand",
    indices = [
        Index(value = ["brand_id"], unique = true)
    ]
)
data class BrandEntity(
    @PrimaryKey
    @ColumnInfo(name = "brand_id")
    val brandId: String,
    @ColumnInfo(name = "store_id")
    val storeId: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "is_synced", defaultValue = "0")
    val isSynced: Int = 0
)
