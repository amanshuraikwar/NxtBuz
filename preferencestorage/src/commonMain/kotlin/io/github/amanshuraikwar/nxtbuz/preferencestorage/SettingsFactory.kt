package io.github.amanshuraikwar.nxtbuz.preferencestorage

expect class SettingsFactory {
    fun createPreferenceStorage(): PreferenceStorage
    companion object {
        fun prefOnboardingKey(): String
    }
}