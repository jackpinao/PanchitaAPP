package com.pinao.panchitaapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "detail_ticket",
    foreignKeys = [
        ForeignKey(
            entity = TicketEntity::class,
            parentColumns = ["id"],
            childColumns = ["idTicket"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["idProduct"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class DetailTicketEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "price")
    val price: Double,
    @ColumnInfo(name = "import")
    val import: Double,
    @ColumnInfo(name = "amount")
    val amount: Int,
    @ColumnInfo(name = "idTicket")
    val idTicket: Int,
    @ColumnInfo(name = "idProduct")
    val idProduct: Int
)
