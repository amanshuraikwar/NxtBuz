package io.github.amanshuraikwar.nxtbuz.settings.ui.model

sealed class SettingsItemData(
    val id: String
) {
    data class Header(
        val title: String
    ) : SettingsItemData(title)

    data class About(
        val appName: String,
        val versionName: String,
    ) : SettingsItemData("about")

    class RadioGroup(
        id: String,
        val title: String,
        val description: String,
        val options: List<String>,
        val selectedIndex: Int,
        val onClick: (optionIndex: Int) -> Unit,
    ) : SettingsItemData(id) {
        fun copy(selectedIndex: Int): RadioGroup {
            return RadioGroup(
                id = id,
                title = title,
                description = description,
                options = options,
                selectedIndex = selectedIndex,
                onClick = onClick
            )
        }

    }

    class Switch(
        id: String,
        val title: String,
        val onDescription: String,
        val offDescription: String,
        val on: Boolean,
        val enabled: Boolean = true,
        val onClick: (newValue: Boolean) -> Unit,
    ) : SettingsItemData(id) {
        fun copy(on: Boolean, enabled: Boolean): Switch {
            return Switch(
                id, title, onDescription, offDescription, on, enabled, onClick
            )
        }
    }

    object Oss : SettingsItemData("oss")

    object RequestFeature : SettingsItemData("request-feature")

    object MadeBy : SettingsItemData("made-by")

    object RateOnPlayStore : SettingsItemData("rate-on-play-store")

    object MadeWith : SettingsItemData("made-with")
}