package io.github.amanshuraikwar.nxtbuz.map.ui.recenter

sealed class RecenterButtonState {
    object LocationAvailable : RecenterButtonState()
    object LocationNotAvailable : RecenterButtonState()
}