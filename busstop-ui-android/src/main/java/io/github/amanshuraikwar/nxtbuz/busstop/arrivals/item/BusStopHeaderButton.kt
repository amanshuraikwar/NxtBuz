package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.outline

@Composable
fun BusStopHeaderButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    text: String,
    onClick: () -> Unit,
    selected: Boolean = false
) {
    Surface(
        modifier = modifier,
        border = BorderStroke(1.dp, MaterialTheme.colors.outline),
        shape = RoundedCornerShape(percent = 50),
        color = if (selected) {
            MaterialTheme.colors.primary
        } else {
            MaterialTheme.colors.surface
        }
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 4.dp)
                .clip(RoundedCornerShape(percent = 50)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = text,
                tint = if (selected) {
                    MaterialTheme.colors.onPrimary
                } else {
                    MaterialTheme.colors.onSurface.medium
                },
                modifier = Modifier
                    .padding(4.dp)
                    .size(16.dp)
            )

            Text(
                modifier = Modifier
                    .padding(end = 6.dp, start = 0.dp)
                    .padding(bottom = 4.dp, top = 2.dp),
                text = text,
                style = MaterialTheme.typography.button,
                color = if (selected) {
                    MaterialTheme.colors.onPrimary
                } else {
                    MaterialTheme.colors.onSurface.medium
                }
            )
        }
    }
}