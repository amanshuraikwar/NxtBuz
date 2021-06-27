package io.github.amanshuraikwar.nxtbuz.onboarding.setup

sealed class SetupScreenState {
    object Fetching : SetupScreenState()
    object SetupComplete : SetupScreenState()
    data class Error(val message: String) : SetupScreenState()
    data class InProgress(val progress: Int) : SetupScreenState()
}