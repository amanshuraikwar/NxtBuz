package io.github.amanshuraikwar.nxtbuz.ui.main.fragment

import androidx.annotation.DrawableRes

sealed class Loading {
    data class Show(@DrawableRes val avd: Int, val txt: String) : Loading()
    object Hide : Loading()
}