package com.raf.profile.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raf.core.domain.model.ApiResult
import com.raf.core.domain.usecase.DeleteAllItemCartUseCase
import com.raf.core.domain.usecase.GetAuthTokenUseCase
import com.raf.core.domain.usecase.GetUserIdUseCase
import com.raf.core.domain.usecase.GetUserProfileUseCase
import com.raf.core.domain.usecase.LogoutUseCase
import com.raf.profile.domain.usecase.DeleteUserProfileByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getAuthTokenUseCase: GetAuthTokenUseCase,
    private val getUserIdUseCase: GetUserIdUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val deleteAllItemCartUseCase: DeleteAllItemCartUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val deleteUserProfileByIdUseCase: DeleteUserProfileByIdUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getUserProfile()
    }

    private fun getUserProfile() {
        viewModelScope.launch {
            Log.d(TAG, "getUserProfile: called")
            _uiState.update { it.copy(isLoading = true) }
            val token = getAuthTokenUseCase().first() ?: ""
            val userId = getUserIdUseCase()
            if (userId == null) {
                _uiState.update { it.copy(isLoading = false) }
                showUiMessage("User not found")
                return@launch
            }

            when (val result = getUserProfileUseCase(token, userId)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, profile = result.data) }
                    Log.d(TAG, "getUserProfile: ${result.data}")
                }

                is ApiResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    Log.d(TAG, "getUserProfile: ${result.message}")
                    showUiMessage(result.message)
                }

                is ApiResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(logOuting = true) }
            val userId = getUserIdUseCase()
            if (userId != null) {
                deleteUserProfileByIdUseCase(userId)
            }
            deleteAllItemCartUseCase()
            logoutUseCase()
            _uiState.update { it.copy(logOuting = false) }
            onLoggedOut()
        }
    }

    var job: Job? = null
    fun showUiMessage(message: String) {
        if (message.isEmpty()) return
        _uiState.update {
            it.copy(uiMessage = null)
        }
        job?.cancel()
        job = viewModelScope.launch {
            _uiState.update {
                it.copy(uiMessage = message)
            }
            delay(1500)
            _uiState.update {
                it.copy(uiMessage = null)
            }
        }
    }

    companion object {
        const val TAG = "ProfileViewModel"
    }
}