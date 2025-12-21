package com.raf.marketplace.presentation.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Card
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.raf.marketplace.R
import com.raf.marketplace.domain.model.Product
import com.raf.marketplace.presentation.components.RatingBar
import com.raf.marketplace.presentation.utilities.CurrencyHelper.covertToIDR

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProductItem(
    modifier: Modifier = Modifier,
    product: Product,
    onClicked: () -> Unit = {},
) {
    Card(
        onClick = {
            onClicked()
        },
        modifier = modifier
            .padding(8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            SubcomposeAsyncImage(
                model = product.image,
                contentDescription = product.title,
                loading = {
                    ContainedLoadingIndicator(
                        modifier = Modifier
                            .widthIn(max = 64.dp)
                            .aspectRatio(1f)
                    )
                },
                error = {
                    Icon(
                        imageVector = Icons.Default.BrokenImage,
                        contentDescription = stringResource(R.string.failed_to_load_product_image),
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .widthIn(max = 64.dp)
                            .aspectRatio(1f)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = product.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
            )
            RatingBar(
                rating = product.rating.rate,
                totalRaters = product.rating.count,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = product.priceInDollar.covertToIDR(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}