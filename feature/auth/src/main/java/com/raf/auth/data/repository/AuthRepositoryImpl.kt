package com.raf.auth.data.repository

import android.util.Log
import com.google.gson.Gson
import com.raf.auth.data.local.AuthDataStore
import com.raf.auth.data.remote.AuthApiService
import com.raf.auth.data.remote.model.LoginRequest
import com.raf.auth.data.remote.model.RegisterRequest
import com.raf.auth.domain.repository.AuthRepository
import com.raf.core.data.utility.EncryptionManager
import com.raf.core.domain.contract.AuthProvider
import com.raf.core.domain.model.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val authDataStore: AuthDataStore,
) : AuthRepository, AuthProvider {

    private companion object {
        private const val TAG = "AuthRepositoryImpl"
    }

    override suspend fun login(
        username: String,
        password: String,
    ): ApiResult<String> {
        return try {
            val loginRequest = LoginRequest(
                username = username,
                password = password,
            )
            val result = apiService.login(loginRequest)
            if (result.isSuccessful) {
                val token = result.body()?.token ?: return ApiResult.Error(result.message())

                val encryptedToken =
                    EncryptionManager.encrypt(token) ?: return ApiResult.Error(result.message())
                authDataStore.saveSessionToken(encryptedToken)
                ApiResult.Success(token)
            } else {
                val errorMessage = result.errorBody()?.string()?.takeIf { it.isNotEmpty() }
                    ?: result.message()
                ApiResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to login", e)
            ApiResult.Error(e.localizedMessage ?: "Unknown Error")
        }
    }

    override suspend fun register(
        username: String,
        email: String,
        password: String,
    ): ApiResult<String> {
        return try {
            val registerRequest = RegisterRequest(
                id = 0,
                username = username,
                email = email,
                password = password,
            )
            val result = apiService.register(registerRequest)
            if (result.isSuccessful) {
                return ApiResult.Success(result.body()?.id.toString())
            } else {
                val errorMessage = result.errorBody()?.string()?.takeIf { it.isNotEmpty() }
                    ?: result.message()
                ApiResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register", e)
            return ApiResult.Error(e.localizedMessage ?: "Unknown Error")
        }
    }

    override fun getAuthToken(): Flow<String?> {
        return try {
            authDataStore.getSessionToken().map { encryptedToken ->
                if (encryptedToken == null) return@map null
                EncryptionManager.decrypt(encryptedToken)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get auth token", e)
            flowOf(null)
        }
    }

    override suspend fun logout() {
        try {
            authDataStore.clearSessionToken()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to logout", e)
        }
    }
}