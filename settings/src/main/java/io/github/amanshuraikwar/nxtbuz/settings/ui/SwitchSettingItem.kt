package io.github.amanshuraikwar.nxtbuz.settings.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.NxtBuzTheme
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.disabled
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium
import io.github.amanshuraikwar.nxtbuz.settings.ui.model.SettingsItemData
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@Composable
fun SwitchSettingItem(
    data: SettingsItemData.Switch
) {
    Surface(
        modifier = Modifier
            .clickable {
                data.onClick(!data.enabled)
            }
            .fillMaxWidth(),
        color = MaterialTheme.colors.surface,
        elevation = 2.dp
    ) {
        FillFirstRow(
            Modifier
                .animateContentSize()
                .fillMaxWidth()
                .padding(
                    top = 16.dp,
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
        ) {
            Column(
                Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Text(
                    text = if (data.enabled) {
                        data.enabledDescription
                    } else {
                        data.disabledDescription
                    },
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 8.dp,
                        )
                )
            }

            Switch(
                modifier = Modifier
                    .padding(start = 16.dp),
                checked = data.enabled,
                onCheckedChange = { newCheckedValue ->
                    data.onClick(newCheckedValue)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colors.primary,
                    uncheckedThumbColor = MaterialTheme.colors.onSurface
                )
            )
        }
    }
}

@Composable
fun FillFirstRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val placeable2 = measurables[1].measure(constraints.copy(minWidth = 0))
        val placeable1 = measurables[0].measure(
            constraints.copy(
                minWidth = constraints.maxWidth - placeable2.width,
                maxWidth = constraints.maxWidth - placeable2.width
            )
        )

        val width = constraints.maxWidth
        val height = placeable1.height.coerceAtLeast(placeable2.height)

        layout(width = width, height = height) {
            placeable1.place(x = 0, y = (height - placeable1.height) / 2)
            placeable2.place(x = placeable1.width, y = (height - placeable2.height) / 2)
        }
    }
}


@ExperimentalAnimationApi
@Composable
@Preview
fun SwitchSettingItemPreview() {
    val scope = rememberCoroutineScope()

    var data by remember {
        mutableStateOf(
            SettingsItemData.Switch(
                title = "Show starred buses that are not arriving",
                enabledDescription = "Starred buses that are not arriving will be shown on the home screen",
                disabledDescription = "Only starred buses that are arriving will be shown on the home screen",
                enabled = true,
                onClick = {

                }
            )
        )
    }

    val initialData = SettingsItemData.Switch(
        title = "Show starred buses that are not arriving",
        enabledDescription = "Starred buses that are not arriving will be shown on the home screen",
        disabledDescription = "Only starred buses that are arriving will be shown on the home screen",
        enabled = true,
        onClick = {
            scope.launch {
                data = data.copy(enabled = it)
            }
        }
    )

    data = initialData

    NxtBuzTheme {
        SwitchSettingItem(
            data = data
        )
    }
}

@ExperimentalAnimationApi
@Composable
@Preview
fun SwitchSettingItemPreviewDark() {
    val scope = rememberCoroutineScope()

    var data by remember {
        mutableStateOf(
            SettingsItemData.Switch(
                title = "Show starred buses that are not arriving",
                enabledDescription = "Starred buses that are not arriving will be shown on the home screen",
                disabledDescription = "Only starred buses that are arriving will be shown on the home screen",
                enabled = false,
                onClick = {

                }
            )
        )
    }

    val initialData = SettingsItemData.Switch(
        title = "Show starred buses that are not arriving",
        enabledDescription = "Starred buses that are not arriving will be shown on the home screen",
        disabledDescription = "Only starred buses that are arriving will be shown on the home screen",
        enabled = false,
        onClick = {
            scope.launch {
                data = data.copy(enabled = it)
            }
        }
    )

    data = initialData

    NxtBuzTheme(darkTheme = true) {
        SwitchSettingItem(
            data = data
        )
    }
}