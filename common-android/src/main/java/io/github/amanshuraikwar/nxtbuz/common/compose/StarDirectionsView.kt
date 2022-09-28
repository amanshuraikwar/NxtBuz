package io.github.amanshuraikwar.nxtbuz.common.compose

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Directions
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.directions
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.disabled
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.star

@Composable
fun StarDirectionsView(
    modifier: Modifier = Modifier,
    starred: Boolean,
    onStarToggle: (newValue: Boolean) -> Unit,
    onGoToClick: () -> Unit
) {
    Row(
        modifier = modifier
    ) {
        CompositionLocalProvider(
            LocalIndication provides rememberRipple(color = MaterialTheme.colors.star)
        ) {
            HeaderButton(
                imageVector = if (starred) {
                    Icons.Rounded.Star
                } else {
                    Icons.Rounded.StarOutline
                },
                text = if (starred) {
                    "UN-STAR"
                } else {
                    "STAR"
                },
                onClick = {
                    onStarToggle(!starred)
                },
                outlineColor = MaterialTheme.colors.star.disabled,
                onUnSelectedColor = MaterialTheme.colors.star
            )
        }

        CompositionLocalProvider(
            LocalIndication provides rememberRipple(
                color = MaterialTheme.colors.directions
            )
        ) {
            HeaderButton(
                modifier = Modifier.padding(start = 12.dp),
                imageVector = Icons.Rounded.Directions,
                text = "DIRECTIONS",
                onClick = onGoToClick,
                outlineColor = MaterialTheme.colors.directions.disabled,
                onUnSelectedColor = MaterialTheme.colors.directions
            )
        }
    }
}

@Composable
fun StarDirectionsView(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
    ) {
        CompositionLocalProvider(
            LocalIndication provides rememberRipple(color = MaterialTheme.colors.star)
        ) {
            HeaderButton(
                imageVector = Icons.Rounded.Star,
                text = "STAR",
                onClick = { },
                selected = true,
                outlineColor = MaterialTheme.colors.onSurface.disabled,
                selectedColor = MaterialTheme.colors.onSurface.disabled,
                onSelectedColor = MaterialTheme.colors.onSurface.disabled
            )
        }

        CompositionLocalProvider(
            LocalIndication provides rememberRipple(
                color = MaterialTheme.colors.directions
            )
        ) {
            HeaderButton(
                modifier = Modifier.padding(start = 12.dp),
                imageVector = Icons.Rounded.Directions,
                text = "DIRECTIONS",
                onClick = { },
                selected = true,
                outlineColor = MaterialTheme.colors.onSurface.disabled,
                selectedColor = MaterialTheme.colors.onSurface.disabled,
                onSelectedColor = MaterialTheme.colors.onSurface.disabled
            )
        }
    }
}