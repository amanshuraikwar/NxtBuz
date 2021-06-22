package io.github.amanshuraikwar.nxtbuz.settings.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.NxtBuzTheme
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium
import io.github.amanshuraikwar.nxtbuz.settings.ui.model.SettingsItemData
import kotlinx.coroutines.launch

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
                RadioGroupOptions(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    options = radioGroup.options,
                    selectedIndex = radioGroup.selectedIndex,
                    onClick = radioGroup.onClick
                )
            }
        }
    }
}

@Composable
fun RadioGroupOptions(
    modifier: Modifier,
    options: List<String>,
    selectedIndex: Int,
    onClick: (Int) -> Unit,
) {
    Column(modifier) {
        options.forEachIndexed { index, option ->
            Row(
                modifier = Modifier
                    .clickable(
                        // disable the ripple
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onClick(index)
                    }
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = index == selectedIndex,
                    onClick = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = option,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface
                )
            }

            if (index != options.size - 1) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
@Preview
fun RadioGroupSettingsItemPreview() {
    val scope = rememberCoroutineScope()

    var data by remember {
        mutableStateOf(
            SettingsItemData.RadioGroup(
                title = "Bus Stop Query Limit",
                description = "Maximum number of bus stops fetched while searching.",
                options = listOf("10 KM", "20 KM", "50 KM"),
                selectedIndex = 1,
                onClick = {
                    scope.launch {

                    }
                }
            )
        )
    }

    val initialData = SettingsItemData.RadioGroup(
        title = "Bus Stop Query Limit",
        description = "Maximum number of bus stops fetched while searching.",
        options = listOf("10 KM", "20 KM", "50 KM"),
        selectedIndex = 1,
        onClick = {
            scope.launch {
                data = data.copy(selectedIndex = it)
            }
        }
    )

    data = initialData

    NxtBuzTheme {
        RadioGroupSettingItem(
            radioGroup = data
        )
    }
}

@ExperimentalAnimationApi
@Composable
@Preview
fun RadioGroupSettingsItemPreviewDark() {
    val scope = rememberCoroutineScope()

    var data by remember {
        mutableStateOf(
            SettingsItemData.RadioGroup(
                title = "Bus Stop Query Limit",
                description = "Maximum number of bus stops fetched while searching.",
                options = listOf("10 KM", "20 KM", "50 KM"),
                selectedIndex = 1,
                onClick = {
                    scope.launch {

                    }
                }
            )
        )
    }

    val initialData = SettingsItemData.RadioGroup(
        title = "Bus Stop Query Limit",
        description = "Maximum number of bus stops fetched while searching.",
        options = listOf("10 KM", "20 KM", "50 KM"),
        selectedIndex = 1,
        onClick = {
            scope.launch {
                data = data.copy(selectedIndex = it)
            }
        }
    )

    data = initialData

    NxtBuzTheme(
        darkTheme = true
    ) {
        RadioGroupSettingItem(
            radioGroup = data
        )
    }
}