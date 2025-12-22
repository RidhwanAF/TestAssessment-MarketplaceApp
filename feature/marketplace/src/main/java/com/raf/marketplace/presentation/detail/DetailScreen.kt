package com.raf.marketplace.presentation.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.SubcomposeAsyncImage
import com.raf.core.presentation.components.customIconButtonShapes
import com.raf.core.presentation.components.shimmerLoading
import com.raf.marketplace.R
import com.raf.marketplace.presentation.components.RatingBar
import com.raf.marketplace.presentation.detail.components.DetailBottomBar
import com.raf.marketplace.presentation.detail.viewmodel.DetailViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    isExpandedScreen: Boolean,
    onBack: () -> Unit,
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scrollState = rememberScrollState()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var isDescTextOverflowing by remember { mutableStateOf(false) }

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
                                Text(text = stringResource(R.string.back))
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
                        state = rememberTooltipState()
                    ) {
                        IconButton(
                            shapes = customIconButtonShapes(),
                            onClick = {
                                // TODO: Carts Show
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = stringResource(R.string.carts)
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            DetailBottomBar(
                priceInDollar = uiState.product?.priceInDollar ?: 0.0,
                onAddToCart = {

                }
            )
        },
        modifier = Modifier
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
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
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
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
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
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                )
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
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
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
            }
        }
    }
}