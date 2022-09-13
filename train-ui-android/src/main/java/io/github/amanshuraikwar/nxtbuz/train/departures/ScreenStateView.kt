package io.github.amanshuraikwar.nxtbuz.train.departures

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Train
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.FetchingView
import io.github.amanshuraikwar.nxtbuz.common.compose.StarIndicatorView
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold
import java.util.Locale

@Composable
internal fun ScreenStateView(
    screenState: ScreenState,
    padding: PaddingValues = PaddingValues(),
    backgroundColor: Color,
) {
    when (screenState) {
        ScreenState.Fetching -> {
            Column {
                Divider()

                FetchingView()
            }
        }
        is ScreenState.Success -> {
            Column {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colors.surface)
                        .fillMaxWidth(),
                ) {
                    Box(
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Surface(
                            modifier = Modifier
                                .padding(
                                    start = 16.dp,
                                    top = 16.dp,
                                    bottom = 16.dp,
                                    end = 16.dp
                                ),
                            color = MaterialTheme.colors.primary,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Train,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(24.dp),
                                contentDescription = "Train Stop",
                                tint = MaterialTheme.colors.onPrimary
                            )
                        }

                        StarIndicatorView(
                            Modifier
                                .padding(bottom = 12.dp, end = 12.dp),
                            isStarred = screenState.header.starred
                        )
                    }

                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 16.dp,
                                start = 72.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            )
                    ) {
                        Text(
                            text = screenState.header.name,
                            style = MaterialTheme.typography.h6Bold,
                            color = MaterialTheme.colors.onSurface
                        )

                        Text(
                            text = screenState.header.code.uppercase(Locale.ROOT),
                            style = MaterialTheme.typography.overline,
                            color = MaterialTheme.colors.onSurface,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Divider()

                DeparturesView(
                    listItems = screenState.listItems
                )
            }
        }
    }
}