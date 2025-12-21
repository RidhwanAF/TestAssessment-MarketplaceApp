package com.raf.auth.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val id: Int,
    val username: String,
    val email: String,
    val password: String,
)
