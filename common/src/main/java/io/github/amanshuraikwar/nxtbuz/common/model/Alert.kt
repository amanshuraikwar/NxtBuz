package io.github.amanshuraikwar.nxtbuz.common.model

import androidx.annotation.DrawableRes
import io.github.amanshuraikwar.nxtbuz.common.R

data class Alert(
    val msg: String = "Something went wrong. Please close and open the app again.",
    @DrawableRes val iconResId: Int = R.drawable.ic_error_128
)