package io.github.amanshuraikwar.nxtbuz.settings.ui

import io.github.amanshuraikwar.nxtbuz.settings.R
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.NxtBuzTheme
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold

@Composable
fun AboutItem(
    appName: String,
    versionName: String,
) {
    Column(Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(32.dp))

        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            elevation = 2.dp
        ) {
            Icon(
                painter = painterResource(
                    id = R.drawable.ic_bus_72
                ),
                contentDescription = "App Icon",
                tint = MaterialTheme.colors.onPrimary,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = appName,
            style = MaterialTheme.typography.h4,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = versionName,
            style = MaterialTheme.typography.h6Bold,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onSurface
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview
@Composable
fun AboutItemPreview() {
    NxtBuzTheme {
        AboutItem("NxtBuz", "1.3.4")
    }
}

@Preview
@Composable
fun AboutItemPreviewDark() {
    NxtBuzTheme(
        darkTheme = true
    ) {
        AboutItem("NxtBuz", "1.3.4")
    }
}