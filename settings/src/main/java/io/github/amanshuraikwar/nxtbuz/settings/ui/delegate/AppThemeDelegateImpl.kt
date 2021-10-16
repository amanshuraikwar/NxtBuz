package io.github.amanshuraikwar.nxtbuz.settings.ui.delegate

import io.github.amanshuraikwar.nxtbuz.domain.user.GetThemeUseCase
import io.github.amanshuraikwar.nxtbuz.domain.user.RefreshThemeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class AppThemeDelegateImpl @Inject constructor(
    private val getThemeUseCase: GetThemeUseCase,
    private val refreshThemeUseCase: RefreshThemeUseCase,
) : AppThemeDelegate {
    private val _theme = MutableStateFlow(getThemeUseCase())

    override val theme = _theme.asStateFlow()

    override suspend fun refreshTheme() {
        refreshThemeUseCase()
        _theme.value = getThemeUseCase()
    }
}