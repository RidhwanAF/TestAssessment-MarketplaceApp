package com.raf.marketplace.presentation.cart.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.raf.core.presentation.components.customIconButtonShapes
import com.raf.core.presentation.components.shimmerLoading
import com.raf.marketplace.R
import com.raf.marketplace.presentation.cart.viewmodel.ProductInCartUi
import com.raf.marketplace.presentation.utilities.CurrencyHelper.covertToIDR
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CartItem(
    modifier: Modifier = Modifier,
    productInCart: ProductInCartUi,
    onClick: () -> Unit,
    onSumOrSubtractQuantity: (isSum: Boolean) -> Unit,
    onRemove: () -> Unit,
) {
    val localHapticFeedback = LocalHapticFeedback.current
    val localDensity = LocalDensity.current
    val scope = rememberCoroutineScope()

    var contentSize by remember {
        mutableStateOf(IntSize.Zero)
    }
    val offsetX = remember {
        Animatable(0f)
    }

    val isPastThreshold by remember(contentSize) {
        derivedStateOf {
            if (contentSize.width == 0) return@derivedStateOf false
            abs(offsetX.value) > (contentSize.width / 2f)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // BG
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .then(
                    with(localDensity) {
                        Modifier.height(contentSize.height.toDp())
                    }
                )
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.error)
                .padding(horizontal = 32.dp)
        ) {
            repeat(2) {
                AnimatedVisibility(
                    visible = abs(offsetX.value) > 150f,
                    enter = scaleIn(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()),
                    exit = scaleOut(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec())
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
        // Content
        Card(
            shape = MaterialTheme.shapes.large,
            onClick = onClick,
            modifier = modifier
                .onGloballyPositioned {
                    contentSize = it.size
                }
                .graphicsLayer {
                    translationX = offsetX.value
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                if (isPastThreshold) {
                                    localHapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                    val targetOffsetX = offsetX.value.sign * contentSize.width * 2f
                                    offsetX.animateTo(
                                        targetOffsetX,
                                        spring()
                                    )
                                    onRemove()
                                } else {
                                    offsetX.animateTo(0f)
                                }
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            val originalX = offsetX.value
                            val newX = originalX + dragAmount
                            scope.launch {
                                offsetX.snapTo(newX)
                            }
                            val threshold = contentSize.width / 2f
                            if (abs(originalX) < threshold && abs(newX) >= threshold) {
                                localHapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                            }
                        }
                    )
                }
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                SubcomposeAsyncImage(
                    model = productInCart.product.image,
                    contentDescription = productInCart.product.title,
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
                            contentDescription = productInCart.product.title,
                            modifier = Modifier.size(64.dp)
                        )
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .heightIn(max = 100.dp)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = productInCart.product.title,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium
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
                            enabled = productInCart.cart.quantity > 0,
                            onClick = {
                                onSumOrSubtractQuantity(false)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = stringResource(R.string.subtract)
                            )
                        }
                        Text(
                            text = productInCart.cart.quantity.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                        )
                        IconButton(
                            shapes = customIconButtonShapes(),
                            onClick = {
                                onSumOrSubtractQuantity(true)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.sum)
                            )
                        }
                    }
                    Text(
                        text = (productInCart.product.priceInDollar * productInCart.cart.quantity).covertToIDR(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}