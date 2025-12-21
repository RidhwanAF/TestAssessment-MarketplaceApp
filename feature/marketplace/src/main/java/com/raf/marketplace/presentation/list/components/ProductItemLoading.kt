package com.raf.marketplace.presentation.list.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.raf.core.presentation.components.shimmerLoading

@Composable
fun ProductItemLoading(modifier: Modifier = Modifier) {
    val randomHeight = (200..320).random()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(randomHeight.dp)
            .padding(8.dp)
            .clip(MaterialTheme.shapes.large)
            .shimmerLoading()
    )
}