package io.github.amanshuraikwar.nxtbuz.search.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.outline
import io.github.amanshuraikwar.nxtbuz.search.R

enum class SearchBarDecorationType {
    OUTLINE,
    SHADOW
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onSearch: (query: String) -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    Surface(
        modifier,
        color = MaterialTheme.colors.surface,
        elevation = 8.dp
    ) {
        var searchString by remember {
            mutableStateOf("")
        }

        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        Box(
            Modifier.statusBarsPadding()
        ) {
            if (searchString.isEmpty()) {
                Text(
                    text = "Search for Bus Stops...",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxWidth()
                        .padding(horizontal = 72.dp, vertical = 12.dp),
                    color = MaterialTheme.colors.onSurface.medium,
                    style = MaterialTheme.typography.subtitle1,
                    maxLines = 1
                )
            }

            BasicTextField(
                modifier = Modifier
                    .focusRequester(focusRequester)
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
                    }
                ),
            ) { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 72.dp, vertical = 12.dp)
                ) {
                    innerTextField()
                }
            }

            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clip(shape = MaterialTheme.shapes.small)
                    .clickable {
                        onBackClick()
                    }
                    .padding(16.dp)
                    .size(24.dp)
            )

            Icon(
                imageVector = Icons.Rounded.Clear,
                contentDescription = "Clear",
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clip(shape = MaterialTheme.shapes.small)
                    .clickable {
                        searchString = ""
                    }
                    .padding(16.dp)
                    .size(24.dp)
            )
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onSettingsClicked: () -> Unit = {},
    decorationType: SearchBarDecorationType = SearchBarDecorationType.SHADOW,
    showAppIcon: Boolean = true,
) {
    val textPaddingStart by animateDpAsState(
        targetValue = if (showAppIcon) {
            56.dp
        } else {
            16.dp
        }
    )
    Surface(
        modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colors.surface,
        elevation = if (decorationType == SearchBarDecorationType.SHADOW) 4.dp else 0.dp,
        border = if (decorationType == SearchBarDecorationType.OUTLINE) {
            BorderStroke(1.dp, MaterialTheme.colors.outline)
        } else {
            null
        }
    ) {
        Box(
            Modifier.clickable {
                onClick()
            }
        ) {
            if (showAppIcon) {
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

            Text(
                text = "Search for Bus Stops...",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .padding(start = textPaddingStart, end = 56.dp),
                color = MaterialTheme.colors.onSurface.medium,
                style = MaterialTheme.typography.subtitle1,
                maxLines = 1
            )

            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clip(shape = MaterialTheme.shapes.small)
                    .clickable {
                        onSettingsClicked()
                    }
                    .padding(16.dp)
                    .size(24.dp)
            )
        }
    }
}