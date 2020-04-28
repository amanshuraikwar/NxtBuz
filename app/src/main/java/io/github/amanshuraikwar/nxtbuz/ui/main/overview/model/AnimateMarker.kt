package io.github.amanshuraikwar.nxtbuz.ui.main.overview.model

sealed class AnimateMarker {
    object NoAnimate : AnimateMarker() 
    data class Animate(val duration: Long = 1000) : AnimateMarker() 
}