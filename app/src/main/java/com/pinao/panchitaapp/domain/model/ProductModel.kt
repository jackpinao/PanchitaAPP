package com.pinao.panchitaapp.domain.model

data class ProductModel(
    val id: Int = System.currentTimeMillis().hashCode(),
    val idCategory: Int = 0,
    val idDetailTicketEntity : Int = 0,
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val stock: Double = 0.0,
    val code: String = "",
    val image: String = "",
)
