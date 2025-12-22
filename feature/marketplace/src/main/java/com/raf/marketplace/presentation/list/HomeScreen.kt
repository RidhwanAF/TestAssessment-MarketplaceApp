package com.raf.marketplace.presentation.list

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarExitDirection.Companion.Bottom
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.raf.marketplace.R
import com.raf.marketplace.presentation.list.components.ProductCategory
import com.raf.marketplace.presentation.list.components.ProductFilterToolbar
import com.raf.marketplace.presentation.list.components.ProductItem
import com.raf.marketplace.presentation.list.components.ProductItemLoading
import com.raf.marketplace.presentation.list.components.ProductListTopAppBar
import com.raf.marketplace.presentation.list.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedTransitionScope.HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    isSettingsScreenVisible: Boolean = false,
    showChartMenu: Boolean = true,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToDetail: (Int) -> Unit = {},
) {
    val localLayoutDirection = LocalLayoutDirection.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val floatingActionToolbarScrollBehavior =
        FloatingToolbarDefaults.exitAlwaysScrollBehavior(Bottom)
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val productFilterState by viewModel.productFilterState.collectAsStateWithLifecycle()

    // Error Message
    LaunchedEffect(uiState.uiMessage) {
        uiState.uiMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                ProductListTopAppBar(
                    scrollBehavior = scrollBehavior,
                    showChartMenu = showChartMenu,
                    searchQuery = productFilterState.query,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    isSettingsButtonVisible = !isSettingsScreenVisible,
                    onNavigateToSettings = onNavigateToSettings
                )
            },
            floatingActionButton = {
                ProductFilterToolbar(
                    scrollBehavior = floatingActionToolbarScrollBehavior,
                    selectedSortType = productFilterState.productSortType,
                    onClicked = viewModel::onProductFilterByChange,
                    onShortByChanged = viewModel::onProductSortByChange
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            },
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .nestedScroll(floatingActionToolbarScrollBehavior)
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                ProductCategory(
                    categories = uiState.categories,
                    selectedCategories = productFilterState.categories,
                    onCLicked = viewModel::onCategoryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = innerPadding.calculateTopPadding())
                )
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(175.dp),
                    contentPadding = PaddingValues(
                        bottom = innerPadding.calculateBottomPadding() + 16.dp,
                        start = innerPadding.calculateStartPadding(localLayoutDirection) + 2.dp,
                        end = innerPadding.calculateEndPadding(localLayoutDirection) + 2.dp,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    if (uiState.isLoading && uiState.products.isEmpty()) {
                        items(10) {
                            ProductItemLoading()
                        }
                        return@LazyVerticalStaggeredGrid
                    }

                    if (uiState.products.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.no_products_found),
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
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
                                onNavigateToDetail(product.id)
                            },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}