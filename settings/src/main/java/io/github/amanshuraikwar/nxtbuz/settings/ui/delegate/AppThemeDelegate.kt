package io.github.amanshuraikwar.nxtbuz.settings.ui.delegate

import io.github.amanshuraikwar.nxtbuz.common.model.NxtBuzTheme
import kotlinx.coroutines.flow.StateFlow

interface AppThemeDelegate {
    val theme: StateFlow<NxtBuzTheme>

    suspend fun refreshTheme()
}