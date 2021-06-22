package io.github.amanshuraikwar.nxtbuz.common.model.user

sealed class UserState {
    object New : UserState()
    object SetupComplete : UserState()
}