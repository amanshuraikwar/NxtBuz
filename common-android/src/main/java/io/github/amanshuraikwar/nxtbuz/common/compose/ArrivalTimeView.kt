package io.github.amanshuraikwar.nxtbuz.common.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.directions
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.disabled
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.outline
import io.github.amanshuraikwar.nxtbuz.common.compose.util.PreviewSurface

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ArrivalTimeView(arrivalInMin: Int) {
    val borderColor by animateColorAsState(
        targetValue = if (arrivalInMin == 0) {
            MaterialTheme.colors.directions
        } else {
            MaterialTheme.colors.outline
        }
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (arrivalInMin == 0) {
            MaterialTheme.colors.directions.copy(alpha = 0.04f)
        } else {
            MaterialTheme.colors.surface
        }
    )

    val textColor by animateColorAsState(
        targetValue = if (arrivalInMin == 0) {
            MaterialTheme.colors.directions
        } else {
            MaterialTheme.colors.onSurface.let {
                if (arrivalInMin < 0) {
                    it.disabled
                } else {
                    it
                }
            }
        }
    )

    Surface(
        border = BorderStroke(
            1.dp,
            borderColor
        ),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        VerticalInOutAnimatedContent(
            targetValue = arrivalInMin
        ) {
            Box(
                modifier = Modifier.padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Arr",
                    style = MaterialTheme.typography.h6Bold,
                    modifier = Modifier
                        .alpha(0f)
                )

                Text(
                    text = arrivalInMin.toArrivalString(),
                    style = MaterialTheme.typography.h6Bold,
                    color = textColor,
                    modifier = Modifier
                        .animateContentSize()
                )
            }
        }
    }
}

private fun Int.toArrivalString(): String {
    return when {
        this < 0 -> "--"
        this > 0 -> String.format("%02d", this)
        else -> "Arr"
    }
}

@Composable
@Preview
private fun ArrivalTimeView_Preview_Light() {
    PreviewSurface(darkTheme = false) {
        Column(
            Modifier.padding(16.dp)
        ) {
            ArrivalTimeView(arrivalInMin = 3)
            Spacer(modifier = Modifier.size(4.dp))
            ArrivalTimeView(arrivalInMin = 0)
            Spacer(modifier = Modifier.size(4.dp))
            ArrivalTimeView(arrivalInMin = -1)
            Spacer(modifier = Modifier.size(4.dp))
            ArrivalTimeView(arrivalInMin = 24)
        }
    }
}

@Composable
@Preview
private fun ArrivalTimeView_Preview_Dark() {
    PreviewSurface(darkTheme = true) {
        Column(
            Modifier.padding(16.dp)
        ) {
            ArrivalTimeView(arrivalInMin = 3)
            Spacer(modifier = Modifier.size(4.dp))
            ArrivalTimeView(arrivalInMin = 0)
            Spacer(modifier = Modifier.size(4.dp))
            ArrivalTimeView(arrivalInMin = -1)
            Spacer(modifier = Modifier.size(4.dp))
            ArrivalTimeView(arrivalInMin = 24)
        }
    }
}