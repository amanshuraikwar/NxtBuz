package io.github.amanshuraikwar.nxtbuz.commonkmm.user

sealed class SetupState {
    data class InProgress(val progress: Double) : SetupState()
    object Complete : SetupState()
}