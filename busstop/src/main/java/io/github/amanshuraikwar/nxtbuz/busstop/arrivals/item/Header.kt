package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.*

@Composable
fun Header(title: String) {
    Text(
        text = title.toUpperCase(Locale.ROOT),
        Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
        color = MaterialTheme.colors.onSurface,
        style = MaterialTheme.typography.overline
    )
}