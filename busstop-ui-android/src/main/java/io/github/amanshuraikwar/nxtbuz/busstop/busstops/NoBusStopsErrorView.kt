package io.github.amanshuraikwar.nxtbuz.busstop.busstops

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun NoBusStopsErrorView(
    icon: ImageVector,
    title: String,
    primaryButtonText: String,
    onPrimaryButtonClick: () -> Unit,
    secondaryButtonText: String,
    onSecondaryButtonClick: () -> Unit
) {
    ErrorView(
        icon = icon,
        title = title,
        primaryButtonText = primaryButtonText,
        onPrimaryButtonClick = onPrimaryButtonClick,
        secondaryButtonText = secondaryButtonText,
        onSecondaryButtonClick = onSecondaryButtonClick
    )
}