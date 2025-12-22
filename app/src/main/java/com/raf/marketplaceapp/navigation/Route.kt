package com.raf.marketplaceapp.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Route {

    @Serializable
    data object Auth : NavKey

    @Serializable
    data object Settings : NavKey

    @Serializable
    data object Home : NavKey

    @Serializable
    data class Detail(val id: Int) : NavKey
}