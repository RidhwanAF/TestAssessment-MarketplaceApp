package com.raf.marketplace.presentation.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.SubcomposeAsyncImage
import com.raf.core.presentation.components.customIconButtonShapes
import com.raf.core.presentation.components.shimmerLoading
import com.raf.marketplace.R
import com.raf.marketplace.presentation.components.ProductPriceTotalBottomBar
import com.raf.marketplace.presentation.components.RatingBar
import com.raf.marketplace.presentation.detail.components.ProductImageViewer
import com.raf.marketplace.presentation.detail.viewmodel.DetailViewModel
import com.raf.marketplace.presentation.utilities.CurrencyHelper.covertToIDR

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedTransitionScope.DetailScreen(
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
    isCartScreenVisible: Boolean,
    viewModel: DetailViewModel,
    isExpandedScreen: Boolean,
    onNavigateToCart: () -> Unit,
    onBack: () -> Unit,
) {
    val localDensity = LocalDensity.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val addToChartMessage = stringResource(R.string.added_to_cart)
    var isDescTextOverflowing by remember { mutableStateOf(false) }

    var baseImageHeight by remember {
        mutableIntStateOf(0)
    }
    var showImgFullScreen by rememberSaveable {
        mutableStateOf(false)
    }

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
                TopAppBar(
                    title = {/*NO-OP*/ },
                    navigationIcon = {
                        TooltipBox(
                            positionProvider =
                                TooltipDefaults.rememberTooltipPositionProvider(
                                    TooltipAnchorPosition.Below
                                ),
                            tooltip = {
                                PlainTooltip {
                                    Text(
                                        text = if (isExpandedScreen) stringResource(R.string.close)
                                        else stringResource(R.string.back)
                                    )
                                }
                            },
                            state = rememberTooltipState()
                        ) {
                            IconButton(
                                shapes = customIconButtonShapes(),
                                onClick = onBack
                            ) {
                                if (isExpandedScreen) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(R.string.close)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(R.string.back)
                                    )
                                }
                            }
                        }
                    },
                    actions = {
                        AnimatedVisibility(
                            visible = !isCartScreenVisible,
                            enter = scaleIn(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()),
                            exit = scaleOut(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec())
                        ) {
                            TooltipBox(
                                positionProvider =
                                    TooltipDefaults.rememberTooltipPositionProvider(
                                        TooltipAnchorPosition.Below
                                    ),
                                tooltip = {
                                    PlainTooltip {
                                        Text(text = stringResource(R.string.carts))
                                    }
                                },
                                state = rememberTooltipState(),
                                modifier = Modifier
                                    .sharedBounds(
                                        sharedContentState = rememberSharedContentState("cart-container"),
                                        animatedVisibilityScope = this
                                    )
                                    .padding(end = 8.dp)
                            ) {
                                BadgedBox(
                                    badge = {
                                        this@TopAppBar.AnimatedVisibility(
                                            visible = uiState.cartItemCount > 0,
                                            enter = scaleIn(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()),
                                            exit = scaleOut(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec())
                                        ) {
                                            Badge {
                                                Text(
                                                    text = uiState.cartItemCount.toString(),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                ) {
                                    IconButton(
                                        shapes = customIconButtonShapes(),
                                        onClick = onNavigateToCart
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ShoppingCart,
                                            contentDescription = stringResource(R.string.carts)
                                        )
                                    }
                                }
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            bottomBar = {
                ProductPriceTotalBottomBar(
                    totalPriceInDollar = (uiState.product?.priceInDollar ?: 0.0) * uiState.quantity,
                    enabled = uiState.quantity > 0 && uiState.product != null && !uiState.isLoading,
                    quantity = if (uiState.product != null && !uiState.isLoading) uiState.quantity else 0,
                    onButtonClicked = {
                        viewModel.addToCart()
                        viewModel.showUiMessage(addToChartMessage)
                    }
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            },
            modifier = modifier
                .sharedBounds(
                    sharedContentState = rememberSharedContentState("product-container-${viewModel.productId}"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(innerPadding)
            ) {
                if (uiState.product == null && uiState.isLoading) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            16.dp,
                            Alignment.CenterVertically
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        ContainedLoadingIndicator(modifier = Modifier.size(100.dp))
                        Text(
                            text = stringResource(R.string.loading_product),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    return@Column
                }
                if (uiState.product == null) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            16.dp,
                            Alignment.CenterVertically
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = stringResource(R.string.product_not_found),
                            modifier = Modifier.size(100.dp)
                        )
                        Text(
                            text = stringResource(R.string.product_not_found),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    return@Column
                }
                uiState.product?.let { product ->
                    AnimatedContent(
                        targetState = showImgFullScreen,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        modifier = Modifier
                            .sharedElement(
                                sharedContentState = rememberSharedContentState("product-image-${product.id}"),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                    ) { isFullScreen ->
                        if (isFullScreen) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(
                                        with(localDensity) {
                                            Modifier.height(baseImageHeight.toDp())
                                        }
                                    )
                            )
                        } else {
                            SubcomposeAsyncImage(
                                model = product.image,
                                contentDescription = product.title,
                                loading = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .size(64.dp)
                                            .shimmerLoading()
                                    )
                                },
                                error = {
                                    Icon(
                                        imageVector = Icons.Default.BrokenImage,
                                        contentDescription = product.title,
                                        modifier = Modifier.size(64.dp)
                                    )
                                },
                                modifier = Modifier
                                    .onGloballyPositioned {
                                        baseImageHeight = it.size.height
                                    }
                                    .sharedElement(
                                        sharedContentState = rememberSharedContentState("product-image-fullscreen-${product.id}"),
                                        animatedVisibilityScope = this@AnimatedContent
                                    )
                                    .fillMaxWidth()
                                    .heightIn(max = 200.dp)
                                    .clickable(
                                        onClick = { showImgFullScreen = true }
                                    )
                            )
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = product.title,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier
                                .sharedElement(
                                    sharedContentState = rememberSharedContentState("product-title-${product.id}"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                        )
                        Text(
                            text = product.category.split(" ")
                                .joinToString(" ") { text -> text.replaceFirstChar { it.uppercase() } },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.extraLarge)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        RatingBar(
                            rating = product.rating.rate,
                            totalRaters = product.rating.count,
                            bigSize = true,
                            modifier = Modifier
                                .sharedElement(
                                    sharedContentState = rememberSharedContentState("product-rating-${product.id}"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                                .fillMaxWidth()
                        )
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.price_quantity),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(
                                    16.dp,
                                    Alignment.CenterHorizontally
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = buildAnnotatedString {
                                        append(product.priceInDollar.covertToIDR())
                                        withStyle(
                                            style = MaterialTheme.typography.bodyMedium.toSpanStyle()
                                        ) {
                                            append(stringResource(R.string.per_item))
                                        }
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .sharedElement(
                                            sharedContentState = rememberSharedContentState("product-price-${product.id}"),
                                            animatedVisibilityScope = animatedVisibilityScope
                                        )
                                        .weight(1f)
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(
                                        8.dp,
                                        Alignment.CenterHorizontally
                                    ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(MaterialTheme.shapes.extraLarge)
                                        .background(MaterialTheme.colorScheme.primary.copy(0.15f))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    IconButton(
                                        shapes = customIconButtonShapes(),
                                        enabled = uiState.quantity > 0,
                                        onClick = {
                                            viewModel.sumOrSubtractQuantity(false)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Remove,
                                            contentDescription = stringResource(R.string.subtract)
                                        )
                                    }
                                    Text(
                                        text = uiState.quantity.toString(),
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 1,
                                    )
                                    IconButton(
                                        shapes = customIconButtonShapes(),
                                        onClick = {
                                            viewModel.sumOrSubtractQuantity(true)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = stringResource(R.string.sum)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 16.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.description),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = product.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = if (uiState.showFullDesc) Int.MAX_VALUE else 3,
                                    onTextLayout = { textLayoutResult ->
                                        isDescTextOverflowing = textLayoutResult.lineCount >= 3
                                    },
                                    modifier = Modifier.animateContentSize()
                                )
                            }
                            if (isDescTextOverflowing) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(
                                        8.dp,
                                        Alignment.CenterHorizontally
                                    ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            onClick = viewModel::toggleFullDescription
                                        )
                                        .padding(horizontal = 16.dp)
                                        .padding(bottom = 16.dp)
                                ) {
                                    AnimatedContent(
                                        targetState = uiState.showFullDesc
                                    ) { target ->
                                        val text =
                                            if (target) stringResource(R.string.show_less)
                                            else stringResource(R.string.show_more)
                                        Text(
                                            text = text,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            textDecoration = TextDecoration.Underline,
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        /**
         * Dialogs
         */
        ProductImageViewer(
            visible = showImgFullScreen,
            productId = viewModel.productId,
            title = uiState.product?.title ?: "",
            image = uiState.product?.image ?: "",
            onCLose = { showImgFullScreen = false },
            modifier = Modifier.zIndex(100f)
        )
    }
}