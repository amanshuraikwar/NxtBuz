package io.github.amanshuraikwar.nxtbuz.settings.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.RadioGroupOptionsView
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium
import io.github.amanshuraikwar.nxtbuz.settings.ui.model.SettingsItemData

@ExperimentalAnimationApi
@Composable
fun RadioGroupSettingItem(
    radioGroup: SettingsItemData.RadioGroup
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

    Surface(
        modifier = Modifier
            .clickable {
                expanded = !expanded
            }
            .fillMaxWidth(),
        color = MaterialTheme.colors.surface,
        elevation = 2.dp
    ) {
        Column(
            Modifier
                .animateContentSize()
                .fillMaxWidth(),
        ) {
            Box(
                Modifier.fillMaxWidth(),
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            end = 40.dp,
                        ),
                ) {
                    Text(
                        text = radioGroup.title,
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 16.dp,
                                top = 16.dp
                            )
                    )

                    Text(
                        text = radioGroup.description,
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 16.dp,
                                top = 4.dp,
                                bottom = 16.dp,
                            )
                    )
                }

                Icon(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                        .size(24.dp)
                        .rotate(rotation),
                    imageVector = Icons.Rounded.ExpandMore,
                    contentDescription = "expanded icon",
                    tint = MaterialTheme.colors.onSurface.medium,
                )
            }

            if (expanded) {
                RadioGroupOptionsView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    options = buildMap {
                        radioGroup.options.forEachIndexed { index, option ->
                            put(index, option)
                        }
                    },
                    selectedOption = radioGroup.selectedIndex,
                    onOptionClick = radioGroup.onClick
                )
            }
        }
    }
}