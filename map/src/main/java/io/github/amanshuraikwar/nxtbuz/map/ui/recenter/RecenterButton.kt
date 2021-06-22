package io.github.amanshuraikwar.nxtbuz.map.ui.recenter

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GpsOff
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.Fab
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
    val tint by animateColorAsState(
        targetValue = when (buttonState) {
            RecenterButtonState.LocationAvailable ->
                MaterialTheme.colors.onSurface
            RecenterButtonState.LocationNotAvailable ->
                MaterialTheme.colors.onSurface.disabled
        }
    )

    val elevation by animateDpAsState(
        targetValue = when (buttonState) {
            RecenterButtonState.LocationAvailable ->
                4.dp
            RecenterButtonState.LocationNotAvailable ->
                0.dp
        }
    )

    Fab(
        modifier = modifier,
        tint = tint,
        elevation = elevation,
        imageVector = when (buttonState) {
            RecenterButtonState.LocationAvailable ->
                Icons.Rounded.MyLocation
            RecenterButtonState.LocationNotAvailable ->
                Icons.Rounded.GpsOff
        },
        contentDescription = "Re Center",
        onClick = {
            vm.recenterClick()
        }
    )
}