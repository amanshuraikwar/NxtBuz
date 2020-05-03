package io.github.amanshuraikwar.nxtbuz.ui.main.fragment.model

sealed class AnimateMarker {
    object NoAnimate : AnimateMarker() 
    data class Animate(val duration: Long = 1000) : AnimateMarker() 
}