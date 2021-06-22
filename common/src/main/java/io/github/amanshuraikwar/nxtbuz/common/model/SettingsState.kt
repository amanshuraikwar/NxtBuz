package io.github.amanshuraikwar.nxtbuz.common.model

sealed class SettingsState {
    object Enabled : SettingsState()
    object Resolvable: SettingsState()
    object UserCancelled: SettingsState()
    data class UnResolvable(val reason: String): SettingsState()
}