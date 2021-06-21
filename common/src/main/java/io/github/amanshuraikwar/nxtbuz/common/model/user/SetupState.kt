package io.github.amanshuraikwar.nxtbuz.common.model.user

import androidx.annotation.FloatRange

sealed class SetupState {
    data class InProgress(@FloatRange(from = 0.0, to = 1.0) val progress: Double) : SetupState()
    object Complete : SetupState()
}