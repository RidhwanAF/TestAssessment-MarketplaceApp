package com.raf.marketplace.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.raf.core.presentation.components.customButtonShapes
import com.raf.marketplace.R
import com.raf.marketplace.presentation.utilities.CurrencyHelper.covertToIDR

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProductPriceTotalBottomBar(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    totalPriceInDollar: Double,
    quantity: Int,
    buttonIcon: ImageVector = Icons.Default.AddShoppingCart,
    buttonLabel: String = stringResource(R.string.add_to_cart),
    onButtonClicked: () -> Unit,
) {
    val localHapticFeedback = LocalHapticFeedback.current

    ElevatedCard(
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize()
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = MaterialTheme.typography.titleMedium.toSpanStyle()
                    ) {
                        append(stringResource(R.string.total))
                    }
                    if (quantity > 0) {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Light,
                                fontSize = MaterialTheme.typography.bodySmall.fontSize
                            )
                        ) {
                            val quantityString = if (quantity > 1) {
                                stringResource(R.string.items_with_args, quantity)
                            } else {
                                stringResource(R.string.item_with_args, quantity)
                            }
                            append(" ($quantityString)")
                        }
                    }
                    append("\n")
                    append(totalPriceInDollar.covertToIDR())
                },
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Button(
                shapes = customButtonShapes(),
                enabled = enabled,
                onClick = {
                    onButtonClicked()
                    localHapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = buttonIcon,
                        contentDescription = buttonLabel
                    )
                    Text(
                        text = buttonLabel,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}