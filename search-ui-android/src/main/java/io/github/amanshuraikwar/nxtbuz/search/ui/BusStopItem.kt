package io.github.amanshuraikwar.nxtbuz.search.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.body1Bold
import io.github.amanshuraikwar.nxtbuz.search.R
import io.github.amanshuraikwar.nxtbuz.search.ui.model.SearchResult
import java.util.*

@Composable
fun BusStopItem(
    modifier: Modifier = Modifier,
    data: SearchResult.BusStopResult,
) {
    Box(
        modifier = modifier,
    ) {
        Surface(
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    top = 16.dp,
                    bottom = 16.dp
                ),
            color = MaterialTheme.colors.primary,
            shape = MaterialTheme.shapes.small
        ) {
            Icon(
                painter = painterResource(
                    R.drawable.ic_bus_stop_24
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp),
                contentDescription = "Bus Stop",
                tint = MaterialTheme.colors.onPrimary
            )
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(
                    top = 16.dp,
                    start = 72.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
        ) {
            Text(
                text = data.busStopDescription,
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface
            )

            Text(
                text = data.busStopInfo.uppercase(Locale.ROOT),
                style = MaterialTheme.typography.overline,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(top = 2.dp)
            )

            Text(
                text = data.operatingBuses,
                style = MaterialTheme.typography.body1Bold,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(top = 8.dp),
                lineHeight = 20.sp,
            )
        }
    }
}