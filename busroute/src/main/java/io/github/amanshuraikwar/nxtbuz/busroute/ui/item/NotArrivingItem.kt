package io.github.amanshuraikwar.nxtbuz.busroute.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium
import java.util.*

@Composable
fun NotArrivingItem(
    modifier: Modifier = Modifier,
    message: String,
    lastUpdatedOn: String? = null,
) {
    Column(modifier) {
        Text(
            text = message,
            style = MaterialTheme.typography.h6Bold,
            color = MaterialTheme.colors.onSurface.medium
        )

        if (lastUpdatedOn != null) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = lastUpdatedOn.toUpperCase(Locale.ROOT),
                color = MaterialTheme.colors.onSurface.medium,
                style = MaterialTheme.typography.overline,
            )
        }
    }
}