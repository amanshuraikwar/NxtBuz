package io.github.amanshuraikwar.nxtbuz.busstop.busstops

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WrongLocation
import androidx.compose.runtime.Composable

@Composable
fun NoBusStopsErrorView(
    title: String,
    primaryButtonText: String,
    onPrimaryButtonClick: () -> Unit,
    secondaryButtonText: String,
    onSecondaryButtonClick: () -> Unit
) {
    ErrorView(
        icon = Icons.Rounded.WrongLocation,
        title = title,
        primaryButtonText = primaryButtonText,
        onPrimaryButtonClick = onPrimaryButtonClick,
        secondaryButtonText = secondaryButtonText,
        onSecondaryButtonClick = onSecondaryButtonClick
    )
}