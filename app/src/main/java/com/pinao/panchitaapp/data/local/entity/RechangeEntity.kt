package com.pinao.panchitaapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "rechange",
    indices = [
        Index(value = ["date"], unique = true)
    ]
)
data class RechangeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "amount")
    val amount: Int,
    @ColumnInfo(name = "numPhone")
    val numPhone: String
)
