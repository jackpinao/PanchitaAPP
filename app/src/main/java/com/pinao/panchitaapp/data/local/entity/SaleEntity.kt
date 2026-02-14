package com.pinao.panchitaapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sale",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
//        ForeignKey(
//            entity = ClientEntity::class,
//            parentColumns = ["client_id"],
//            childColumns = ["client_id"],
//            onDelete = ForeignKey.NO_ACTION
//        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["client_id"]),
        Index(value = ["sale_id"], unique = true)
    ],
)
data class SaleEntity(
    @PrimaryKey
    @ColumnInfo(name = "sale_id")
    val saleId: String,
    @ColumnInfo(name = "store_id")
    val storeId: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "client_id")
    val clientId: String,
    @ColumnInfo(name = "sale_date")
    val saleDate: String,
    @ColumnInfo(name = "total_amount")
    val totalAmount: Double,
    @ColumnInfo(name = "payment_type")
    val paymentType: String,
    @ColumnInfo(name = "is_synced")
    val isSynced: Int = 0
)
