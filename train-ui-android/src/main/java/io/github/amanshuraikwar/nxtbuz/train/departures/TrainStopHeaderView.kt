package io.github.amanshuraikwar.nxtbuz.train.departures

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NotAccessible
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Train
import androidx.compose.material.icons.rounded.Wc
import androidx.compose.material.icons.rounded.WheelchairPickup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.StarDirectionsView
import io.github.amanshuraikwar.nxtbuz.common.compose.StarIndicatorView
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium
import java.util.Locale

@Composable
internal fun TrainStopHeaderView(
    modifier: Modifier = Modifier,
    data: TrainStopHeader
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Icon(
            imageVector = Icons.Rounded.Train,
            modifier = Modifier
                .padding(16.dp)
                .background(
                    color = MaterialTheme.colors.primary,
                    shape = MaterialTheme.shapes.small
                )
                .padding(8.dp)
                .size(24.dp),
            contentDescription = "Train Stop",
            tint = MaterialTheme.colors.onPrimary
        )

        StarIndicatorView(
            Modifier
                .padding(start = 42.dp, top = 42.dp),
            isStarred = data.starred
        )

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
                text = data.name,
                style = MaterialTheme.typography.h6Bold,
                color = MaterialTheme.colors.onSurface
            )

            Row(
                Modifier.padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = if (data.hasTravelAssistance) {
                        Icons.Rounded.WheelchairPickup
                    } else {
                        Icons.Rounded.NotAccessible
                    },
                    contentDescription = if (data.hasTravelAssistance) {
                        "Has travel assistance"
                    } else {
                        "Does not have travel assistance"
                    },
                    tint = MaterialTheme.colors.onSurface.medium
                )

                if (data.hasFacilities) {
                    Text(
                        text = " • ",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.medium,
//                        modifier = Modifier.padding(start = 8.dp)
                    )

                    Icon(
                        modifier = Modifier
//                            .padding(start = 8.dp)
                            .size(16.dp),
                        imageVector = Icons.Rounded.Wc,
                        contentDescription = "Has facilities",
                        tint = MaterialTheme.colors.onSurface.medium
                    )
                }

                if (data.hasDepartureTimes) {
                    Text(
                        text = " • ",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.medium,
//                        modifier = Modifier.padding(start = 8.dp)
                    )

                    Icon(
                        modifier = Modifier
                            //.padding(start = 8.dp)
                            .size(16.dp),
                        imageVector = Icons.Rounded.Schedule,
                        contentDescription = "Has departure times",
                        tint = MaterialTheme.colors.onSurface.medium
                    )
                }

                Text(
                    text = " • " + data.codeToDisplay.uppercase(Locale.ROOT),
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.medium,
//                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            StarDirectionsView(
                modifier = Modifier
                    .padding(top = 12.dp),
                starred = data.starred,
                onStarToggle = { },
                onGoToClick = { }
            )
        }
    }
}