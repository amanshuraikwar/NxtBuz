package io.github.amanshuraikwar.nxtbuz.common.model.view

import androidx.annotation.StringRes
import io.github.amanshuraikwar.nxtbuz.common.R

data class Error(
    @StringRes val errorTitle: Int = R.string.error_title_default,
    @StringRes val errorDescription: Int = R.string.error_description_default,
)