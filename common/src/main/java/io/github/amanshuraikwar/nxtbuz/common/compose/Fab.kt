package io.github.amanshuraikwar.nxtbuz.common.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.outline

enum class FabDecorationType {
    OUTLINE,
    SHADOW
}

@Composable
fun Fab(
    modifier: Modifier = Modifier,
    bgColor: Color = MaterialTheme.colors.surface,
    shape: Shape = MaterialTheme.shapes.small,
    elevation: Dp = 4.dp,
    borderWidth: Dp = 1.dp,
    borderColor: Color = MaterialTheme.colors.outline,
    tint: Color = MaterialTheme.colors.onSurface,
    decorationType: FabDecorationType = FabDecorationType.SHADOW,
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        color = bgColor,
        shape = shape,
        elevation = if (decorationType == FabDecorationType.SHADOW) elevation else 0.dp,
        border = if (decorationType == FabDecorationType.OUTLINE) {
            BorderStroke(borderWidth, borderColor)
        } else {
            null
        }
    ) {
        Icon(
            imageVector = imageVector,
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(12.dp)
                .size(24.dp),
            contentDescription = contentDescription,
            tint = tint
        )
    }
}