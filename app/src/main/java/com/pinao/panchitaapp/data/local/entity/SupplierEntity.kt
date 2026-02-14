package com.pinao.panchitaapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "Supplier")
data class SupplierEntity(
    @PrimaryKey
    @ColumnInfo(name = "supplier_id")
    val supplierId: String,

    @ColumnInfo(name = "store_id")
    val storeId: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "phone")
    val phone: String? = null,

    @ColumnInfo(name = "email")
    val email: String? = null,

    @ColumnInfo(name = "is_synced", defaultValue = "0")
    val isSynced: Int = 0
)

