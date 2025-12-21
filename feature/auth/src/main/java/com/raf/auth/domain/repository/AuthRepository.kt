package com.raf.auth.domain.repository

import com.raf.core.domain.model.ApiResult

interface AuthRepository {
    suspend fun login(username: String, password: String): ApiResult<String>
    suspend fun register(username: String, email: String, password: String): ApiResult<String>
}