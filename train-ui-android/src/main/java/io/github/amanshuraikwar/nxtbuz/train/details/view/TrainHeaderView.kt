package io.github.amanshuraikwar.nxtbuz.train.details.view

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessibleForward
import androidx.compose.material.icons.rounded.DoNotDisturbOnTotalSilence
import androidx.compose.material.icons.rounded.PedalBike
import androidx.compose.material.icons.rounded.Power
import androidx.compose.material.icons.rounded.Wc
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.amanshuraikwar.nxtbuz.common.compose.loading
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.disabled
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium
import io.github.amanshuraikwar.nxtbuz.common.compose.util.PreviewSurface
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainFacility
import io.github.amanshuraikwar.nxtbuz.train.details.TrainHeader
import java.util.Locale

@Composable
internal fun TrainHeaderView(
    modifier: Modifier = Modifier,
    data: TrainHeader
) {
    Column(modifier) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp),
            text =
            "${data.trainCategoryName} • ${data.trainCode}".uppercase(Locale.ROOT),
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colors.primary
        )

        Text(
            modifier = Modifier
                .padding(top = 14.dp)
                .padding(horizontal = 16.dp),
            text = data.destinationTrainStopName,
            style = MaterialTheme.typography.h6Bold,
            color = MaterialTheme.colors.onSurface
        )

        Text(
            modifier = Modifier
                .padding(top = 4.dp)
                .padding(horizontal = 16.dp),
            text = "from ${data.sourceTrainStopName}",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface.medium
        )

        if (data.facilities.isNotEmpty()) {
            Row(
                Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (facility in data.facilities) {
                    Icon(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(
                                MaterialTheme.colors.primary,
                                shape = RoundedCornerShape(6.dp),
                            )
                            .padding(2.dp)
                            .size(18.dp),
                        imageVector = when (facility) {
                            TrainFacility.TOILET -> Icons.Rounded.Wc
                            TrainFacility.POWER_SOCKETS -> Icons.Rounded.Power
                            TrainFacility.WIFI -> Icons.Rounded.Wifi
                            TrainFacility.QUIET_TRAIN -> Icons.Rounded.DoNotDisturbOnTotalSilence
                            TrainFacility.BICYCLE -> Icons.Rounded.PedalBike
                            TrainFacility.WHEELCHAIR_ACCESSIBLE -> Icons.Rounded.AccessibleForward
                        },
                        contentDescription = facility.toString(),
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }

        Divider(Modifier.padding(top = 16.dp))

        if (data.rollingStockImages.isNotEmpty()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
            ) {
                Column {
                    Row {
                        Spacer(modifier = Modifier.width(16.dp))

                        for (rollingStockImage in data.rollingStockImages) {
                            AsyncImage(
                                modifier = Modifier
                                    .height(48.dp),
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(rollingStockImage.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentScale = ContentScale.FillHeight,
                                contentDescription = "Rolling stock"
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))
                    }

                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "${data.length} carriages • ${data.lengthInMeters} metres"
                            .uppercase(Locale.ROOT),
                        style = MaterialTheme.typography.overline,
                        color = MaterialTheme.colors.onSurface.medium
                    )

                }
            }

            Divider(Modifier.padding(top = 12.dp))
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light theme")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark theme")
fun TrainHeaderView_Preview() {
    PreviewSurface {
        TrainHeaderView(
            data = TrainHeader(
                trainCode = "1234",
                trainCategoryName = "Intercity",
                sourceTrainStopName = "Amsterdam Centraal",
                destinationTrainStopName = "Haarlem",
                facilities = TrainFacility.values().asList(),
                rollingStockImages = emptyList(),
                length = 1,
                lengthInMeters = 100,
            )
        )
    }
}

@Composable
internal fun TrainHeaderView(
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp)
                .loading(),
            text =
            "                  ".uppercase(Locale.ROOT),
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colors.primary
        )

        Text(
            modifier = Modifier
                .padding(top = 14.dp)
                .padding(horizontal = 16.dp)
                .loading(),
            text = "          ",
            style = MaterialTheme.typography.h6Bold,
            color = MaterialTheme.colors.onSurface
        )

        Text(
            modifier = Modifier
                .padding(top = 4.dp)
                .padding(horizontal = 16.dp)
                .loading(),
            text = "                                     ",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface.medium
        )

        Row(
            Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0..2) {
                Text(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .background(
                            MaterialTheme.colors.onSurface.disabled,
                            shape = RoundedCornerShape(6.dp),
                        )
                        .padding(2.dp)
                        .size(18.dp),
                    text = ""
                )
            }
        }


        Divider(Modifier.padding(top = 16.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
        ) {
            Column {
                Row {
                    Spacer(modifier = Modifier.width(16.dp))

                    for (i in 0..3) {
                        Spacer(
                            modifier = Modifier
                                .height(48.dp)
                                .width(128.dp)
                                .padding(end = 4.dp, top = 16.dp, bottom = 8.dp)
                                .loading(),
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                }

                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .loading(),
                    text = "                        "
                        .uppercase(Locale.ROOT),
                    style = MaterialTheme.typography.overline,
                    color = MaterialTheme.colors.onSurface.medium
                )

            }
        }

        Divider(Modifier.padding(top = 12.dp))
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light theme")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark theme")
fun TrainHeaderViewLoading_Preview() {
    PreviewSurface {
        TrainHeaderView()
    }
}

