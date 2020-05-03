package io.github.amanshuraikwar.nxtbuz.ui.main.fragment.model

import androidx.annotation.DrawableRes
import io.github.amanshuraikwar.nxtbuz.R

data class Alert(
    val msg: String = "Something went wrong. Please close and open the app again.",
    @DrawableRes val iconResId: Int = R.drawable.ic_error_128
)