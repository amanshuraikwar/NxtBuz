package io.github.amanshuraikwar.nxtbuz.common.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.util.PreviewSurface

/**
 * Displays single selectable radio group options.
 *
 * @param modifier compose modifier applied before all internal modifiers
 * @param options map of option -> display string
 * @param selectedOption current selected option
 * @param onOptionClick called when an option is clicked, use this to update selected option
 */
@Composable
fun <T> RadioGroupOptionsView(
    modifier: Modifier = Modifier,
    options: Map<T, String>,
    selectedOption: T,
    onOptionClick: (T) -> Unit,
) {
    Column(modifier) {
        options.toList()
            .forEachIndexed { index, (option, displayString) ->
                Row(
                    modifier = Modifier
                        .clickable(
                            // disable the ripple
                            indication = null,
                            interactionSource = remember {
                                MutableInteractionSource()
                            }
                        ) {
                            onOptionClick(option)
                        }
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = option == selectedOption,
                        onClick = null
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = displayString,
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

@Preview
@Composable
fun RadioGroupOptionsView_Preview() {
    PreviewSurface {
        RadioGroupOptionsView(
            options = mapOf(
                0 to "Netherlands",
                1 to "Singapore"
            ),
            selectedOption = 0,
            onOptionClick = {}
        )
    }
}