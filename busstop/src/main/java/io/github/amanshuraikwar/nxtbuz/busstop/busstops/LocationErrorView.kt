package io.github.amanshuraikwar.nxtbuz.busstop.busstops

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.outline
import io.github.amanshuraikwar.nxtbuz.common.compose.util.PreviewSurface

@Composable
fun LocationErrorView(
    title: String,
    primaryButtonText: String,
    onPrimaryButtonClick: () -> Unit,
    secondaryButtonText: String,
    onSecondaryButtonClick: () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(72.dp))

        Surface(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            color = MaterialTheme.colors.primary,
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(
                imageVector = Icons.Rounded.LocationOff,
                modifier = Modifier
                    .padding(16.dp)
                    .size(48.dp),
                contentDescription = "Error",
                tint = MaterialTheme.colors.onPrimary
            )
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 16.dp,
                    start = 32.dp,
                    end = 32.dp
                ),
            text = title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h6Bold,
            color = MaterialTheme.colors.onSurface
        )

        Surface(
            modifier = Modifier
                .padding(
                    top = 32.dp,
                    start = 32.dp,
                    end = 32.dp
                )
                .fillMaxWidth(),
            color = MaterialTheme.colors.primary,
            elevation = 0.dp,
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = primaryButtonText,
                style = MaterialTheme.typography.button,
                color = MaterialTheme.colors.onPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable {
                        onPrimaryButtonClick()
                    }
                    .padding(12.dp)
            )
        }

        Surface(
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    start = 32.dp,
                    end = 32.dp,
                    bottom = 32.dp,
                )
                .fillMaxWidth(),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colors.outline
            ),
            color = MaterialTheme.colors.surface,
            elevation = 0.dp,
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = secondaryButtonText,
                style = MaterialTheme.typography.button,
                color = MaterialTheme.colors.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable {
                        onSecondaryButtonClick()
                    }
                    .padding(12.dp)
            )
        }
    }
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