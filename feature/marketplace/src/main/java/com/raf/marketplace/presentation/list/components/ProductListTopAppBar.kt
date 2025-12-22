package com.raf.marketplace.presentation.list.components

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.raf.core.presentation.components.customIconButtonShapes
import com.raf.marketplace.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedTransitionScope.ProductListTopAppBar(
    modifier: Modifier = Modifier,
    showChartMenu: Boolean = false,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSettingsButtonVisible: Boolean = true,
    onNavigateToSettings: () -> Unit = {},
    cartItemCount: Int = 0,
    onNavigateToCart: () -> Unit = {},
) {
    TopAppBar(
        title = {
            SearchProductTextField(
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                modifier = Modifier.padding(end = 8.dp)
            )
        },
        actions = {
            AnimatedVisibility(
                visible = showChartMenu,
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
                ) {
                    BadgedBox(
                        badge = {
                            this@TopAppBar.AnimatedVisibility(
                                visible = cartItemCount > 0,
                                enter = scaleIn(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()),
                                exit = scaleOut(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec())
                            ) {
                                Badge {
                                    Text(
                                        text = cartItemCount.toString(),
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
            TooltipBox(
                positionProvider =
                    TooltipDefaults.rememberTooltipPositionProvider(
                        TooltipAnchorPosition.Below
                    ),
                tooltip = {
                    PlainTooltip {
                        Text(text = stringResource(R.string.profile))
                    }
                },
                state = rememberTooltipState()
            ) {
                IconButton(
                    shapes = customIconButtonShapes(),
                    onClick = {
                        // TODO: Profile Show
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = stringResource(R.string.profile)
                    )
                }
            }
            AnimatedContent(
                targetState = isSettingsButtonVisible,
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
                            shapes = customIconButtonShapes(),
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
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SharedTransitionScope.SearchProductTextField(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val motionScheme = MaterialTheme.motionScheme.fastSpatialSpec<Float>()

    var onSearch by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(onSearch) {
        if (onSearch) {
            focusRequester.requestFocus()
        }
    }

    val searchBoxMessageList = listOf(
        stringResource(R.string.search_product),
        stringResource(R.string.choose_your_best_product),
        stringResource(R.string.find_your_best_deal)
    )
    var searchBoxMessageIndex by remember {
        mutableIntStateOf(0)
    }
    LaunchedEffect(Unit, onSearch) {
        while (!onSearch) {
            searchBoxMessageIndex = (searchBoxMessageIndex + 1) % searchBoxMessageList.size
            delay(3000)
        }
    }

    var backProgress by remember {
        mutableFloatStateOf(0f)
    }
    PredictiveBackHandler(onSearch || searchQuery.isNotEmpty()) { backEvent ->
        backEvent.collect { backEventCompat ->
            backProgress = backEventCompat.progress
        }
        if (onSearch) {
            onSearch = false
            focusManager.clearFocus()
        } else if (searchQuery.isNotEmpty()) {
            onSearchQueryChange("")
        }
    }
    LaunchedEffect(onSearch) {
        if (!onSearch) {
            delay(500)
            backProgress = 0f
        }
    }

    AnimatedContent(
        targetState = onSearch,
        transitionSpec = {
            scaleIn(animationSpec = motionScheme) togetherWith scaleOut(animationSpec = motionScheme)
        },
        contentAlignment = Alignment.CenterStart
    ) { isSearching ->
        if (!isSearching) {
            Card(
                shape = MaterialTheme.shapes.extraLarge,
                onClick = { onSearch = true },
                modifier = modifier
                    .sharedElement(
                        sharedContentState = rememberSharedContentState("transition_search_product_container_key"),
                        animatedVisibilityScope = this
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search_product)
                    )
                    AnimatedContent(
                        targetState = searchQuery.ifBlank { searchBoxMessageList[searchBoxMessageIndex] },
                        transitionSpec = {
                            fadeIn() + scaleIn(animationSpec = motionScheme) togetherWith
                                    scaleOut(animationSpec = motionScheme) + fadeOut()
                        },
                        contentAlignment = Alignment.CenterStart
                    ) { message ->
                        Text(
                            text = message,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .graphicsLayer {
                                    val newValue =
                                        if (!onSearch && searchQuery.isNotEmpty())
                                            (1f - backProgress).coerceAtLeast(0.25f)
                                        else 1f
                                    alpha = newValue
                                }
                        )
                    }
                }
            }
        } else {
            Card(
                shape = MaterialTheme.shapes.extraLarge,
                modifier = modifier
                    .sharedElement(
                        sharedContentState = rememberSharedContentState("transition_search_product_container_key"),
                        animatedVisibilityScope = this
                    )
                    .graphicsLayer {
                        val newScaleValue = (1f - backProgress).coerceAtLeast(0.85f)
                        val newAlphaValue = (1f - backProgress).coerceAtLeast(0.75f)
                        scaleY = newScaleValue
                        alpha = newAlphaValue
                    }
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    shape = MaterialTheme.shapes.extraLarge,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions {
                        this.defaultKeyboardAction(imeAction = ImeAction.Done)
                        focusManager.clearFocus()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.search_product)
                        )
                    },
                    trailingIcon = {
                        AnimatedVisibility(
                            visible = searchQuery.isNotEmpty(),
                            enter = scaleIn(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()),
                            exit = scaleOut(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec())
                        ) {
                            IconButton(
                                shapes = customIconButtonShapes(),
                                onClick = {
                                    onSearch = false
                                    onSearchQueryChange("")
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.close)
                                )
                            }
                        }
                    },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search_product),
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            }
        }
    }
}