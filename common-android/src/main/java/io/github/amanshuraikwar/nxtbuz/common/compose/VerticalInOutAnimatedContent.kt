package io.github.amanshuraikwar.nxtbuz.common.compose

import androidx.compose.animation.*
import androidx.compose.runtime.Composable

@ExperimentalAnimationApi
@Composable
fun VerticalInOutAnimatedContent(
    targetValue: Int,
    content: @Composable (value: Int) -> Unit
) {
    AnimatedContent(
        targetState = targetValue,
        transitionSpec = {
            if (targetState > initialState) {
                slideInVertically({ it }) + fadeIn() with
                        slideOutVertically({ -it }) + fadeOut()
            } else {
                slideInVertically({ -it }) + fadeIn() with
                        slideOutVertically({ it }) + fadeOut()
            }.using(SizeTransform(clip = false))
        },
    ) {
        content(targetValue)
    }
}