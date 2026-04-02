package com.pinao.panchitaapp.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "client",
    indices = [
        Index(value = ["numDoc"], unique = true)
    ]
)
data class ClientEntity(
    @PrimaryKey
    @ColumnInfo(name = "client_id")
    val clientId: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "numDoc")
    val numDoc: String,
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    @ColumnInfo(name = "dateCreate", defaultValue = "CURRENT_TIMESTAMP")
    val dateCreate: String,
)
