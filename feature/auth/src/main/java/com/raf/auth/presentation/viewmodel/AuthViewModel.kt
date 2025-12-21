package com.raf.auth.presentation.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raf.auth.domain.usecase.LoginUseCase
import com.raf.auth.domain.usecase.RegisterUseCase
import com.raf.core.domain.model.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthState())
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    var username by mutableStateOf("")
        private set

    var usernameError by mutableStateOf(false)
        private set

    var email by mutableStateOf("")
        private set

    var emailError by mutableStateOf(false)
        private set

    var password by mutableStateOf("")
        private set

    var showPassword by mutableStateOf(false)
        private set

    var passwordError by mutableStateOf(false)
        private set

    var passwordConfirmation by mutableStateOf("")
        private set

    var showPasswordConfirmation by mutableStateOf(false)
        private set

    var passwordConfirmationError by mutableStateOf(false)
        private set

    fun onUsernameChange(newUsername: String) {
        username = newUsername
        usernameError = username.isBlank()
    }

    fun onEmailChange(newEmail: String) {
        email = newEmail
        emailError = email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
        passwordError = password.isBlank() || password.length < 8
        passwordConfirmationError =
            passwordConfirmation.isNotBlank() && passwordConfirmation != password
    }

    fun onPasswordConfirmationChange(newPasswordConfirmation: String) {
        passwordConfirmation = newPasswordConfirmation
        passwordConfirmationError =
            passwordConfirmation.isBlank() || passwordConfirmation != password
    }

    fun toggleShowPassword() {
        showPassword = !showPassword
    }

    fun toggleShowPasswordConfirmation() {
        showPasswordConfirmation = !showPasswordConfirmation
    }

    fun toggleLoginState(value: Boolean? = null) {
        _uiState.update {
            it.copy(isLoginState = value ?: !it.isLoginState)
        }
        usernameError = false
        passwordError = false
        passwordConfirmationError = false
    }

    fun login() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = loginUseCase(username, password)) {
                is ApiResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }

                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, isLoginSuccess = username)
                    }
                    Log.d(TAG, "Login Success: ${result.data}")
                }

                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                    Log.e(TAG, "Login Error: ${result.message}")
                    showUiMessage(result.message)
                }
            }
        }
    }

    fun register(onRegisterSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = registerUseCase.invoke(username, email, passwordConfirmation)) {
                is ApiResult.Success -> {
                    onRegisterSuccess()
                    username = ""
                    email = ""
                    password = ""
                    passwordConfirmation = ""
                    Log.d(TAG, "Register Success: ${result.data}")
                }

                is ApiResult.Error -> {
                    Log.e(TAG, "Register Error: ${result.message}")
                    showUiMessage(result.message)
                }

                is ApiResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    var messageJob: Job? = null
    fun showUiMessage(message: String) {
        messageJob?.cancel()
        messageJob = viewModelScope.launch {
            _uiState.update { it.copy(uiMessage = message) }
            delay(1500)
            _uiState.update { it.copy(uiMessage = null) }
        }
    }

    fun randomUserLogin() {
        val userLogin = AuthUserLogin.listAuthUserLogin.random()
        username = userLogin.username
        password = userLogin.password
    }

    companion object {
        private const val TAG = "AuthViewModel"
    }
}