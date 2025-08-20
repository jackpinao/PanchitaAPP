package com.pinao.panchitaapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "product",
//    foreignKeys = [
//        ForeignKey(
//            entity = CategoryEntity::class,
//            parentColumns = ["id"],
//            childColumns = ["idCategory"],
//            onDelete = ForeignKey.NO_ACTION,
//        ),
//        ForeignKey(
//            entity = DetailTicketEntity::class,
//            parentColumns = ["id"],
//            childColumns = ["idDetailTicketEntity"],
//            onDelete = ForeignKey.NO_ACTION,
//        )
//    ],
    indices = [
        Index(value = ["code"], unique = true)
    ]
)
data class ProductsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "idCategory")
    val idCategory: Int,
    @ColumnInfo(name = "idDetailTicketEntity")
    val idDetailTicketEntity : Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "price")
    val price: Double,
    @ColumnInfo(name = "stock")
    val stock: Double,
    @ColumnInfo(name = "code")
    val code: String,
    @ColumnInfo(name = "image")
    val image: String
)
