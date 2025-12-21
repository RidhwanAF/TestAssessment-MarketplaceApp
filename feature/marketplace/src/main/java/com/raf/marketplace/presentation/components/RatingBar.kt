package com.raf.marketplace.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlin.math.floor

@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    rating: Double = 0.0,
    totalRaters: Int = 0,
    stars: Int = 5,
) {
    FlowRow(
        verticalArrangement = Arrangement.Center,
        itemVerticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        val filledStars = floor(rating).toInt()
        val hasHalfStar = rating - filledStars >= 0.5

        for (i in 1..stars) {
            val starType = when {
                i <= filledStars -> StarType.FULL
                hasHalfStar && i == filledStars + 1 -> StarType.HALF
                else -> StarType.BORDER
            }

            val color =
                if (starType != StarType.BORDER) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

            Star(color = color, starType = starType)
        }
        Text(
            text = " ($totalRaters)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        )
    }
}

@Composable
private fun Star(color: Color, starType: StarType = StarType.BORDER) {
    Icon(
        imageVector = when (starType) {
            StarType.BORDER -> Icons.Outlined.StarRate
            StarType.HALF -> Icons.AutoMirrored.Filled.StarHalf
            StarType.FULL -> Icons.Filled.Star
        },
        contentDescription = null,
        tint = color
    )
}

private enum class StarType {
    BORDER, HALF, FULL
}