package com.pinao.panchitaapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "category",
//    foreignKeys = [
//        ForeignKey(
//            entity = ProductsEntity::class,
//            parentColumns = ["id"],
//            childColumns = ["idProduct"],
//            onDelete = ForeignKey.NO_ACTION
//        )
//    ],
    indices = [
        Index(value = ["id"], unique = true)
    ]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
//    @ColumnInfo(name = "idProduct")
//    val idProduct: Int,
    @ColumnInfo(name = "name")
    val name: String,
)
