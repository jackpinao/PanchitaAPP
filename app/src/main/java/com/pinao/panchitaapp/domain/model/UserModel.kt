package com.pinao.panchitaapp.domain.model

import java.util.UUID

data class UserModel(
    val userId: String = UUID.randomUUID().toString(),
    val storeId: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val role: String = "",
    val active: Boolean = true
)
