package com.pinao.panchitaapp.data.local.entity

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
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "numDoc")
    val numDoc: String,
    @ColumnInfo(name = "active")
    val active: Boolean
)
