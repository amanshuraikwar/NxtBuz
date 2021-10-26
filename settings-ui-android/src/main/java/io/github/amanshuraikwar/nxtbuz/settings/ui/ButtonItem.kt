package io.github.amanshuraikwar.nxtbuz.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ButtonItem(
    title: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .padding(top = 2.dp)
            .clickable(onClick = onClick)
            .fillMaxWidth(),
        color = MaterialTheme.colors.surface,
        elevation = 2.dp
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}