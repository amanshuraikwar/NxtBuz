package io.github.amanshuraikwar.nxtbuz.data.user

sealed class UserState {
    object New : UserState()
    object SetupComplete : UserState()
}