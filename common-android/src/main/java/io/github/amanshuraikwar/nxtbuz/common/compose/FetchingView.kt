package io.github.amanshuraikwar.nxtbuz.common.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.disabled

@Composable
fun FetchingView(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
    ) {
        Text(
            text = "",
            color = MaterialTheme.colors.onSurface,
            style = MaterialTheme.typography.body2,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .loading(),
        )

        Text(
            text = "                   ",
            color = MaterialTheme.colors.onSurface,
            style = MaterialTheme.typography.body2,
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                .loading(),
        )
    }
}

@Composable
fun Modifier.loading() = this.background(
    color = MaterialTheme.colors.onSurface.disabled,
    shape = RoundedCornerShape(4.dp),
)