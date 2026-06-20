package io.github.amanshuraikwar.nxtbuz.onboarding.setup

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h4Bold
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium
import io.github.amanshuraikwar.nxtbuz.onboarding.R
import io.github.amanshuraikwar.nxtbuz.common.R as CommonR

@Composable
internal fun WelcomeScreenView(
    versionName: String = "",
    content: @Composable ColumnScope.() -> Unit,
) {
    var alpha1 by remember { mutableStateOf(0f) }
    var alpha2 by remember { mutableStateOf(0f) }
    var alpha3 by remember { mutableStateOf(0f) }

    LaunchedEffect(key1 = null) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(300, delayMillis = 300)
        ) { value, _ ->
            alpha1 = value
        }

        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(300)
        ) { value, _ ->
            alpha2 = value
        }

        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(300)
        ) { value, _ ->
            alpha3 = value
        }
    }

    Surface(
        Modifier
            .fillMaxSize(),
        color = MaterialTheme.colors.surface
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Column {
                Spacer(modifier = Modifier.height(128.dp))

                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .alpha(alpha1)
                        .align(Alignment.CenterHorizontally),
                    elevation = 2.dp
                ) {
                    Icon(
                        painter = painterResource(
                            id = CommonR.drawable.ic_bus_72
                        ),
                        contentDescription = "App Icon",
                        tint = MaterialTheme.colors.onPrimary,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(48.dp)
                    )
                }

                Spacer(Modifier.height(48.dp))

                Text(
                    modifier = Modifier
                        .alpha(alpha2)
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    text = stringResource(id = CommonR.string.app_name),
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.h4Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    modifier = Modifier
                        .alpha(alpha2)
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .padding(horizontal = 32.dp),
                    text = stringResource(id = CommonR.string.on_boarding_app_description),
                    color = MaterialTheme.colors.onSurface.medium,
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier
                    .alpha(alpha3)
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .navigationBarsPadding()
                    .align(Alignment.BottomCenter)
            ) {
                this.content()

                Spacer(Modifier.height(16.dp))

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    text = versionName.uppercase(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}
