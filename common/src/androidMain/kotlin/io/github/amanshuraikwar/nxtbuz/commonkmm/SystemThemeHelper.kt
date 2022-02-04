package io.github.amanshuraikwar.nxtbuz.commonkmm

import android.content.Context
import android.content.res.Configuration

class AndroidSystemThemeHelper(
    private val context: Context
) : SystemThemeHelper {
    override fun isSystemInDarkTheme(): Boolean {
        return context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}