package com.pinao.panchitaapp.domain.model

import java.util.UUID

data class UserModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val password: String,
    val active: Boolean
)
