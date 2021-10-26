package io.github.amanshuraikwar.nxtbuz.settings.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.FillFirstRow
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.disabled
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium
import io.github.amanshuraikwar.nxtbuz.settings.ui.model.SettingsItemData

@ExperimentalAnimationApi
@Composable
fun SwitchSettingItem(
    data: SettingsItemData.Switch
) {
    val titleColor by animateColorAsState(
        targetValue = if (data.enabled) {
            MaterialTheme.colors.onSurface
        } else {
            MaterialTheme.colors.onSurface.disabled
        }
    )

    val descriptionColor by animateColorAsState(
        targetValue = if (data.enabled) {
            MaterialTheme.colors.onSurface.medium
        } else {
            MaterialTheme.colors.onSurface.disabled
        }
    )

    Surface(
        modifier = Modifier
            .clickable(enabled = data.enabled) {
                data.onClick(!data.on)
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
                    color = titleColor,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Text(
                    text = if (data.on) {
                        data.onDescription
                    } else {
                        data.offDescription
                    },
                    style = MaterialTheme.typography.body2,
                    color = descriptionColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 4.dp,
                        )
                )
            }

            Switch(
                modifier = Modifier
                    .padding(start = 16.dp),
                checked = data.on,
                onCheckedChange = { newCheckedValue ->
                    data.onClick(newCheckedValue)
                },
                enabled = data.enabled,
                colors = if (data.enabled) {
                    SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colors.primary,
                        uncheckedThumbColor = MaterialTheme.colors.onSurface
                    )
                } else {
                    SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colors.onSurface,
                        uncheckedThumbColor = MaterialTheme.colors.onSurface.medium
                    )
                }
            )
        }
    }
}