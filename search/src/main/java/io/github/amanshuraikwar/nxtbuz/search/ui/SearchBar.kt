package io.github.amanshuraikwar.nxtbuz.search.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium
import io.github.amanshuraikwar.nxtbuz.search.R
import io.github.amanshuraikwar.nxtbuz.search.ui.model.SearchScreenState

@ExperimentalComposeUiApi
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    screenState: SearchScreenState?,
    onSearch: (query: String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    Surface(
        modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colors.surface,
        elevation = 8.dp
    ) {
        var searchString by rememberSaveable {
            mutableStateOf("")
        }

        val keyboardController = LocalSoftwareKeyboardController.current

        Box {
            Crossfade(
                modifier = Modifier.align(Alignment.CenterStart),
                targetState = screenState
            ) { screenState ->
                when (screenState) {
                    is SearchScreenState.Failed,
                    is SearchScreenState.Success -> {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colors.onSurface,
                            modifier = Modifier
                                .clip(shape = CircleShape)
                                .clickable {
                                    searchString = ""
                                    onBack()
                                }
                                .padding(16.dp)
                                .size(24.dp)
                        )
                    }
                    null -> {
                        Icon(
                            painter = painterResource(
                                R.drawable.ic_bus_24
                            ),
                            modifier = Modifier
                                .padding(vertical = 12.dp, horizontal = 16.dp)
                                .size(24.dp),
                            contentDescription = "Bus",
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }
            }

            BasicTextField(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth()
                    .padding(horizontal = 56.dp, vertical = 12.dp),
                value = searchString,
                onValueChange = { newValue ->
                    searchString = newValue
                },
                textStyle = MaterialTheme.typography.subtitle1.merge(
                    TextStyle(
                        color = MaterialTheme.colors.onSurface
                    )
                ),
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colors.primary),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    autoCorrect = false,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearch(searchString)
                        keyboardController?.hideSoftwareKeyboard()
                    }
                )
            )

            if (searchString.isEmpty()) {
                Text(
                    text = "Search for Bus Stops...",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxWidth()
                        .padding(horizontal = 56.dp, vertical = 12.dp),
                    color = MaterialTheme.colors.onSurface.medium,
                    style = MaterialTheme.typography.subtitle1,

                    )
            }

            Crossfade(
                modifier = Modifier.align(Alignment.CenterEnd),
                targetState = screenState
            ) { screenState ->
                when (screenState) {
                    is SearchScreenState.Failed,
                    is SearchScreenState.Success -> { }
                    null -> {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colors.onSurface,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .clip(shape = CircleShape)
                                .clickable {

                                }
                                .padding(16.dp)
                                .size(24.dp)
                        )
                    }
                }
            }

        }
    }
}