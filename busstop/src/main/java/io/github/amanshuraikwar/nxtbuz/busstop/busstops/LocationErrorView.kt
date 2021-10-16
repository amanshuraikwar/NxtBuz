package io.github.amanshuraikwar.nxtbuz.busstop.busstops

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NearMeDisabled
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.amanshuraikwar.nxtbuz.common.compose.util.PreviewSurface

@Composable
fun LocationErrorView(
    title: String,
    primaryButtonText: String,
    onPrimaryButtonClick: () -> Unit,
    secondaryButtonText: String,
    onSecondaryButtonClick: () -> Unit
) {
    ErrorView(
        icon = Icons.Rounded.NearMeDisabled,
        title = title,
        primaryButtonText = primaryButtonText,
        onPrimaryButtonClick = onPrimaryButtonClick,
        secondaryButtonText = secondaryButtonText,
        onSecondaryButtonClick = onSecondaryButtonClick
    )
}

@Composable
@Preview
fun LocationErrorViewPreview() {
    PreviewSurface {
        LocationErrorView(
            title = "We don't have location permission!",
            primaryButtonText = "GIVE PERMISSION",
            onPrimaryButtonClick = {},
            secondaryButtonText = "USE DEFAULT LOCATION",
            onSecondaryButtonClick = {}
        )
    }
}

@Composable
@Preview
fun LocationErrorViewPreviewDark() {
    PreviewSurface(
        darkTheme = true
    ) {
        LocationErrorView(
            title = "Location setting is not enabled!",
            primaryButtonText = "ENABLE LOCATION",
            onPrimaryButtonClick = {},
            secondaryButtonText = "USE DEFAULT LOCATION",
            onSecondaryButtonClick = {}
        )
    }
}