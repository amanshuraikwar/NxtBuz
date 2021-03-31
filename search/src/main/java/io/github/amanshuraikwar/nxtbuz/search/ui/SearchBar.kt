package io.github.amanshuraikwar.nxtbuz.search.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
    screenState: SearchScreenState,
    onSearch: (query: String) -> Unit = {},
    onBackClicked: () -> Unit = {},
    onSettingClicked: () -> Unit = {}
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

        LaunchedEffect(key1 = screenState) {
            if (screenState is SearchScreenState.Nothing) {
                searchString = ""
                keyboardController?.hideSoftwareKeyboard()
            }
        }

        Box {
            if (screenState == SearchScreenState.Nothing) {
                Icon(
                    painter = painterResource(
                        R.drawable.ic_bus_24
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                        .size(24.dp),
                    contentDescription = "Bus",
                    tint = MaterialTheme.colors.primary
                )
            }
            
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

            BasicTextField(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(),
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
            ) { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 56.dp, vertical = 12.dp)
                ) {
                    innerTextField()
                }
            }

            if (screenState != SearchScreenState.Nothing) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colors.onSurface,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clip(shape = MaterialTheme.shapes.small)
                        .clickable {
                            onBackClicked()
                        }
                        .padding(16.dp)
                        .size(24.dp)
                )
            }

            Crossfade(
                modifier = Modifier.align(Alignment.CenterEnd),
                targetState = screenState
            ) { screenState ->
                when (screenState) {
                    is SearchScreenState.Failed,
                    is SearchScreenState.Success -> {
                    }
                    is SearchScreenState.Nothing -> {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colors.onSurface,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .clip(shape = MaterialTheme.shapes.small)
                                .clickable {
                                    onSettingClicked()
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