package io.github.amanshuraikwar.nxtbuz.common.model.map

sealed class AnimateMarker {
    object NoAnimate : AnimateMarker()
    data class Animate(val duration: Long = 1000) : AnimateMarker()
}