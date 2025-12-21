package com.raf.marketplaceapp.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.NavigationEvent
import com.raf.auth.presentation.AuthScreen
import com.raf.marketplace.presentation.detail.DetailScreen
import com.raf.marketplace.presentation.list.HomeScreen
import com.raf.settings.presentation.SettingsScreen

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    startDestination: NavKey = Route.Auth,
) {
    val backStack = rememberNavBackStack(startDestination)
    val strategy = rememberListDetailSceneStrategy<Any>()

    val localMotionScheme = MaterialTheme.LocalMotionScheme.current
    val animationSpecFloat = localMotionScheme.slowSpatialSpec<Float>()
    val animationSpecIntOffset = localMotionScheme.slowSpatialSpec<IntOffset>()

    SharedTransitionLayout(
        modifier = Modifier.fillMaxSize(),
    ) {
        NavDisplay(
            modifier = modifier.fillMaxSize(),
            backStack = backStack,
            sceneStrategy = strategy,
            onBack = {
                backStack.removeLastOrNull()
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            transitionSpec = {
                fadeIn(animationSpec = animationSpecFloat) +
                        slideInHorizontally(animationSpec = animationSpecIntOffset) { it } +
                        scaleIn(
                            animationSpec = animationSpecFloat,
                            initialScale = 0.75f
                        ) togetherWith
                        scaleOut(animationSpec = animationSpecFloat, targetScale = 1.05f)
            },
            popTransitionSpec = {
                scaleIn(initialScale = 1.05f) togetherWith
                        scaleOut(targetScale = 0.5f, animationSpec = animationSpecFloat) +
                        slideOutHorizontally(animationSpec = animationSpecIntOffset) { it }
            },
            predictivePopTransitionSpec = { swipeNavigation ->
                val isSwipeFromRight = swipeNavigation == NavigationEvent.EDGE_RIGHT
                scaleIn(initialScale = 1.05f) togetherWith
                        scaleOut(targetScale = 0.5f, animationSpec = animationSpecFloat) +
                        slideOutHorizontally(animationSpec = animationSpecIntOffset) {
                            val offset = it / 2
                            if (isSwipeFromRight) -offset else offset
                        }
            },
            entryProvider = entryProvider {
                entry<Route.Settings> {
                    val isSettingsScreenVisible = backStack.lastOrNull() is Route.Settings

                    AnimatedVisibility(visible = isSettingsScreenVisible) {
                        SettingsScreen(
                            modifier = Modifier
                                .sharedBounds(
                                    sharedContentState = rememberSharedContentState("transition_settings_screen_container_key"),
                                    animatedVisibilityScope = this@AnimatedVisibility
                                ),
                            onBack = {
                                if (backStack.size > 1) {
                                    backStack.removeLastOrNull()
                                }
                            }
                        )
                    }
                }

                entry<Route.Auth> {
                    val isSettingsScreenVisible = backStack.contains(Route.Settings)

                    AuthScreen(
                        isSettingsScreenVisible = isSettingsScreenVisible,
                        onNavigateToSettings = {
                            backStack.add(Route.Settings)
                        },
                        onLoginSuccess = {
                            backStack.clear()
                            backStack.add(Route.Home)
                        }
                    )
                }

                entry<Route.Home> {
                    HomeScreen()
                }

                entry<Route.Detail> {
                    DetailScreen()
                }
            }
        )
    }
}