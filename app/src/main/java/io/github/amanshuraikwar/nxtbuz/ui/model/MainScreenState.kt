package io.github.amanshuraikwar.nxtbuz.ui.model

sealed class MainScreenState {
    object Fetching : MainScreenState()
    data class Success(
        val showMap: Boolean,
        val navigationState: NavigationState,
    ) : MainScreenState()
}

