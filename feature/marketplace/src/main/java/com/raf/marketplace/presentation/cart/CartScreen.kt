package com.raf.marketplace.presentation.cart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material.icons.outlined.ShoppingCart
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.raf.core.presentation.components.customIconButtonShapes
import com.raf.marketplace.R
import com.raf.marketplace.presentation.cart.component.CartItem
import com.raf.marketplace.presentation.cart.viewmodel.CartViewModel
import com.raf.marketplace.presentation.components.ProductPriceTotalBottomBar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedTransitionScope.CartScreen(
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: CartViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onItemClicked: (productId: Int) -> Unit,
) {
    val localLayoutDirection = LocalLayoutDirection.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Error Message
    LaunchedEffect(uiState.uiMessage) {
        uiState.uiMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.carts),
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    TooltipBox(
                        positionProvider =
                            TooltipDefaults.rememberTooltipPositionProvider(
                                TooltipAnchorPosition.Below
                            ),
                        tooltip = {
                            PlainTooltip {
                                Text(text = stringResource(R.string.back))
                            }
                        },
                        state = rememberTooltipState()
                    ) {
                        IconButton(
                            shapes = customIconButtonShapes(),
                            onClick = onBack
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = uiState.productsInCart.isNotEmpty(),
                enter = scaleIn(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()) + slideInVertically { it },
                exit = slideOutVertically { it } + scaleOut(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec())
            ) {
                ProductPriceTotalBottomBar(
                    totalPriceInDollar = uiState.totalPriceInDollar,
                    quantity = uiState.totalQuantity,
                    buttonLabel = stringResource(R.string.checkout),
                    buttonIcon = Icons.Default.ShoppingCartCheckout,
                    onButtonClicked = {
                        // TODO: Checkout
                    }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        modifier = modifier
            .sharedBounds(
                sharedContentState = rememberSharedContentState("cart-container"),
                animatedVisibilityScope = animatedVisibilityScope
            )
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = innerPadding.calculateStartPadding(localLayoutDirection) + 16.dp,
                end = innerPadding.calculateEndPadding(localLayoutDirection) + 16.dp,
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() + 16.dp
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            if (uiState.productsInCart.isEmpty()) {
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = stringResource(R.string.empty_cart_message),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = stringResource(R.string.empty_cart_message),
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                return@LazyColumn
            }

            items(
                items = uiState.productsInCart,
                key = { it.product.id }
            ) { productInCart ->
                CartItem(
                    productInCart = productInCart,
                    onSumOrSubtractQuantity = { isSum ->
                        viewModel.updateCartItem(productInCart.product.id, isSum)
                    },
                    onClick = {
                        onItemClicked(productInCart.product.id)
                    },
                    onRemove = {
                        // TODO
                    },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}