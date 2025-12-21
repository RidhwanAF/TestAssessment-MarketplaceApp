package com.raf.marketplace.presentation.list

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.raf.core.presentation.components.CustomIconButtonShapes
import com.raf.marketplace.R
import com.raf.marketplace.presentation.list.components.ProductItem
import com.raf.marketplace.presentation.list.components.ProductItemLoading
import com.raf.marketplace.presentation.list.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedTransitionScope.HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    isSettingsScreenVisible: Boolean = false,
    onNavigateToSettings: () -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Error Message
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.choose_your_best_product),
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    actions = {
                        AnimatedContent(
                            targetState = !isSettingsScreenVisible,
                        ) { targetState ->
                            if (targetState) {
                                TooltipBox(
                                    positionProvider =
                                        TooltipDefaults.rememberTooltipPositionProvider(
                                            TooltipAnchorPosition.Below
                                        ),
                                    tooltip = {
                                        PlainTooltip {
                                            Text(text = stringResource(R.string.settings))
                                        }
                                    },
                                    state = rememberTooltipState(),
                                    modifier = Modifier
                                        .sharedBounds(
                                            sharedContentState = rememberSharedContentState("transition_settings_screen_container_key"),
                                            animatedVisibilityScope = this
                                        )
                                ) {
                                    IconButton(
                                        shapes = CustomIconButtonShapes(),
                                        onClick = onNavigateToSettings
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = stringResource(R.string.settings)
                                        )
                                    }
                                }
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            },
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { innerPadding ->
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(175.dp),
                contentPadding = innerPadding,
                modifier = Modifier.fillMaxSize()
            ) {
                if (uiState.isLoading && uiState.products.isEmpty()) {
                    items(10) {
                        ProductItemLoading()
                    }
                    return@LazyVerticalStaggeredGrid
                }
                items(
                    items = uiState.products,
                    key = { product -> product.id }
                ) { product ->
                    ProductItem(
                        product = product,
                        onClicked = {

                        },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}