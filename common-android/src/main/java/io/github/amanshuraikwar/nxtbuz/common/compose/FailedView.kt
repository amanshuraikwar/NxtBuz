package io.github.amanshuraikwar.nxtbuz.common.compose

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold

@Composable
fun FailedView(
    modifier: Modifier = Modifier,
    onRetryClicked: () -> Unit = {},
) {
    Column(
            modifier
    ) {
        Box(
            Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 16.dp, end = 72.dp)
            ) {
                Text(
                    text = "Something went wrong",
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.h6Bold,
                )

                Text(
                    text = "Please try again",
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 16.dp, top = 24.dp),
                color = MaterialTheme.colors.error,
                shape = MaterialTheme.shapes.small
            ) {
                Icon(
                    imageVector = Icons.Rounded.Error,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(32.dp),
                    contentDescription = "Error",
                    tint = MaterialTheme.colors.onError
                )
            }
        }

        CompositionLocalProvider(
            LocalIndication provides rememberRipple(color = MaterialTheme.colors.error)
        ) {
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 16.dp, end = 16.dp),
                onClick = onRetryClicked,
            ) {
                Text(
                    "RETRY",
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = MaterialTheme.colors.error
                )
            }
        }
    }
}