package io.github.amanshuraikwar.nxtbuz.common.model

sealed class UserState {
    object New : UserState()
    object SetupComplete : UserState()
}