package com.example.libraryapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun StatusBadge(isBorrowed: Boolean, isMine: Boolean, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val (text, container, content) = when {
        isMine -> Triple("Yours", colors.tertiaryContainer, colors.onTertiaryContainer)
        isBorrowed -> Triple("Taken", colors.errorContainer, colors.onErrorContainer)
        else -> Triple("Available", colors.secondaryContainer, colors.onSecondaryContainer)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(container)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelMedium, color = content)
    }
}
