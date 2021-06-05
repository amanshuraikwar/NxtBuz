package io.github.amanshuraikwar.nxtbuz.settings.ui.model

sealed class SettingsItemData {
    data class Header(
        val title: String
    ) : SettingsItemData()
    data class About(
        val appName: String,
        val versionName: String,
    ) : SettingsItemData()

    data class RadioGroup(
        val title: String,
        val description: String,
        val options: List<String>,
        val selectedIndex: Int,
        val onClick: (optionIndex: Int) -> Unit,
    ) : SettingsItemData()

    data class Switch(
        val title: String,
        val enabledDescription: String,
        val disabledDescription: String,
        val enabled: Boolean,
        val onClick: (newValue: Boolean) -> Unit,
    ) : SettingsItemData()
}