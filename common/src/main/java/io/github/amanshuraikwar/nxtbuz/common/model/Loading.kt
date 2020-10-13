package io.github.amanshuraikwar.nxtbuz.common.model

import androidx.annotation.DrawableRes

sealed class Loading {
    data class Show(@DrawableRes val avd: Int, val txt: String) : Loading()
    object Hide : Loading()
}