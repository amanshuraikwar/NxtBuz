package io.github.amanshuraikwar.nxtbuz.commonkmm

import android.content.Context
import android.content.res.Configuration

actual class SystemThemeHelper(
    private val context: Context
) {
    actual fun isSystemInDarkTheme(): Boolean {
        return context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}