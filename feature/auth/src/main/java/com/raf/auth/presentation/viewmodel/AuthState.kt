package com.raf.auth.presentation.viewmodel

data class AuthState(
    val isLoading: Boolean = false,
    val isLoginState: Boolean = true,
    val isLoginSuccess: String? = null,
    val uiMessage: String? = null,
)

data class AuthUserLogin(
    val username: String,
    val password: String,
) {
    companion object {
        val listAuthUserLogin = listOf(
            AuthUserLogin("johnd", "m38rmF$"),
            AuthUserLogin("david", "morrison"),
            AuthUserLogin("kevinryan", "kev02937@"),
            AuthUserLogin("donero", "ewedon"),
            AuthUserLogin("derek", "jklg*_56"),
            AuthUserLogin("david_r", "3478*#54"),
            AuthUserLogin("snyder", "f238&@*$"),
            AuthUserLogin("hopkins", $$"William56$hj"),
            AuthUserLogin("kate_h", "kfejk@*_"),
            AuthUserLogin("jimmie_k", "klein*#%*"),
        )
    }
}