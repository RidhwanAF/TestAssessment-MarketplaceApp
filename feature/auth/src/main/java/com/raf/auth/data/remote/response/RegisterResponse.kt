package com.raf.auth.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val id: Int,
    val username: String,
    val email: String,
    val password: String,
)
