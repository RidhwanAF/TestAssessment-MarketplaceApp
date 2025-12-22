package com.raf.marketplace.presentation.list.components

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingToolbarScrollBehavior
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.raf.core.presentation.components.customIconToggleButtonShapes
import com.raf.marketplace.R
import com.raf.marketplace.domain.model.ProductSortType
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProductFilterToolbar(
    modifier: Modifier = Modifier,
    scrollBehavior: FloatingToolbarScrollBehavior,
    selectedSortType: Pair<ProductSortType, Boolean>?,
    onClicked: (ProductSortType) -> Unit,
    onShortByChanged: (ProductSortType, Boolean) -> Unit,
) {
    val localHapticFeedback = LocalHapticFeedback.current

    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    var backProgress by remember {
        mutableFloatStateOf(0f)
    }
    PredictiveBackHandler(expanded) { backEventCompat ->
        backEventCompat.collect { backEventCompat ->
            backProgress = backEventCompat.progress
        }
        expanded = false
    }
    LaunchedEffect(expanded) {
        if (!expanded) {
            delay(500)
            backProgress = 0f
        }
    }

    HorizontalFloatingToolbar(
        expanded = expanded,
        scrollBehavior = scrollBehavior,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { expanded = !expanded },
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = stringResource(R.string.filter)
                )
            }
        },
        modifier = modifier
            .graphicsLayer {
                scaleY = (1f - backProgress).coerceAtLeast(0.9f)
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp)
        ) {
            ProductSortType.entries.forEach { sortType ->
                val icon = when (sortType) {
                    ProductSortType.NAME -> Icons.Default.Abc
                    ProductSortType.PRICE -> Icons.Default.MonetizationOn
                    ProductSortType.RATING -> Icons.Default.StarRate
                }
                val label = when (sortType) {
                    ProductSortType.NAME -> stringResource(R.string.name)
                    ProductSortType.PRICE -> stringResource(R.string.price)
                    ProductSortType.RATING -> stringResource(R.string.rating)
                }

                val isSelected = selectedSortType?.first == sortType

                FilterToolbarItem(
                    icon = icon,
                    label = label,
                    isSelected = isSelected,
                    isSortByASC = selectedSortType?.second == true,
                    onClicked = {
                        onClicked(sortType)
                        if (isSelected) {
                            localHapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOff)
                        } else {
                            localHapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOn)
                        }
                    },
                    onSortTypeClicked = { checked ->
                        onShortByChanged(sortType, checked)
                        if (checked) {
                            localHapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOn)
                        } else {
                            localHapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOff)
                        }
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FilterToolbarItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    isSortByASC: Boolean,
    onClicked: () -> Unit,
    onSortTypeClicked: (Boolean) -> Unit,
) {
    val motionScheme = MaterialTheme.motionScheme.fastSpatialSpec<Float>()
    val toolboxPositionProvider =
        TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above)

    val bgColorAlphaAnimation by animateFloatAsState(
        targetValue = if (isSelected) 0.1f else 0f
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    topStart = 16.dp,
                    bottomStart = 16.dp,
                    topEnd = 32.dp,
                    bottomEnd = 32.dp
                )
            )
            .background(MaterialTheme.colorScheme.primary.copy(bgColorAlphaAnimation))
            .padding(2.dp)
            .animateContentSize()
    ) {
        TooltipBox(
            positionProvider = toolboxPositionProvider,
            tooltip = {
                PlainTooltip {
                    Text(text = label)
                }
            },
            state = rememberTooltipState()
        ) {
            FilledTonalIconToggleButton(
                shapes = customIconToggleButtonShapes(),
                checked = isSelected,
                onCheckedChange = { onClicked() }
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label
                )
            }
        }
        AnimatedVisibility(
            visible = isSelected,
            enter = scaleIn(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()),
            exit = scaleOut(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec())
        ) {
            TooltipBox(
                positionProvider = toolboxPositionProvider,
                tooltip = {
                    PlainTooltip {
                        Text(text = stringResource(R.string.sort_list))
                    }
                },
                state = rememberTooltipState()
            ) {
                IconToggleButton(
                    checked = isSortByASC,
                    onCheckedChange = { onSortTypeClicked(it) }
                ) {
                    AnimatedContent(
                        targetState = isSortByASC,
                        transitionSpec = {
                            scaleIn(animationSpec = motionScheme) togetherWith
                                    scaleOut(animationSpec = motionScheme)
                        },
                        contentAlignment = Alignment.Center
                    ) { targetState ->
                        if (!targetState) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = stringResource(R.string.sort_list)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = stringResource(R.string.sort_list)
                            )
                        }
                    }
                }
            }
        }
    }
}
