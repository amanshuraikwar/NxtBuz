package io.github.amanshuraikwar.nxtbuz.train.details

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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainFacility
import java.util.Locale

@Composable
internal fun TrainHeaderView(
    modifier: Modifier = Modifier,
    data: TrainHeader
) {
    Column {
        Text(
            modifier = modifier
                .background(MaterialTheme.colors.surface)
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
