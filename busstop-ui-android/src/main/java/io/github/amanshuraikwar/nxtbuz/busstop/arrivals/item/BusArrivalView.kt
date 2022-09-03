package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RemoveCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busstop.R
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.disabled
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.outline
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusLoad
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusType

@ExperimentalAnimationApi
@Composable
fun BusArrivalView(
    arrival: Int,
    busLoad: BusLoad?,
    busType: BusType?,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            border = BorderStroke(1.dp, MaterialTheme.colors.outline),
            shape = RoundedCornerShape(16.dp)
        ) {
            ArrivalTimeView(arrivalInMin = arrival)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Icon(
                painter = if (busType != null) {
                    painterResource(
                        when (busType) {
                            BusType.SD -> R.drawable.ic_bus_normal_16
                            BusType.DD -> R.drawable.ic_bus_dd_16
                            BusType.BD -> R.drawable.ic_bus_feeder_16
                        }
                    )
                } else {
                    rememberVectorPainter(image = Icons.Rounded.RemoveCircle)
                },
                modifier = Modifier
                    .size(16.dp)
                    .let {
                        if (busLoad != null) {
                            it.padding(1.dp)
                        } else {
                            it
                        }
                    },
                contentDescription = "Bus Type",
                tint = MaterialTheme.colors.onSurface.let {
                    if (busType == null) {
                        it.disabled
                    } else {
                        it
                    }
                }
            )

            Icon(
                painter = if (busLoad != null) {
                    painterResource(
                        when (busLoad) {
                            BusLoad.SEA -> R.drawable.ic_bus_load_1_16
                            BusLoad.SDA -> R.drawable.ic_bus_load_2_16
                            BusLoad.LSD -> R.drawable.ic_bus_load_3_16
                        }
                    )
                } else {
                    rememberVectorPainter(image = Icons.Rounded.RemoveCircle)
                },
                modifier = Modifier
                    .padding(start = 1.dp)
                    .size(16.dp),
                contentDescription = "Bus Load",
                tint = MaterialTheme.colors.onSurface.let {
                    if (busLoad == null) {
                        it.disabled
                    } else {
                        it
                    }
                }
            )
        }
    }
}

@Composable
fun BusArrivalView(
    arrival: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = arrival,
            style = MaterialTheme.typography.h6Bold,
            color = MaterialTheme.colors.onSurface.medium,
            modifier = Modifier.animateContentSize()
        )
    }
}
