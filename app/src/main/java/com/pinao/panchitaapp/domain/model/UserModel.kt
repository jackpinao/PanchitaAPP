package com.pinao.panchitaapp.domain.model

data class UserModel(
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val active: Boolean
)
