package io.github.amanshuraikwar.nxtbuz.common.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.onStar
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.star

@Composable
fun StarIndicatorView(
    modifier: Modifier = Modifier,
    isStarred: Boolean
) {
    AnimatedVisibility(
        modifier = modifier
            .size(16.dp),
        visible = isStarred,
        enter = expandIn(expandFrom = Alignment.Center)
                + fadeIn(),
        exit = shrinkOut(shrinkTowards = Alignment.Center)
                + fadeOut()
    ) {
        Surface(
            color = MaterialTheme.colors.star,
            shape = CircleShape,
            elevation = 1.dp
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                modifier = Modifier
                    .size(16.dp)
                    .padding(start = 1.dp, end = 2.dp)
                    .padding(vertical = 2.dp),
                contentDescription = "Starred",
                tint = MaterialTheme.colors.onStar
            )
        }
    }
}