package io.github.amanshuraikwar.nxtbuz.map.ui.recenter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NearMe
import androidx.compose.material.icons.rounded.NearMeDisabled
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.disabled

@Composable
fun RecenterButton(
    modifier: Modifier = Modifier,
    vm: RecenterViewModel,
) {
    LaunchedEffect(key1 = true) {
        vm.init()
    }

    val buttonState by vm.recenterButtonState.collectAsState()

    Surface(
        modifier = modifier,
        color = MaterialTheme.colors.surface,
        shape = MaterialTheme.shapes.small,
        elevation = 4.dp
    ) {
        Icon(
            imageVector = when (buttonState) {
                RecenterButtonState.LocationAvailable ->
                    Icons.Rounded.NearMe
                RecenterButtonState.LocationNotAvailable ->
                    Icons.Rounded.NearMeDisabled
            },
            modifier = Modifier
                .clickable {
                    vm.recenterClick()
                }
                .padding(12.dp)
                .size(24.dp),
            contentDescription = "Re Center",
            tint = when (buttonState) {
                RecenterButtonState.LocationAvailable ->
                    MaterialTheme.colors.onSurface
                RecenterButtonState.LocationNotAvailable ->
                    MaterialTheme.colors.onSurface.disabled
            }
        )
    }
}