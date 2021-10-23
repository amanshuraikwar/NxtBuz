package io.github.amanshuraikwar.nxtbuz.onboarding.setup

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.*
import io.github.amanshuraikwar.nxtbuz.onboarding.R

@Composable
fun WelcomeScreen(
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
            Icon(
                painter = painterResource(
                    id = R.drawable.ic_mbs
                ),
                contentDescription = "MBS Icon",
                tint = MaterialTheme.colors.outline,
                modifier = Modifier
                    .alpha(alpha3)
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )

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
                            id = R.drawable.ic_bus_72
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
                    text = "Next Bus SG",
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
                    text = buildAnnotatedString {
                        append("Easily find bus arrival timings anywhere in ")
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colors.singapore,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Singapore")
                        }
                    },
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