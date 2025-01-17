package com.pinao.panchitaapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "ticket",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["idUser"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["idClient"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    ignoredColumns = ["details"]
)
data class TicketEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "total")
    val total: Double,
    @ColumnInfo(name = "state")
    val state : String,
    @ColumnInfo(name = "idUser")
    val idUser: Int,
    @ColumnInfo(name = "idClient")
    val idClient: Int
){
    var details: List<DetailTicketEntity> = emptyList()
}
