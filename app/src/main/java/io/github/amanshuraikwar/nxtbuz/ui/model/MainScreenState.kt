package io.github.amanshuraikwar.nxtbuz.ui.model

sealed class MainScreenState {
    object Fetching : MainScreenState()
    object Setup : MainScreenState()
    data class Success(
        val showMap: Boolean,
        val navigationState: NavigationState,
        val showBackBtn: Boolean,
    ) : MainScreenState()
}

