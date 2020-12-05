package io.github.amanshuraikwar.nxtbuz.common.model

import androidx.annotation.DrawableRes

sealed class Loading {
    data class Show(@DrawableRes val avdResId: Int, val messageResId: Int) : Loading()
    object Hide : Loading()
}