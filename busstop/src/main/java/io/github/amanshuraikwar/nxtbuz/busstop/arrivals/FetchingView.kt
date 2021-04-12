package io.github.amanshuraikwar.nxtbuz.busstop.arrivals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold

@Composable
fun FetchingView(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
    ) {
        Text(
            text = "Fetching Arrivals...",
            style = MaterialTheme.typography.h6Bold,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp, top = 32.dp)
        )

//        LinearProgressIndicator(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 32.dp, end = 32.dp, start = 32.dp)
//        )
    }
}