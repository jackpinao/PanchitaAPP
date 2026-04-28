package com.pinao.panchitaapp.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Entidad que representa la tabla StockEntry en la base de datos local.
 */
@Entity(
    tableName = "StockEntry",
    foreignKeys = [
        ForeignKey(
            entity = SupplierEntity::class,
            parentColumns = ["supplier_id"],
            childColumns = ["supplier_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class StockEntryEntity(
    @PrimaryKey
    @ColumnInfo(name = "entry_id")
    val entryId: String,

    @ColumnInfo(name = "store_id")
    val storeId: String,

    @ColumnInfo(name = "supplier_id", index = true)
    val supplierId: String,

    @ColumnInfo(name = "entry_date")
    val entryDate: Long,

    @ColumnInfo(name = "total_cost")
    val totalCost: Double,

    @ColumnInfo(name = "document_number")
    val documentNumber: String?,

    @ColumnInfo(name = "is_synced")
    val isSynced: Int = 0,

    @ColumnInfo(name = "product_id", defaultValue = "")
    val productId: String = "",

    @ColumnInfo(name = "quantity_added", defaultValue = "0.0")
    val quantityAdded: Double = 0.0,

    @ColumnInfo(name = "unit_cost_ppp", defaultValue = "0.0")
    val unitCostPpp: Double = 0.0,

    @ColumnInfo(name = "movement_type", defaultValue = "ENTRY")
    val movementType: String = "ENTRY"
)