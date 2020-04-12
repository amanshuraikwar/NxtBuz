package io.github.amanshuraikwar.nxtbuz.data.user.model

sealed class UserState {
    object New : UserState()
    object SetupComplete : UserState()
}